package analysis.rule;

import analysis.AbstractRuleVisitor;
import model.IssueContext;
import model.JavaModel;

import java.util.List;

public class AvoidConcurrentCompetitionRandomRule extends AbstractRuleVisitor {
    @Override
    public IssueContext apply(List<JavaModel> javaModels) {
        return null;
    }
}
