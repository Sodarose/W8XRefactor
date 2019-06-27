package analysis.rule;

import analysis.AbstractRuleVisitor;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import io.FileUlits;
import model.IssueContext;
import model.JavaModel;

import java.util.List;
import java.util.stream.Collectors;

public class StringComparisonRule extends AbstractRuleVisitor {

    @Override
    public IssueContext apply(List<JavaModel> javaModels) {
        for (JavaModel javaModel : javaModels) {
            fix(javaModel);
        }
        return getContext();
    }

    private void fix(JavaModel javaModel) {
        List<BinaryExpr> binaryExprs = javaModel.getUnit().findAll(BinaryExpr.class);
        List<MethodCallExpr> methodCallExprs = javaModel.getUnit().findAll(MethodCallExpr.class);
        //过滤
        binaryExprs = binaryExprs.stream().filter(binaryExpr -> {
            if (!binaryExpr.getOperator().equals(BinaryExpr.Operator.EQUALS)) {
                return false;
            }
            if (!binaryExpr.getLeft().isStringLiteralExpr() && !binaryExpr.getRight().isStringLiteralExpr()) {
                return false;
            }
            return true;
        }).collect(Collectors.toList());
        methodCallExprs = methodCallExprs.stream().filter(methodCallExpr -> {
            if (!"equals".equals(methodCallExpr.getName().getIdentifier())) {
                return false;
            }
            if (!methodCallExpr.getScope().isPresent()) {
                return false;
            }
            if (methodCallExpr.getArguments().size() > 1) {
                return false;
            }
            Expression left = methodCallExpr.getScope().get();
            Expression right = methodCallExpr.getArguments().get(0);
            if (left.isStringLiteralExpr()) {
                return false;
            }
            if (!right.isStringLiteralExpr()) {
                return false;
            }
            return true;
        }).collect(Collectors.toList());

        for(BinaryExpr binaryExpr :binaryExprs){
            Expression left = binaryExpr.getLeft();
            Expression right = binaryExpr.getRight();
            MethodCallExpr methodCallExpr = new MethodCallExpr();
            methodCallExpr.setName("equals");
            if(left.isStringLiteralExpr()){
                methodCallExpr.setScope(left);
                methodCallExpr.getArguments().add(right);
            }
            if(right.isStringLiteralExpr()){
                methodCallExpr.setScope(right);
                methodCallExpr.getArguments().add(left);
            }
            if(!binaryExpr.getParentNode().isPresent()){
                continue;
            }
            binaryExpr.getParentNode().get().replace(binaryExpr,methodCallExpr);
        }

        for(MethodCallExpr methodCallExpr:methodCallExprs){
            Expression left = methodCallExpr.getScope().get();
            Expression right = methodCallExpr.getArguments().get(0);
            methodCallExpr.setScope(right);
            methodCallExpr.getArguments().clear();;
            methodCallExpr.getArguments().add(left);
        }
    }

}
