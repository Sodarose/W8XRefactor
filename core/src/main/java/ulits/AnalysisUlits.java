package ulits;

import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;

import java.util.List;

public class AnalysisUlits {
    /**
     * 递归拆解表达式
     * */
    public static void analysisExpr(Expression condition, List<Expression> exprs) {
        if (condition.isBinaryExpr()) {
            analysisExpr(condition.asBinaryExpr().getLeft(), exprs);
            analysisExpr(condition.asBinaryExpr().getRight(), exprs);
        } else if (condition.isMethodCallExpr()) {
            if (!condition.asMethodCallExpr().getName().getIdentifier().contains("equals")) {
                exprs.add(condition);
                return;
            }
            Expression left = condition.asMethodCallExpr().getArguments().get(0);
            Expression right = condition.asMethodCallExpr().getScope().get();
            exprs.add(left);
            exprs.add(right);
            return;
        } else {
            exprs.add(condition);
            return;
        }
    }

    public static void analysisCondition(Expression condition, List<Expression> expressions) {
        if (condition.isBinaryExpr()) {
            if (condition.asBinaryExpr().getOperator().equals(BinaryExpr.Operator.OR)) {
                analysisCondition(condition.asBinaryExpr().getLeft(), expressions);
                analysisCondition(condition.asBinaryExpr().getRight(), expressions);
            } else if (condition.asBinaryExpr().getOperator().equals(BinaryExpr.Operator.EQUALS)) {
                expressions.add(condition);
                return;
            } else {
                expressions.add(null);
                return;
            }
        } else if (condition.isMethodCallExpr()) {
            if (condition.asMethodCallExpr().getName().getIdentifier().contains("equals")) {
                expressions.add(condition);
            } else {
                expressions.add(null);
            }
            return;
        } else {
            expressions.add(null);
            return;
        }
    }
    /**
     * 探测嵌套深度
     */
    public static int getDeep(Statement root) {
        IfStmt it = getIfStmt(root);
        if (it == null) {
            return 0;
        } else {
            int left = getDeep(it.getThenStmt());
            int right = 0;
            if (it.hasElseBranch()) {
                right = getDeep(it.getElseStmt().get());
            }
            return 1 + Math.max(left, right);
        }
    }

    private static IfStmt getIfStmt(Statement stmt) {
        if (stmt.isIfStmt()) {
            return stmt.asIfStmt();
        }
        if (stmt.isBlockStmt()) {
            for (Statement statement : stmt.asBlockStmt().getStatements()) {
                if (statement.isIfStmt()) {
                    return statement.asIfStmt();
                }
            }
            return null;
        } else {
            return null;
        }
    }
}

