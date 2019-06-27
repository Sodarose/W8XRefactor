package analysis.rule;

import analysis.AbstractRuleVisitor;
import analysis.BaseVisitor;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import io.FileUlits;
import model.Issue;
import model.IssueContext;
import model.JavaModel;

import java.util.ArrayList;
import java.util.List;

public class ClassConstantNamingRule extends AbstractRuleVisitor {

    public void checkConstantName(JavaModel javaModel){
        List<FieldDeclaration> fieldDeclarationList=javaModel.getUnit().findAll(FieldDeclaration.class);
        for (FieldDeclaration fieldDeclaration:fieldDeclarationList) {
            if (fieldDeclaration.isFinal()) {
                String constantName = fieldDeclaration.getVariable(0).getNameAsString();
                if (!(constantName.equals(constantName.toUpperCase()))) {
                    Issue issue = new Issue();
                    issue.setIssueNode(fieldDeclaration);
                    issue.setJavaModel(javaModel);
                    issue.setRefactorName(getSolutionClassName());
                    issue.setDescription(getDescription());
                    issue.setRuleName(getRuleName());
                    getContext().getIssues().add(issue);
                }
            }
        }
    }
    @Override
    public IssueContext apply(List<JavaModel> javaModels) {
        for (JavaModel javaModel:javaModels){
            checkConstantName(javaModel);
        }
        return getContext();
    }
    public static void main(String[] args) {

    }
}
