package analysis.rule;

import analysis.AbstractRuleVisitor;
import com.github.javaparser.ast.stmt.SwitchEntry;
import com.github.javaparser.ast.stmt.SwitchStmt;
import model.IssueContext;
import model.JavaModel;

import java.util.List;

/**
 * 为缺少默认值得switch语句补充默认值 这个不返回issue
 */
public class SwicthDefaultRule extends AbstractRuleVisitor {
    @Override
    public IssueContext apply(List<JavaModel> javaModels) {
        for (JavaModel javaModel : javaModels) {
            fix(javaModel);
        }
        return getContext();
    }

    private void fix(JavaModel javaModel) {
        List<SwitchStmt> switchStmts = javaModel.getUnit().findAll(SwitchStmt.class);
        for (SwitchStmt switchStmt : switchStmts) {
            List<SwitchEntry> switchEntries = switchStmt.getEntries();
            if (hasDefaultEntries(switchEntries)) {
                continue;
            }
            SwitchEntry switchEntry = new SwitchEntry();
            switchStmt.getEntries().add(switchEntry);
        }
    }

    private boolean hasDefaultEntries(List<SwitchEntry> switchEntries) {
        for (SwitchEntry switchEntry : switchEntries) {
            if (switchEntry.getLabels().size() == 0 || switchEntry.getLabels() == null) {
                return true;
            }
        }
        return false;
    }
}
