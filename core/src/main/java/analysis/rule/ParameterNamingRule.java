package analysis.rule;

import analysis.AbstractRuleVisitor;
import analysis.BaseVisitor;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.Parameter;
import io.FileUlits;
import model.Issue;
import model.IssueContext;
import model.JavaModel;
import ulits.SplitName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ParameterNamingRule extends AbstractRuleVisitor {
    @Override
    public IssueContext apply(List<JavaModel> javaModels) {
        for(JavaModel javaModel:javaModels){
            try {
                checkParameterName(javaModel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return getContext();
    }
    public void checkParameterName(JavaModel javaModel) throws IOException {
        List<Parameter> parameterList=javaModel.getUnit().findAll(Parameter.class);
        for(Parameter parameter:parameterList){
            String name=parameter.getNameAsString();
            List<String> nameList= SplitName.split(name);
            if(nameList!=null) {
                boolean nameFlag = check(nameList);
                if (!nameFlag) {
                    Issue issue = new Issue();
                    issue.setIssueNode(parameter);
                    issue.setJavaModel(javaModel);
                    issue.setRefactorName(getSolutionClassName());
                    issue.setDescription(getDescription());
                    issue.setRuleName(getRuleName());
                    getContext().getIssues().add(issue);
                }
            }
        }
    }
    public boolean check(List<String> nameList){
        boolean flag=false;
        for(String name:nameList){
            if(flag==false){
                flag=true;
                continue;
            }
            char temp=name.charAt(0);
            if(temp >= 97 && temp <=122){
                return false;
            }
        }
        return true;
    }
}
