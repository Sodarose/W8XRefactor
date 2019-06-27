package refactor.refactorimpl;

import analysis.rule.ParameterNamingRule;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.Parameter;
import io.FileUlits;
import model.Issue;
import refactor.AbstractRefactor;
import ulits.SplitName;
import ulits.VariableReferUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ParameterNameRefactor extends AbstractRefactor {
    @Override
    public void refactor(Issue issue) {
        Parameter parameter=(Parameter) issue.getIssueNode();
        try {
            parameterNameRefactor(parameter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void parameterNameRefactor(Parameter parameter) throws IOException {
        String oldName=parameter.getNameAsString();
        String newName="";
        List<String> nameList= SplitName.split(parameter.getNameAsString());
        if(nameList==null){
            return;
        }
        for(String data:nameList){
            if(newName.equals("")){
                newName=newName+data;
                continue;
            }
            data=data.substring(0,1).toUpperCase()+data.substring(1);
            newName=newName+data;

        }
        parameter.setName(newName);
        VariableReferUtil.VariNameUtil(oldName,newName);
    }

}
