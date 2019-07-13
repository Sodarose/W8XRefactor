package analysis.rule;

import analysis.AbstractRuleVisitor;
import analysis.process.Analysis;
import api.AnalysisApi;
import com.github.javaparser.JavaToken;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.AnnotationDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserClassDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserInterfaceDeclaration;
import com.github.javaparser.symbolsolver.model.resolution.SymbolReference;
import model.Issue;
import model.IssueContext;
import model.JavaModel;
import model.Store;
import refactor.refactorimpl.OverwriteMethodRefactor;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OverwriteMethodRule extends AbstractRuleVisitor {
    @Override
    public IssueContext apply(List<JavaModel> javaModels) {
        for (JavaModel javaModel : javaModels) {
            checkMethod(javaModel);
        }
        return getContext();
    }


    /**
     * 检查函数是否是继承或者实现的函数
     */
    private void checkMethod(JavaModel javaModel) {
        CompilationUnit unit = javaModel.getUnit();

        //拿到所有的类声明
        List<ClassOrInterfaceDeclaration> classOrInterfaceDeclarations =
                unit.findAll(ClassOrInterfaceDeclaration.class);


        //遍历类声明
        for (ClassOrInterfaceDeclaration clazz : classOrInterfaceDeclarations) {
            collectParentMethod(javaModel, clazz);

            //拿到当前类的接口的所有方法声明 递归查找父类方法
            //collectParentMethod(clazz, interfaceMethods);
            //处理
            //solveMethods(javaModel, methodDeclarations, interfaceMethods);
        }
    }


    /**
     * 得到一个类的其实现的接口或继承的父类的所有方法 向上递归
     */
    private void collectParentMethod(JavaModel javaModel, ClassOrInterfaceDeclaration clazz) {
        //拿到当前类的所有方法声明
        List<MethodDeclaration> methodDeclarations = clazz.findAll(MethodDeclaration.class);
        Set<MethodDeclaration> interfaceMethods = new HashSet<>();
        //将当前类的所有类声明
        List<ClassOrInterfaceType> declarations = clazz.getImplementedTypes();
        declarations.addAll(clazz.getExtendedTypes());
        declarations.stream().forEach(clazzType -> {
            collectMethod(clazzType, interfaceMethods);
        });
        compareMethod(javaModel, methodDeclarations, interfaceMethods);
    }

    /**
     * 递归收集
     */
    private void collectMethod(ClassOrInterfaceType classType, Set<MethodDeclaration> interfaceMethods) {
        //得到完成名称
        String qualifiedName = null;
        try {
            qualifiedName = Store.javaParserFacade.getSymbolSolver().solveType(classType).getQualifiedName();
        } catch (UnsolvedSymbolException u) {

        }

        if (qualifiedName == null) {
            return;
        }

        //System.out.println(Store.combinedTypeSolver.tryToSolveType(qualifiedName));
        SymbolReference<ResolvedReferenceTypeDeclaration> result = Store.
                combinedTypeSolver.tryToSolveType(qualifiedName);
        //查看是否解析成功
        if (!result.isSolved()) {
            return;
        }

        //获得当前类 不管是接口还是类
        ClassOrInterfaceDeclaration clazz = null;
        if (result.getCorrespondingDeclaration() instanceof JavaParserInterfaceDeclaration) {
            JavaParserInterfaceDeclaration interfaceDeclaration = (JavaParserInterfaceDeclaration) result
                    .getCorrespondingDeclaration();
            clazz = interfaceDeclaration.getWrappedNode();
        }

        if (result.getCorrespondingDeclaration() instanceof JavaParserClassDeclaration) {
            JavaParserClassDeclaration classDeclaration = (JavaParserClassDeclaration)
                    result.getCorrespondingDeclaration();
            clazz = classDeclaration.getWrappedNode();
        }

        if (clazz == null) {
            return;
        }
        interfaceMethods.addAll(clazz.getMethods());
        List<ClassOrInterfaceType> declarations = clazz.getImplementedTypes();
        declarations.addAll(clazz.getExtendedTypes());
        declarations.stream().forEach(clazzType -> {
            collectMethod(clazzType, interfaceMethods);
        });
    }

    /**
     * 找到被实现的方法
     */
    private void compareMethod(JavaModel javaModel, List<MethodDeclaration> clazzMethods, Set<MethodDeclaration> overMethods) {
        //去重
        Set<MethodDeclaration> methodDeclarationSet = new HashSet<>();
        for (MethodDeclaration clazzMethod : clazzMethods) {
            for (MethodDeclaration overMethod : overMethods) {
                //不匹配
                if (!compare(clazzMethod, overMethod)) {
                    continue;
                }
                //判断是否有Override注解
                if (isHasOverrideAnnotation(clazzMethod)) {
                    continue;
                }
                methodDeclarationSet.add(clazzMethod);
                break;
            }
        }
        //生成issue
        for (MethodDeclaration method : methodDeclarationSet) {
            getContext().getIssues().add(createIssue(javaModel, method));
        }
    }

    /**
     * 判断是否有Override注解
     */
    private boolean isHasOverrideAnnotation(MethodDeclaration methodDeclaration) {
        List<AnnotationExpr> annotationExprs = methodDeclaration.getAnnotations();
        if (annotationExprs.size() == 0) {
            return false;
        }

        //遍历注解列表 是否有Override注解
        for (AnnotationExpr expr : annotationExprs) {
            if ("Override".equals(expr.getName().asString())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 比较方法是否一样
     */
    private boolean compare(MethodDeclaration methodDeclaration, MethodDeclaration method) {
        //返回值是否一样
        if (!methodDeclaration.getType().asString().equals(method.getType().asString())) {
            return false;
        }
        //方法名是否一样
        if (!methodDeclaration.getName().getIdentifier().equals(method.getName().getIdentifier())) {
            return false;
        }

        List<Parameter> classParameters = methodDeclaration.getParameters();
        List<Parameter> parameters = method.getParameters();
        if (classParameters.size() != parameters.size()) {
            return false;
        }

        //检查参数位置和元素释放一样
        for (Parameter classParameter : classParameters) {
            for (Parameter parameter : parameters) {
                if (!classParameter.equals(parameter)) {
                    return false;
                }
            }
        }
        return true;
    }

    //返回坏味道区域
    private Issue createIssue(JavaModel javaModel, Node node) {
        Issue issue = new Issue();
        issue.setIssueNode(node);
        issue.setRuleName(getRuleName());
        issue.setJavaModel(javaModel);
        issue.setDescription(getDescription());
        issue.setRefactorName(getSolutionClassName());
        return issue;
    }
}
