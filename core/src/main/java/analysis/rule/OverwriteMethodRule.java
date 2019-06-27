package analysis.rule;

import analysis.AbstractRuleVisitor;
import analysis.process.Analysis;
import api.AnalysisApi;
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
import com.github.javaparser.resolution.declarations.ResolvedTypeDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserClassDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserInterfaceDeclaration;
import io.FileUlits;
import model.Issue;
import model.IssueContext;
import model.JavaModel;
import model.Store;

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

    private void checkMethod(JavaModel javaModel) {
        CompilationUnit unit = javaModel.getUnit();
        //拿到所有的类声明
        List<ClassOrInterfaceDeclaration> classOrInterfaceDeclarations =
                unit.findAll(ClassOrInterfaceDeclaration.class);
        //遍历类声明
        for (ClassOrInterfaceDeclaration clazz : classOrInterfaceDeclarations) {
            //拿到当前类的所有方法声明
            List<MethodDeclaration> methodDeclarations = clazz.findAll(MethodDeclaration.class);
            List<MethodDeclaration> interfaceMethods = new ArrayList<>();
            //拿到当前类的接口的所有方法声明 递归查找父类方法
            collectParentMethod(clazz, interfaceMethods);
            //处理
            solveMethods(javaModel, methodDeclarations, interfaceMethods);
        }
    }

    private void collectParentMethod(ClassOrInterfaceDeclaration clazz, List<MethodDeclaration> interfaceMethods) {

        //得到接口的方法
        for (ClassOrInterfaceType interfaceType : clazz.getImplementedTypes()) {
            try {
                ResolvedTypeDeclaration resolvedTypeDeclaration = Store.javaParserFacade.getSymbolSolver()
                        .solveType(interfaceType);
                JavaParserInterfaceDeclaration javaParserInterfaceDeclaration = (JavaParserInterfaceDeclaration)
                        (resolvedTypeDeclaration.asInterface());
                ClassOrInterfaceDeclaration interfaceDeclaration = javaParserInterfaceDeclaration.getWrappedNode();
                interfaceMethods.addAll(interfaceDeclaration.getMethods());
                collectParentMethod(interfaceDeclaration, interfaceMethods);
            } catch (UnsolvedSymbolException e) {

            } catch (UnsupportedOperationException e) {

            }
        }

        //得到父类的方法
        for (ClassOrInterfaceType classType : clazz.getExtendedTypes()) {
            try {

                ResolvedTypeDeclaration resolvedTypeDeclaration = Store.javaParserFacade.getSymbolSolver()
                        .solveType(classType);
                JavaParserClassDeclaration javaParserClassDeclaration = (JavaParserClassDeclaration) (resolvedTypeDeclaration.asClass());
                ClassOrInterfaceDeclaration interfaceDeclaration = javaParserClassDeclaration.getWrappedNode();
                interfaceMethods.addAll(interfaceDeclaration.getMethods());
                collectParentMethod(interfaceDeclaration, interfaceMethods);
            } catch (UnsolvedSymbolException e) {

            } catch (UnsupportedOperationException e) {

            }
        }
    }

    private void solveMethods(JavaModel javaModel, List<MethodDeclaration> clazzMethods, List<MethodDeclaration> methods) {
        //去重
        Set<MethodDeclaration> methodDeclarationSet = new HashSet<>();
        for (MethodDeclaration methodDeclaration : methods) {
            //查找类中是否有覆写父类方法的方法
            for (MethodDeclaration method : clazzMethods) {
                //比较方法是否相同
                if (!compare(methodDeclaration, method)) {
                    continue;
                }

                //如果方法是覆写的 判断是否有注解Override
                if (isHasOverrideAnnotation(method)) {
                    continue;
                }
                methodDeclarationSet.add(method);
                break;
            }
        }

        for (MethodDeclaration method : methodDeclarationSet) {
            getContext().getIssues().add(createIssue(javaModel, method));
        }
    }

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
