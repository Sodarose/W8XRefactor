package refactor.refactorimpl;

import analysis.rule.UnusedImportsRule;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import io.FileUlits;
import model.Issue;
import model.JavaModel;
import refactor.AbstractRefactor;

import java.util.ArrayList;
import java.util.List;

public class UnusedImportsRefactor extends AbstractRefactor {

    @Override
    public void refactor(Issue issue) {
        ImportDeclaration importDeclaration = (ImportDeclaration) issue.getIssueNode();
        removeImport(importDeclaration);
    }
    private void removeImport(ImportDeclaration declaration){
        if(declaration.getParentNode().isPresent()){
            System.out.println(declaration);
            declaration.getParentNode().get().remove(declaration);
        }
    }

}
