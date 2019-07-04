package refactor.refactorimpl;

import analysis.rule.ParameterNamingRule;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.Parameter;
import io.FileUlits;
import model.Issue;
import refactor.AbstractRefactor;
import ulits.SplitName;
import ulits.SplitWord;
import ulits.VariableReferUtil;

import java.awt.peer.ScrollbarPeer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
        SplitWord splitWord=new SplitWord();
        List<String> nameList= splitWord.split(parameter.getNameAsString());
        Collections.reverse(nameList);
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
        if(!newName.equals("")) {
            parameter.setName(newName);
            VariableReferUtil.VariableNameUtil(oldName, newName);
        }}

}
