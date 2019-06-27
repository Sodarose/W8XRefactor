package analysis.rule;

import analysis.AbstractRuleVisitor;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.type.Type;
import io.FileUlits;
import model.IssueContext;
import model.JavaModel;


import java.util.List;

public class VariableDeclarationSpecification extends AbstractRuleVisitor {

    @Override
    public IssueContext apply(List<JavaModel> javaModels) {
        for (JavaModel javaModel : javaModels) {
            checkVariableDeclaration(javaModel);
        }

        return getContext();
    }

    private void checkVariableDeclaration(JavaModel javaModel) {
        List<LongLiteralExpr> longLiteralExprs = javaModel.getUnit().findAll(LongLiteralExpr.class);
        for (LongLiteralExpr longLiteralExpr : longLiteralExprs) {
            longLiteralExpr.setLong(longLiteralExpr.asLong());
            if(!longLiteralExpr.getParentNode().isPresent()){
                continue;
            }
            Node node = longLiteralExpr.getParentNode().get();
            if ("com.github.javaparser.ast.body.VariableDeclarator".equals(node.getClass().getName())) {
                VariableDeclarator variableDeclarator = (VariableDeclarator) node;
                Long temp = longLiteralExpr.asLong();
                variableDeclarator.setInitializer(temp+"L");
            }
            if ("com.github.javaparser.ast.expr.AssignExpr".equals(node.getClass().getName())) {
                AssignExpr assignExpr = (AssignExpr) node;
                Long temp = longLiteralExpr.asLong();
                NameExpr nameExpr = new NameExpr();
                nameExpr.setName(temp+"L");
                assignExpr.setValue(nameExpr);
            }
        }
    }
}
