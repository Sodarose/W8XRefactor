package refactor.refactorimpl;

import analysis.rule.VoidPoolRule;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.*;
import io.FileUlits;
import model.Issue;
import refactor.AbstractRefactor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * for(;;)=>while(true)
 * and
 * for(;expr;)=>while(expr)
 *
 * @author kangkang
 */
public class VoidPoolRefactor extends AbstractRefactor {

    @Override
    public void refactor(Issue issue) {
        ForStmt forStmt = (ForStmt) issue.getIssueNode();
        tranFromWhile(forStmt);
    }

    /**
     * 转换方法
     */
    private void tranFromWhile(ForStmt forStmt) {
        final Statement stmt = forStmt.getBody();

        //构建body
        BlockStmt blockStmt;
        if (stmt.isBlockStmt()) {
            blockStmt = stmt.asBlockStmt();
        } else {
            blockStmt = new BlockStmt();
            blockStmt.getStatements().add(stmt);
        }

        //构建表达式
        Expression condition;
        if (forStmt.getCompare().isPresent()) {
            condition = forStmt.getCompare().get();
        } else {
            BooleanLiteralExpr temp = new BooleanLiteralExpr();
            temp.setValue(true);
            condition = temp;
        }


        List<Expression> initializations = forStmt.getInitialization();
        //初始化条件
        List<ExpressionStmt> inits = initializations.stream().map(expression -> {
                    ExpressionStmt initStmt = new ExpressionStmt();
                    initStmt.setExpression(expression);
                    return initStmt;
                }
        ).collect(Collectors.toList());

        List<Expression> updates = forStmt.getUpdate();
        //更新条件
        List<ExpressionStmt> updateStmts = updates.stream().map(expression -> {
            ExpressionStmt initStmt = new ExpressionStmt();
            initStmt.setExpression(expression);
            return initStmt;
        }).collect(Collectors.toList());

        WhileStmt whileStmt = new WhileStmt();
        whileStmt.setCondition(condition);
        whileStmt.setBody(blockStmt);
        //将更新条件放进while方法体的末尾
        blockStmt.getStatements().addAll(updateStmts);
        if(!forStmt.getParentNode().isPresent()){
            return;
        }
        Node parentNode = forStmt.getParentNode().get();
        Node indexNode = forStmt;
        if("com.github.javaparser.ast.stmt.LabeledStmt".equals(parentNode.getClass().getName())){
            if(!parentNode.getParentNode().isPresent()){
                return;
            }
            indexNode = parentNode;
            parentNode = parentNode.getParentNode().get();
        }
        if(!"com.github.javaparser.ast.stmt.BlockStmt".equals(parentNode.getClass().getName())){
            return;
        }

        BlockStmt parent = (BlockStmt) parentNode;
        int index = parent.getStatements().indexOf(indexNode);
        //使用while替换for
        forStmt.getParentNode().get().replace(forStmt,whileStmt);
        //将初始化条件放在while的前面,如果有标签 就是在标签的前面
        parent.getStatements().addAll(index, inits);
    }
    
}
