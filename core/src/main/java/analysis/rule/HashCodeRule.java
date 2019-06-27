package analysis.rule;

import analysis.AbstractRuleVisitor;
import api.AnalysisApi;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.symbolsolver.javaparser.Navigator;
import io.FileUlits;
import model.Issue;
import model.IssueContext;
import model.JavaModel;

import java.util.List;
import java.util.Optional;

public class HashCodeRule extends AbstractRuleVisitor {
    final  String HASHCODE_WARN = "该类重写了hashCode函数 但是没有重写equals函数";
    final  String EQUALS_WARN = "该类重写了equals函数 但是没有重写hashCode函数";
    @Override
    public IssueContext apply(List<JavaModel> javaModels) {
        for (JavaModel javaModel : javaModels) {
            checkHashCode(javaModel);
        }
        return getContext();
    }

    private void checkHashCode(JavaModel javaModel) {
        List<ClassOrInterfaceDeclaration> clazzs = javaModel.getUnit().findAll(ClassOrInterfaceDeclaration.class);
        for (ClassOrInterfaceDeclaration clazz : clazzs) {
            //得到方法列表
            List<MethodDeclaration> methodDeclarations = clazz.getMethods();
            //得到hashcode
            Optional<MethodDeclaration> hashCodeMethod = methodDeclarations.stream().filter(methodDeclaration -> {
                String methodName = methodDeclaration.getName().getIdentifier();
                //判断函数名
                if (!"hashCode".equals(methodName)) {
                    return false;
                }
                //判断返回值
                if (!methodDeclaration.getType().equals(PrimitiveType.intType())) {
                    return false;
                }
                //判断参数各个数
                if (methodDeclaration.getParameters().size() != 0) {
                    return false;
                }
                return true;
            }).findFirst();

            //得到equals
            Optional<MethodDeclaration> hashEqualsMethod = methodDeclarations.stream().filter(methodDeclaration -> {
                String method = methodDeclaration.getName().getIdentifier();
                if (!"equals".equals(method)) {
                    return false;
                }
                if (!methodDeclaration.getType().equals(PrimitiveType.booleanType())) {
                    return false;
                }
                if (methodDeclaration.getParameters().size() != 1) {
                    return false;
                }
                return true;
            }).findFirst();

            if(hashCodeMethod.isPresent()&&hashEqualsMethod.isPresent()){
                continue;
            }

            if(hashCodeMethod.isPresent()&&!hashEqualsMethod.isPresent()){
                BlockComment blockComment = new BlockComment();
                blockComment.setContent(HASHCODE_WARN);
                hashCodeMethod.get().setComment(blockComment);
                getContext().getIssues().add(createIssue(javaModel, hashCodeMethod.get()));
            }

            if(!hashCodeMethod.isPresent()&&hashEqualsMethod.isPresent()){
                BlockComment blockComment = new BlockComment();
                blockComment.setContent(EQUALS_WARN);
                hashEqualsMethod.get().setComment(blockComment);
                getContext().getIssues().add(createIssue(javaModel, hashEqualsMethod.get()));
            }

        }
    }

    private Issue createIssue(JavaModel javaModel, Node node) {
        Issue issue = new Issue();
        issue.setRuleName(getRuleName());
        issue.setJavaModel(javaModel);
        issue.setClassName(getClassName());
        issue.setIssueNode(node);
        issue.setDescription(getDescription());
        issue.setRefactorName(getSolutionClassName());
        return issue;
    }
}
