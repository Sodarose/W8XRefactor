package refactor.refactorimpl;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.model.resolution.SymbolReference;
import io.ParserProject;
import model.Issue;
import model.Store;
import refactor.AbstractRefactor;

import java.util.*;

/**
 * 将case小于等于3的switch语句转换为if
 *
 * @author kangkang
 */
public class ShallowSwitchRefactor extends AbstractRefactor {
    private boolean isEnum = false;
    private boolean isString = false;
    private boolean isOrder = false;
    String enumName;
    private Node node = null;

    @Override
    public void refactor(Issue issue) {
        SwitchStmt switchStmt = (SwitchStmt) issue.getIssueNode();
        checkType(switchStmt);
        transFrom(switchStmt);
    }

    /**
     * 转换函数
     */
    private void transFrom(SwitchStmt switchStmt) {
        checkType(switchStmt);
        node = switchStmt.findRootNode();
        Expression selector = switchStmt.getSelector();
        Statement statement = buildStmt(selector, switchStmt.getEntries());
        //这个地方有可能出错
        BlockStmt blockStmt = (BlockStmt) switchStmt.getParentNode().get();
        int index = blockStmt.getStatements().indexOf(switchStmt);
        if (statement.isBlockStmt()) {
            blockStmt.getStatements().addAll(index, statement.asBlockStmt().getStatements());
        } else {
            blockStmt.getStatements().add(index, statement.asIfStmt());
        }
        blockStmt.remove(switchStmt);
    }


    /**
     * 组合分支语句
     */
    private Statement buildStmt(Expression selector, List<SwitchEntry> switchEntries) {
        IfStmt p = null, q = null;
        boolean head = true;
        //针对空body的swuich分支
        BinaryExpr expr = null;
        for (int i = 0; i < switchEntries.size(); i++) {
            Statement statement = createIfStmt(selector, switchEntries, i);
            if (statement == null) {
                continue;
            }
            //
            if (statement.isIfStmt()) {
                IfStmt ifStmt = statement.asIfStmt();
                //如果返回的if语句是空if语句 保留它的condition
                if (!ifStmt.getThenStmt().isBlockStmt()) {
                    //第一次
                    if (expr == null) {
                        expr = new BinaryExpr();
                        expr.setLeft(ifStmt.getCondition());
                        continue;
                    }
                    BinaryExpr temp = new BinaryExpr();
                    temp.setLeft(expr.getLeft());
                    temp.setRight(ifStmt.getCondition());
                    temp.setOperator(BinaryExpr.Operator.OR);
                    expr.setLeft(temp);
                    continue;
                }

                //如果不为0则将表达式以或的条件放入
                if (expr != null) {
                    expr.setRight(ifStmt.getCondition());
                    expr.setOperator(BinaryExpr.Operator.OR);
                    ifStmt.setCondition(expr);
                    expr = null;
                }

                //头结点
                if (head) {
                    p = ifStmt;
                    q = p;
                    head = false;
                    continue;
                }
                p.setElseStmt(ifStmt);
                p = statement.asIfStmt();
            }

            if (statement.isBlockStmt()) {
                if (head) {
                    return statement.asBlockStmt();
                }
                p.setElseStmt(statement.asBlockStmt());
            }
        }

        //记得删掉

        clean();
        return q;
    }

    /**
     * 根据case生成分支语句
     */
    private Statement createIfStmt(Expression selector, List<SwitchEntry> switchEntrys, int index) {

        BlockStmt blockStmt = new BlockStmt();
        SwitchEntry switchEntry = switchEntrys.get(index);
        Expression condition = buildCondition(selector, switchEntry);
        // 添加自己的statmes
        if (switchEntry.getStatements().size() == 1 && switchEntry.getStatements().get(0).isBlockStmt()) {
            blockStmt = switchEntry.getStatements().get(0).asBlockStmt();
        } else {
            blockStmt.getStatements().addAll(switchEntry.getStatements());
        }

        //如果是个空分支则直接返回空if语句
        if (blockStmt.getStatements().size() == 0) {
            IfStmt ifStmt = new IfStmt();
            ifStmt.setCondition(condition);
            return ifStmt;
        }

        // 从当前位置向下搜索,如果遇到没有break或者return 将他们的statme提取到当前分支
        for (int i = index; i < switchEntrys.size() - 1; i++) {
            if (ishasBreakOrReturn(switchEntrys.get(i))) {
                break;
            }
            blockStmt.getStatements().addAll(switchEntrys.get(i + 1).getStatements());
        }

        // 清除当前分支blockstate中的break
        cleanBreakStmt(blockStmt);

        //如果为default分支 则直接返回分支
        if (condition == null) {
            return blockStmt;
        }
        //如果不是default 返回if
        IfStmt ifStmt = new IfStmt();
        ifStmt.setCondition(condition);
        ifStmt.setThenStmt(blockStmt);
        return ifStmt;
    }

    /**
     * 生成表达式
     * condition =>
     */
    private Expression buildCondition(Expression select, SwitchEntry switchEntry) {
        //判断是否为一个default 如果是就返回空
        if (switchEntry.getLabels() == null || switchEntry.getLabels().size() == 0) {
            return null;
        }
        Expression cases = switchEntry.getLabels().get(0);
        //字符串
        if (isString) {
            FieldAccessExpr fieldAccessExpr = new FieldAccessExpr();
            fieldAccessExpr.setName("equals(\"" + cases.asStringLiteralExpr().asString() + "\")");
            fieldAccessExpr.setScope(select);
            return fieldAccessExpr;
        }

        //枚举变量
        if (isEnum) {
            String labName = cases.toString();
            Expression left = select;
            Expression right = null;
            if (labName.indexOf(enumName) > 0) {
                right = cases;
            } else {
                FieldAccessExpr fieldAccessExpr = new FieldAccessExpr();
                fieldAccessExpr.setScope(new NameExpr(enumName));
                fieldAccessExpr.setName(labName);
                right = fieldAccessExpr;
            }
            BinaryExpr condition = new BinaryExpr();
            condition.setOperator(BinaryExpr.Operator.EQUALS);
            condition.setLeft(left);
            condition.setRight(right);
            return condition;
        }

        BinaryExpr condition = new BinaryExpr();
        condition.setOperator(BinaryExpr.Operator.EQUALS);
        condition.setLeft(select);
        condition.setRight(switchEntry.getLabels().get(0));
        return condition;
    }

    /**
     * 检查select的类型 select 可以为 name 可以为 函数  或者
     */
    private void checkType(SwitchStmt switchStmt) {
        Expression selector = switchStmt.getSelector();
        if (selector.isStringLiteralExpr()) {
            isString = true;
            return;
        }

        ResolvedType s = Store.javaParserFacade.getType(selector);
        //搜索根据类限定名称搜索
        SymbolReference v = Store.combinedTypeSolver.tryToSolveType(s.describe());
        if (!v.isSolved()) {
            isOrder = true;
            return;
        }
        if (v.getCorrespondingDeclaration().isType()) {
            if (v.getCorrespondingDeclaration().asType().isEnum()) {
                isEnum = true;
                enumName = v.getCorrespondingDeclaration().asType().getClassName();
                return;
            }
            if (v.getCorrespondingDeclaration().asType().getClassName().equals("String")) {
                isString = true;
                return;
            }
        }
    }

    /**
     * 去除分支中的break
     */
    public void cleanBreakStmt(BlockStmt blockStmt) {
        ArrayList<BreakStmt> breakStmts = new ArrayList<>();
        for (Statement stmt : blockStmt.getStatements()) {
            if (stmt.isBreakStmt()) {
                breakStmts.add(stmt.asBreakStmt());
            }
        }
        blockStmt.getStatements().removeAll(breakStmts);
    }

    /**
     * 判断这个case是否有break return 和 throw
     */
    private boolean ishasBreakOrReturn(SwitchEntry switchEntry) {
        final List<Statement> statements;
        if (switchEntry.getStatements().size() == 1 && switchEntry.getStatements().get(0).isBlockStmt()) {
            statements = switchEntry.getStatements().get(0).asBlockStmt().getStatements();
        } else {
            statements = switchEntry.getStatements();
        }
        if (statements.size() == 0) {
            return false;
        }

        if (statements.get(statements.size() - 1).isBreakStmt()) {
            return true;
        }
        if (statements.get(statements.size() - 1).isReturnStmt()) {
            return true;
        }
        if (statements.get(statements.size() - 1).isThrowStmt()) {
            return true;
        }

        //返回语句在if之中
        if (statements.get(statements.size() - 1).isIfStmt()) {
            IfStmt ifStmt = statements.get(statements.size() - 1).asIfStmt();
            if (!ifStmt.hasElseBranch()) {
                return false;
            }
            if (!ifStmt.getElseStmt().isPresent()) {
                return false;
            }
            return ishasReturn(ifStmt);
        }
        return false;
    }

    /**
     * 针对 if(){
     * return xx;
     * }else if{
     * return xx;
     * }else{
     * return xx;
     * }
     */
    private boolean ishasReturn(IfStmt ifStmt) {
        while (ifStmt.hasElseBranch()) {
            if (ifStmt.getThenStmt().findAll(ReturnStmt.class).size() == 0) {
                return false;
            }
            if (ifStmt.getElseStmt().get().isIfStmt()) {
                ifStmt = ifStmt.getElseStmt().get().asIfStmt();
            } else {
                if (ifStmt.getElseStmt().get().findAll(ReturnStmt.class).size() == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private void clean() {
        isString = false;
        isEnum = false;
        enumName = "";
    }

    /**
     * 测试
     */
    public static void main(String[] args) {

    }
}

