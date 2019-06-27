package refactor.refactorimpl;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import model.Issue;
import refactor.AbstractRefactor;

public class OverwriteMethodRefactor extends AbstractRefactor {
    @Override
    public void refactor(Issue issue) {
        MethodDeclaration method = (MethodDeclaration) issue.getIssueNode();
        fix(method);

    }

    private void fix(MethodDeclaration method) {
        MarkerAnnotationExpr markerAnnotationExpr = new MarkerAnnotationExpr();
        markerAnnotationExpr.setName("Override");
        method.getAnnotations().add(markerAnnotationExpr);
    }


}
