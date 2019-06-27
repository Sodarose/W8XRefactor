package analysis.rule;


import analysis.AbstractRuleVisitor;
import analysis.BaseVisitor;
import com.github.javaparser.Range;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.*;
import io.FileUlits;
import io.ParserProject;
import jdk.nashorn.internal.ir.IfNode;
import model.Issue;
import model.IssueContext;
import model.JavaModel;
import refactor.refactorimpl.IfTransformSwitchRefactor;
import ulits.AnalysisUlits;


import java.util.*;
import java.util.stream.Collectors;

/**
 * rule if深层嵌套扫描
 *
 * @author kangkang
 */
public class DeeplyIfStmtsRule extends AbstractRuleVisitor {

    private JavaModel javaModel;
    /**
     * 测定深度
     */
    private final int MAX_DEEP = 3;


    @Override
    public IssueContext apply(List<JavaModel> javaModels) {
        for (JavaModel javaModel : javaModels) {
            this.javaModel = javaModel;
            collectIssue(javaModel.getUnit());
        }
        return getContext();
    }

    /**
     * 收集坏味道代码
     */
    private void collectIssue(CompilationUnit unit) {
        List<IfStmt> ifStmts = getSurface(unit);
        Map<Range, IfStmt> map = new HashMap<>(5);
        for (IfStmt ifStmt : ifStmts) {
            IfStmt i = getParent(ifStmt);
            if (!i.getRange().isPresent()) {
                continue;
            }
            map.put(i.getRange().get(), i);
        }
        ifStmts = map.values().stream().collect(Collectors.toList());
        Iterator<IfStmt> it = ifStmts.iterator();
        while (it.hasNext()) {
            IfStmt ifStmt = it.next();
            //深度小于MAX 并且宽度也小于max的剔除
            if (AnalysisUlits.getDeep(ifStmt) < MAX_DEEP) {
                if (getBreadth(ifStmt) <= MAX_DEEP) {
                    it.remove();
                }
            }
        }
        Iterator<IfStmt> is = ifStmts.iterator();
        while (is.hasNext()) {
            IfStmt ifStmt = is.next();
            Issue issue = new Issue();
            issue.setJavaModel(javaModel);
            issue.setIssueNode(ifStmt);
            issue.setRefactorName(getSolutionClassName());
            Map<String, Object> data = new HashMap<>(1);
            String returnTyle = getType(ifStmt);
            if (returnTyle == null) {
                return;
            }
            data.put("Interrupt", returnTyle);
            issue.setData(data);
            getContext().getIssues().add(issue);
        }
    }

    /**
     * 找到返回的Type类型
     */
    private String getType(Node node) {
        while (node.getParentNode().isPresent()) {
            node = node.getParentNode().get();
            if (node.getClass().getName().equals("com.github.javaparser.ast.body.MethodDeclaration")) {
                MethodDeclaration method = (MethodDeclaration) node;
                if (method.getType().isVoidType()) {
                    return "void";
                } else {
                    return "object";
                }
            }
            if (node.getClass().getName().equals("com.github.javaparser.ast.stmt.ForStmt") ||
                    node.getClass().getName().equals("com.github.javaparser.ast.stmt.WhileStmt")||
                    "com.github.javaparser.ast.stmt.ForEachStmt".equals(node.getClass().getName())) {
                return "continue";
            }
        }
        return null;
    }

    /**
     * 收集表层if
     */
    private List<IfStmt> getSurface(CompilationUnit unit) {
        BaseVisitor<IfStmt> visitor = new BaseVisitor<IfStmt>() {
            @Override
            public void visit(IfStmt n, Object arg) {
                getList().add(n);
            }
        };
        unit.accept(visitor, null);
        return visitor.getList();
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


    /**
     * 探测广度
     */
    private int getBreadth(Statement stmt) {
        int i = 0;
        while (stmt.isIfStmt()) {
            IfStmt ifStmt = stmt.asIfStmt();
            i++;
            if (!ifStmt.hasElseBranch()) {
                break;
            }
            stmt = ifStmt.getElseStmt().get();
        }
        if (!stmt.isIfStmt()) {
            i++;
        }
        return i;
    }

}