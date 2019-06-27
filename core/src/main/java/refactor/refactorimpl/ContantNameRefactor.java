package refactor.refactorimpl;


import com.github.javaparser.ast.body.FieldDeclaration;
import io.FileUlits;
import model.Issue;
import refactor.AbstractRefactor;
import ulits.VariableReferUtil;

import java.util.ArrayList;
import java.util.List;

public class ContantNameRefactor extends AbstractRefactor {

    @Override
    public void refactor(Issue issue) {
        FieldDeclaration fieldDeclaration=(FieldDeclaration) issue.getIssueNode();
        constantNameRefactor(fieldDeclaration);
    }
    public void constantNameRefactor(FieldDeclaration fieldDeclaration){
        if (fieldDeclaration.isFinal()) {
            String constantName = fieldDeclaration.getVariable(0).getNameAsString();
            String newName = constantName.toUpperCase();
            fieldDeclaration.getVariable(0).setName(newName);
            VariableReferUtil.referUtil(constantName, newName);
        }
    }

}
