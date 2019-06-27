package refactor.refactorimpl;

import com.github.javaparser.ast.ImportDeclaration;
import model.Issue;
import refactor.AbstractRefactor;

public class UnusedImportsRefactor extends AbstractRefactor {

    @Override
    public void refactor(Issue issue) {
        ImportDeclaration importDeclaration = (ImportDeclaration) issue.getIssueNode();
        removeImport(importDeclaration);
    }
    private void removeImport(ImportDeclaration declaration){
        if(declaration.getParentNode().isPresent()){
            declaration.getParentNode().get().remove(declaration);
        }
    }

}
