package analysis.rule;

import analysis.AbstractRuleVisitor;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.symbolsolver.javaparser.Navigator;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserSymbolDeclaration;
import com.github.javaparser.symbolsolver.model.resolution.SymbolReference;
import io.FileUlits;
import javassist.expr.Expr;
import model.Issue;
import model.IssueContext;
import model.JavaModel;
import model.Store;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 转换为for语句会更好
 * int i
 * while(i<100){
 * xxxxxxx;
 * i++;
 * }
 *
 * @author kangkang
 */
public class WhileChangeForRule extends AbstractRuleVisitor {

    private final String labeledStmt = "LabeledStmt";
    private JavaModel javaModel;

    @Override
    public IssueContext apply(List<JavaModel> javaModels) {
        for (JavaModel javaModel : javaModels) {
            this.javaModel = javaModel;
            checkWhile(javaModel.getUnit());
        }
        return getContext();
    }

    /**
     * while转换为For
     */
    private void checkWhile(CompilationUnit unit) {
        List<WhileStmt> stmts = unit.findAll(WhileStmt.class);
        check(stmts);
    }

    /**
     * swicth转换for所需要的规则
     */
    private void check(List<WhileStmt> stmts) {
        for (WhileStmt stmt : stmts) {
            //得到条件表达式
            Expression condition = stmt.getCondition();
            //得到循环方法体
            Statement statement = stmt.getBody();

            if (!condition.isBinaryExpr()) {
                continue;
            }
            if (!stmt.getParentNode().isPresent()) {
                continue;
            }
            List<Expression> expres = new ArrayList<>();

            collectExpr(condition, expres);
            if (expres.size() == 0) {
                continue;
            }
            Node parent;
            if (!stmt.getParentNode().isPresent()) {
                continue;
            }
            //判断父类是否是标签类
            if (stmt.getParentNode().get().getClass().getName().indexOf("LabeledStmt") > 0) {
                if (!stmt.getParentNode().get().getParentNode().isPresent()) {
                    continue;
                }
                parent = stmt.getParentNode().get().getParentNode().get();
            } else {
                parent = stmt.getParentNode().get();
            }
            //得到初始化函数
            List<VariableDeclarator> inits = collectInit(stmt, parent, expres);
            if (inits == null || inits.size() == 0) {
                continue;
            }

            //得到更新点
            List<Expression> updates = collectUpdate(inits, statement);
            //检查
            boolean result = checkUpdates(updates, stmt);

            if (!result) {
                continue;
            }

            if (updates == null || updates.size() == 0) {
                continue;
            }
            Map<String, Object> data = new HashMap<>(4);
            data.put("parent", parent);
            data.put("condition", condition);
            data.put("inits", inits);
            data.put("updates", updates);
            Issue issue = new Issue();
            issue.setData(data);
            issue.setJavaModel(javaModel);
            issue.setIssueNode(stmt);
            issue.setRefactorName(getSolutionClassName());
            issue.setDescription(getDescription());
            issue.setRuleName(getRuleName());
            getContext().getIssues().add(issue);
        }
    }

    private boolean checkUpdates(List<Expression> updates, WhileStmt stmt) {
        for (Expression expr : updates) {
            Expression expression = null;

            if (expr instanceof AssignExpr) {
                AssignExpr assignExpr = (AssignExpr) expr;
                expression = assignExpr.getValue();

            }

            if (expr instanceof UnaryExpr) {
                UnaryExpr unaryExpr = (UnaryExpr) expr;
                expression = unaryExpr.getExpression();
            }

            SymbolReference symbolReference = null;
            try {
                symbolReference = Store.javaParserFacade.solve(expression);
            } catch (Exception e) {
            }
            if (symbolReference == null) {
                continue;
            }
            if (!symbolReference.isSolved()) {
                return false;
            }

            if (!(symbolReference.getCorrespondingDeclaration() instanceof JavaParserSymbolDeclaration)) {
                return false;
            }

            JavaParserSymbolDeclaration javaParser = (JavaParserSymbolDeclaration) symbolReference.
                    getCorrespondingDeclaration();

            if (!(javaParser.getWrappedNode() instanceof VariableDeclarator)) {
                return false;
            }
            VariableDeclarator variableDeclarator = (VariableDeclarator) javaParser.getWrappedNode();
            if (!variableDeclarator.getParentNode().isPresent()) {
                return false;
            }

            VariableDeclarationExpr variableDeclarationExpr = (VariableDeclarationExpr)
                    variableDeclarator.getParentNode().get();

            if (!variableDeclarationExpr.getParentNode().isPresent()) {
                return false;
            }

            if (!(variableDeclarationExpr.getParentNode().get() instanceof ExpressionStmt)) {
                return false;
            }

            ExpressionStmt expressionStmt = (ExpressionStmt) variableDeclarationExpr.getParentNode().get();
            //Integer k =(Integer) data.values().toArray()[0];
            if (stmt.getBody().asBlockStmt().getStatements().contains(expressionStmt)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 通过递归找到表达式中所有NameExpr的表达式
     */
    private void collectExpr(Expression condition, List<Expression> list) {
        if (condition.isNameExpr()) {
            list.add(condition);
            return;
        } else if (condition.isBinaryExpr()) {
            collectExpr(condition.asBinaryExpr().getLeft(), list);
            collectExpr(condition.asBinaryExpr().getRight(), list);
            return;
        } else {
            return;
        }
    }


    /**
     * 确定init
     */
    private List<VariableDeclarator> collectInit(WhileStmt stmt, Node parent, List<Expression> exprs) {
        List<VariableDeclarator> expressions = new ArrayList<>();
        for (Expression ex : exprs) {
            //得到名字
            String name = ex.asNameExpr().getName().getIdentifier();
            //根据名字找到声明语句
            Optional<VariableDeclarator> optional = Navigator.demandVariableDeclaration(parent, ex.asNameExpr().
                    getName().getIdentifier());
            if (!optional.isPresent()) {
                continue;
            }
            //声明语句
            VariableDeclarator variable = optional.get();
            //根据名字找到使用这个变量的地方
            List<SimpleName> simpleNames = parent.findAll(SimpleName.class).stream().filter(simpleName -> simpleName.
                    getIdentifier().equals(name)).collect(Collectors.toList());
            //如果在到达while之前variable被更改 则不能转换为for
            boolean notChange = false;
            for (SimpleName simpleName : simpleNames) {
                if (!stmt.getRange().isPresent()) {
                    continue;
                }
                if (!variable.getRange().get().contains(simpleName.getRange().get()) && !stmt.getRange().
                        get().contains(simpleName.getRange().get())) {
                    notChange = true;
                    break;
                }
            }
            if (notChange) {
                continue;
            }
            expressions.add(variable);
        }
        return expressions;
    }

    /**
     * 确定update 这个地方还有争议
     */
    private List<Expression> collectUpdate(List<VariableDeclarator> inits, Statement statement) {
        List<Expression> exprs = new ArrayList<>();
        if (!statement.isBlockStmt()) {
            return null;
        }
        for (VariableDeclarator v : inits) {
            String name = v.getName().getIdentifier();
            List<AssignExpr> assignExprs = statement.findAll(AssignExpr.class).stream().filter(assignExpr ->
                    assignExpr.getTarget().isNameExpr() && assignExpr.getTarget().asNameExpr().getName().getIdentifier().equals(name)).
                    collect(Collectors.toList());
            List<UnaryExpr> unaryExprs = statement.findAll(UnaryExpr.class).
                    stream().filter(unaryExpr -> unaryExpr.getExpression().isNameExpr() &&
                    unaryExpr.getExpression().asNameExpr().getName().getIdentifier().equals(name)).collect(Collectors.toList());

            if (assignExprs.size() == 1) {
                AssignExpr assignExpr = assignExprs.get(0);
                exprs.add(assignExpr);
            }

            if (unaryExprs.size() == 1) {
                UnaryExpr unaryExpr = unaryExprs.get(0);
                exprs.add(unaryExpr);
            }
        }
        return exprs;
    }
}
