package refactor.refactorimpl;


import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.WhileStmt;
import model.Issue;
import refactor.Refactor;

import java.util.List;
import java.util.Map;

/**
 * @author kangkang
 */
public class WhileChangeRefactor implements Refactor {
    @Override
    public void refactor(Issue issue) {
        Map<String, Object> data = issue.getData();
        WhileStmt whileStmt = (WhileStmt) issue.getIssueNode();
        transFromsFor(whileStmt, data);
    }

    private void transFromsFor(WhileStmt whileStmt, Map<String, Object> data) {
        //初始化条件
        List<VariableDeclarator> inits = (List<VariableDeclarator>) data.get("inits");
        //更新条件
        List<Expression> updates = (List<Expression>) data.get("updates");
        //比较条件
        Expression condition = (Expression) data.get("condition");

        if (!clean(inits, updates)) {
            return;
        }

        ForStmt forStmt = new ForStmt();
        //设置condition
        forStmt.setCompare(condition);
        //设置初始化
        forStmt.setInitialization(createInitialization(inits));
        //设置更新
        forStmt.setUpdate(new NodeList<>(createUpdates(updates)));
        //设置body
        forStmt.setBody(whileStmt.getBody());
        //替换
        if (!whileStmt.getParentNode().isPresent()) {
            return;
        }
        whileStmt.getParentNode().get().replace(whileStmt, forStmt);
    }

    /**
     * 清理原来的初始化语句
     */
    private boolean clean(List<VariableDeclarator> inits, List<Expression> updates) {
        for (VariableDeclarator ex : inits) {
            if (!ex.getParentNode().isPresent()) {
                return false;
            }

            //优化
            VariableDeclarationExpr v = (VariableDeclarationExpr) ex.getParentNode().get();
            v.remove(ex);

            if (v.getVariables().size() == 0) {

                ExpressionStmt stmt = (ExpressionStmt) v.getParentNode().get();
                stmt.getParentNode().get().remove(stmt);
            }
        }
        for (Expression ex : updates) {
            if (!ex.getParentNode().isPresent()) {
                return false;
            }
            ExpressionStmt stmt = (ExpressionStmt) ex.getParentNode().get();
            stmt.getParentNode().get().remove(stmt);
        }
        return true;
    }

    private NodeList<Expression> createInitialization(List<VariableDeclarator> inits) {
        VariableDeclarationExpr initialization = new VariableDeclarationExpr();
        initialization.getVariables().addAll(inits);
        return new NodeList<>(initialization);
    }

    private NodeList<Expression> createUpdates(List<Expression> updates) {
        NodeList<Expression> expressions = new NodeList<>();
        expressions.addAll(updates);
        return expressions;
    }

}
