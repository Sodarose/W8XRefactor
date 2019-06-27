package analysis.rule;

import analysis.AbstractRuleVisitor;
import com.github.javaparser.Range;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import model.Issue;
import model.IssueContext;
import model.JavaModel;
import ulits.AnalysisUlits;

import java.util.*;

/**
 * if转换为transform
 *
 * @author kangkang
 */
public class IFTransformSwitchRule extends AbstractRuleVisitor {
    private final int min = 3;
    private JavaModel javaModel;

    @Override
    public IssueContext apply(List<JavaModel> javaModels) {
        for (JavaModel javaModel : javaModels) {
            this.javaModel = javaModel;
            collectIssue(javaModel.getUnit());
        }
        return getContext();
    }

    /**
     * 收集问题代码
     */
    private void collectIssue(CompilationUnit unit) {
        List<IfStmt> ifStmts = unit.findAll(IfStmt.class);
        Map<Range, IfStmt> map = new HashMap<>(1);
        for (IfStmt ifStmt : ifStmts) {
            IfStmt i = getParent(ifStmt);
            if (!i.getRange().isPresent()) {
                continue;
            }
            map.put(i.getRange().get(), i);
        }
        for (Map.Entry<Range, IfStmt> entry : map.entrySet()) {
            Map<String, Object> data = needRefactor(entry.getValue());
            if (data == null || data.size() == 0) {
                continue;
            }
            getContext().getIssues().add(createIssue(entry.getValue(), data));
        }
    }

    /**
     * 判断释放需要重构s
     */
    private Map<String, Object> needRefactor(IfStmt ifStmt) {
        int deep = checkDeepAndCondition(ifStmt);
        if (deep <= min) {
            return null;
        }
        List<Expression> conditions = getAllConditions(ifStmt);
        if (conditions == null || conditions.size() == 0) {
            return null;
        }
        List<Expression> tags = getAllExpr(conditions);
        if (tags == null || tags.size() == 0) {
            return null;
        }
        Expression selector = getSelector(tags, conditions.size());
        Map<String, Object> map = new HashMap<>(2);
        map.put("selector", selector);
        return map;
    }


    /**
     * 从tag选出selector 并且判断是否有重复的标签
     */
    private Expression getSelector(List<Expression> tags, int i) {
        Map<Expression, Integer> map = new HashMap<>(tags.size());
        for (Expression tag : tags) {
            if (map.containsKey(tag)) {
                map.put(tag, map.get(tag) + 1);
            } else {
                map.put(tag, 1);
            }
        }
        Expression selector = null;
        for (Map.Entry<Expression, Integer> entry : map.entrySet()) {
            //selector 个数一定等于表达式个数
            if (entry.getValue() == i) {
                selector = entry.getKey();
            }
        }
        map.remove(selector);
        for (Map.Entry<Expression, Integer> entry : map.entrySet()) {
            if (entry.getValue() != 1) {
                return null;
            }
            if (!entry.getKey().isLiteralExpr() && !entry.getKey().isNameExpr() && !entry.getKey().isFieldAccessExpr()) {
                return null;
            }
        }
        return selector;
    }

    /**
     * 拆分表达式得到所有tag选项 tag选项只能为NameExpr 或者是基本类型 例如; int string 等等
     */
    private List<Expression> getAllExpr(List<Expression> conditions) {
        List<Expression> exprs = new ArrayList<>();
        for (Expression expr : conditions) {
            AnalysisUlits.analysisExpr(expr, exprs);
        }
        return exprs;
    }

    /**
     * 得到所有条件
     */
    private List<Expression> getAllConditions(Statement stmt) {
        List<Expression> conditions = new ArrayList<>();
        while (stmt.isIfStmt()) {
            IfStmt ifStmt = stmt.asIfStmt();
            Expression condition = ifStmt.getCondition();
            AnalysisUlits.analysisCondition(condition,conditions);
            if (!ifStmt.hasElseBranch()) {
                break;
            }
            stmt = ifStmt.getElseStmt().get();
        }
        if(conditions.contains(null)){
            return null;
        }
        return conditions;
    }

    /**
     * 判断深度是否符合转换规则
     */
    private int checkDeepAndCondition(Statement stmt) {
        int i = 0;
        while (stmt.isIfStmt()) {
            IfStmt ifStmt = stmt.asIfStmt();
            if (!ifStmt.getElseStmt().isPresent()) {
                break;
            }
            i++;
            if (!ifStmt.hasElseBranch()) {
                break;
            }
            stmt = ifStmt.getElseStmt().get();
        }
        return i;
    }

    /**
     * 生成issue
     */
    private Issue createIssue(IfStmt ifStmt, Map<String, Object> data) {
        Issue issue = new Issue();
        issue.setRefactorName(getSolutionClassName());
        issue.setIssueNode(ifStmt);
        issue.setData(data);
        issue.setJavaModel(this.javaModel);
        issue.setDescription(getDescription());
        issue.setRuleName(getRuleName());
        return issue;
    }

    /**
     * 递归方式找到头if节点
     */
    private IfStmt getParent(IfStmt ifStmt) {
        if (!ifStmt.getParentNode().isPresent()) {
            return ifStmt;
        } else if ("com.github.javaparser.ast.stmt.IfStmt".equals(ifStmt.getParentNode().get().getClass().getName())) {
            return getParent((IfStmt) ifStmt.getParentNode().get());
        } else {
            return ifStmt;
        }
    }

}
