package refactor.refactorimpl;

import analysis.rule.LowerCamelCaseVariableNaming;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import io.FileUlits;
import model.Issue;
import refactor.AbstractRefactor;
import ulits.SplitName;
import ulits.VariableReferUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VariableNameRefactor extends AbstractRefactor {
    @Override
    public void refactor(Issue issue) {
        VariableDeclarationExpr variableDeclarationExpr=(VariableDeclarationExpr) issue.getIssueNode();
        try {
            variableNameRefactor(variableDeclarationExpr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void variableNameRefactor(VariableDeclarationExpr variableDeclarationExpr) throws IOException {
        String oldName=variableDeclarationExpr.getVariable(0).getNameAsString();
        String newName="";
        List<String> nameList = SplitName.split(variableDeclarationExpr.getVariable(0).getNameAsString());
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
        variableDeclarationExpr.getVariable(0).setName(newName);
        VariableReferUtil.VariNameUtil(oldName,newName);
    }
}
