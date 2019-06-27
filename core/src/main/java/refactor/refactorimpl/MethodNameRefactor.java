package refactor.refactorimpl;

import analysis.rule.MethodNamingShouldBeCamelRule;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import io.FileUlits;
import model.Issue;
import refactor.AbstractRefactor;
import ulits.MethodReferUtil;
import ulits.SplitName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MethodNameRefactor extends AbstractRefactor {
    @Override
    public void refactor(Issue issue) {
        MethodDeclaration methodDeclaration=(MethodDeclaration) issue.getIssueNode();
        try {
            methodNameRefactor(methodDeclaration);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void methodNameRefactor(MethodDeclaration methodDeclaration) throws IOException {
        String oldName=methodDeclaration.getNameAsString();
        String newName="";
        List<String> nameList= SplitName.split(methodDeclaration.getNameAsString());
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
        methodDeclaration.setName(newName);
        MethodReferUtil.referUtil(oldName,newName);
    }

}
