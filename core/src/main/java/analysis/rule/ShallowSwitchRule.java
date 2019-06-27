package analysis.rule;

import analysis.AbstractRuleVisitor;
import analysis.BaseVisitor;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.stmt.SwitchStmt;
import model.Issue;
import model.IssueContext;
import model.JavaModel;
import refactor.refactorimpl.ShallowSwitchRefactor;

import java.util.List;

/**
 * 浅的Switch
 */
public class ShallowSwitchRule extends AbstractRuleVisitor {
    private final int MAX_DEEP = 4;

    @Override
    public IssueContext apply(List<JavaModel> javaModels) {
        for (JavaModel javaModel : javaModels) {
            checkSwitch(javaModel);
        }
        return getContext();
    }

    /**
     * 检查 检查case的层次
     */
    private void checkSwitch(JavaModel javaModel) {
        List<SwitchStmt> switchStmts = javaModel.getUnit().findAll(SwitchStmt.class);
        for (SwitchStmt switchStmt : switchStmts) {
            if (switchStmt.getEntries().size() > MAX_DEEP) {
                continue;
            }
            Issue issue = new Issue();
            issue.setJavaModel(javaModel);
            issue.setIssueNode(switchStmt);
            issue.setRefactorName(getSolutionClassName());
            issue.setDescription(getDescription());
            issue.setRuleName(getRuleName());
            getContext().getIssues().add(issue);
        }
    }


}
