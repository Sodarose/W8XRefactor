package refactor;

import com.github.javaparser.ast.expr.*;
import org.omg.CORBA.UNKNOWN;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kangkang
 */
public class ExpressionTool {
    private static Map<String, BinaryExpr.Operator>
            logicMap = new HashMap<>();
    private static Map<String, BinaryExpr.Operator>
            judgeMap = new HashMap<>();

    static {
        /**
         * || and &&
         * | and &
         * */
        logicMap.put(BinaryExpr.Operator.OR.asString(), BinaryExpr.Operator.AND);
        logicMap.put(BinaryExpr.Operator.AND.asString(), BinaryExpr.Operator.OR);
        logicMap.put(BinaryExpr.Operator.BINARY_OR.asString(), BinaryExpr.Operator.BINARY_AND);
        logicMap.put(BinaryExpr.Operator.BINARY_AND.asString(), BinaryExpr.Operator.BINARY_OR);

        /**
         * == and !=
         * < and >=
         * > and <=
         * */
        judgeMap.put(BinaryExpr.Operator.EQUALS.asString(), BinaryExpr.Operator.NOT_EQUALS);
        judgeMap.put(BinaryExpr.Operator.NOT_EQUALS.asString(), BinaryExpr.Operator.EQUALS);
        judgeMap.put(BinaryExpr.Operator.LESS.asString(), BinaryExpr.Operator.GREATER_EQUALS);
        judgeMap.put(BinaryExpr.Operator.GREATER_EQUALS.asString(), BinaryExpr.Operator.LESS);
        judgeMap.put(BinaryExpr.Operator.GREATER.asString(), BinaryExpr.Operator.LESS_EQUALS);
        judgeMap.put(BinaryExpr.Operator.LESS_EQUALS.asString(), BinaryExpr.Operator.GREATER);
    }

    public static Expression reverse(Expression expression) {
        Expression expr = null;
        //如果是布尔值的单变量
        if (expression.isBooleanLiteralExpr()) {
            return reverseBool(expression);
        }

        if (expression.isBinaryExpr()) {
            BinaryExpr binaryExpr = expression.asBinaryExpr();
            reverseBinaryExpr(binaryExpr);
            return expression;
        }

        return reverseMethodCall(expression);
    }

    //如果是布尔值的单变量
    private static BooleanLiteralExpr reverseBool(Expression expression) {
        BooleanLiteralExpr booleanLiteralExpr = expression.asBooleanLiteralExpr();
        return booleanLiteralExpr.setValue(!booleanLiteralExpr.getValue());
    }

    //如果是方法体
    private static UnaryExpr reverseMethodCall(Expression expression) {
        UnaryExpr unaryExpr = new UnaryExpr();
        unaryExpr.setExpression(expression);
        unaryExpr.setOperator(UnaryExpr.Operator.LOGICAL_COMPLEMENT);
        return unaryExpr;
    }

    //如果是== || 这种
    private static void reverseBinaryExpr(BinaryExpr binaryExpr) {
        Expression left = binaryExpr.getLeft();
        Expression right = binaryExpr.getRight();
        String operator = binaryExpr.getOperator().asString();
        //先判断是logic还是judge 如果是logic则 两边都需要取反 如果是logic则符号取反即可
        if (logicMap.containsKey(operator)) {
            if (!left.isBinaryExpr()) {
                binaryExpr.setLeft(reverseMethodCall(left));
            } else {
                reverseBinaryExpr(left.asBinaryExpr());
            }
            if (!right.isBinaryExpr()) {
                binaryExpr.setRight(reverseMethodCall(right));
            }else{
                reverseBinaryExpr(right.asBinaryExpr());
            }
            binaryExpr.setOperator(logicMap.get(operator));
            return;
        }

        if (judgeMap.containsKey(operator)) {
            if (left.isBinaryExpr()) {
                reverseBinaryExpr(left.asBinaryExpr());
            }
            if (right.isBinaryExpr()) {
                reverseBinaryExpr(right.asBinaryExpr());
            }
            binaryExpr.setOperator(judgeMap.get(operator));
            return;
        }
    }


}