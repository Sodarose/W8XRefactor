package refactor.refactorimpl;

import com.github.javaparser.ast.body.FieldDeclaration;
import model.Issue;
import refactor.AbstractRefactor;
import ulits.VariableReferUtil;

public class ContantNameRefactor extends AbstractRefactor {

    @Override
    public void refactor(Issue issue) {
        FieldDeclaration fieldDeclaration=(FieldDeclaration) issue.getIssueNode();
        constantNameRefactor(fieldDeclaration);
    }
    public void constantNameRefactor(FieldDeclaration fieldDeclaration){
        String constantName=fieldDeclaration.getVariable(0).getNameAsString();
        String newName=constantName.toUpperCase();
        fieldDeclaration.getVariable(0).setName(constantName.toUpperCase());
        VariableReferUtil.referUtil(constantName,newName);
    }

}
