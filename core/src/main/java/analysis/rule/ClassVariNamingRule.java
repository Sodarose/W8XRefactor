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
import ulits.SplitName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClassVariNamingRule extends AbstractRuleVisitor {
    @Override
    public IssueContext apply(List<JavaModel> javaModels) {
        for (JavaModel javaModel : javaModels) {
            try {
                checkVariableName(javaModel);
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        return getContext();
    }

    public void checkVariableName(JavaModel javaModel) throws IOException {
        List<FieldDeclaration> fieldDeclarationList = javaModel.getUnit().findAll(FieldDeclaration.class);
        for (FieldDeclaration fieldDeclaration : fieldDeclarationList) {
            String name = fieldDeclaration.getVariable(0).getNameAsString();
            List<String> nameList = SplitName.split(name);
            if (nameList != null) {
                boolean nameFlag = check(nameList);
                if (!nameFlag) {
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

    public boolean check(List<String> nameList) {
        boolean flag = false;
        for (String name : nameList) {
            if (flag == false) {
                flag = true;
                continue;
            }
            char temp = name.charAt(0);
            if (temp >= 97 && temp <= 122) {
                return false;
            }
        }
        return true;
    }
}
