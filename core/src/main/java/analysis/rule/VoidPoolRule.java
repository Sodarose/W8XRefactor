package analysis.rule;

import analysis.AbstractRuleVisitor;
import analysis.BaseVisitor;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import io.FileUlits;
import model.Issue;
import model.IssueContext;
import model.JavaModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 空条件for循环
 * for(;;){
 * <p>
 * }
 * for(;xx;){
 * <p>
 * }
 * trandform
 * while(xx){
 * <p>
 * }
 * while(true){
 * <p>
 * }
 *
 * @author kangkang
 */
public class VoidPoolRule extends AbstractRuleVisitor {
    @Override
    public IssueContext apply(List<JavaModel> javaModels) {
        for (JavaModel javaModel : javaModels) {
            collectFor(javaModel);
        }
        return getContext();
    }

    private void collectFor(JavaModel javaModel) {
        List<ForStmt> forStmts = javaModel.getUnit().findAll(ForStmt.class);
        check(forStmts, javaModel);
    }

    /**
     * 检测
     */
    private void check(List<ForStmt> forStmts, JavaModel javaModel) {
        int initialization = 0;
        int update = 0;
        int compare = 0;
        for (ForStmt forStmt : forStmts) {
            initialization = forStmt.getInitialization().size();
            update = forStmt.getUpdate().size();
            if (forStmt.getCompare().isPresent()) {
                compare = 1;
            }
            if (initialization > 0 && update > 0 && compare > 0) {
                continue;
            }
            Issue issue = new Issue();
            issue.setJavaModel(javaModel);
            issue.setIssueNode(forStmt);
            issue.setRefactorName(getSolutionClassName());
            issue.setDescription(getDescription());
            issue.setRuleName(getRuleName());
            getContext().getIssues().add(issue);
        }
    }
}
