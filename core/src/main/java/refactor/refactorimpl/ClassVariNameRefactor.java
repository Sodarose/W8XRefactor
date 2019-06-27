package refactor.refactorimpl;

import analysis.rule.ClassVariNamingRule;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import io.FileUlits;
import model.Issue;
import refactor.AbstractRefactor;
import ulits.SplitName;
import ulits.VariableReferUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClassVariNameRefactor  extends AbstractRefactor {
    @Override
    public void refactor(Issue issue) {
        FieldDeclaration fieldDeclaration=(FieldDeclaration)issue.getIssueNode();
        try {
            variableNameRefactor(fieldDeclaration);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void variableNameRefactor(FieldDeclaration fieldDeclaration) throws IOException {
        String newName="";
        String oldName=fieldDeclaration.getVariable(0).getNameAsString();
        List<String> nameList = SplitName.split(fieldDeclaration.getVariable(0).getNameAsString());
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
        fieldDeclaration.getVariable(0).setName(newName);
        VariableReferUtil.referUtil(oldName,newName);
    }

}
