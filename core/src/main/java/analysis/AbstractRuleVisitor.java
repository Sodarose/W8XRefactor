package analysis;

import lombok.Data;
import model.IssueContext;

@Data
public abstract class AbstractRuleVisitor  implements Rule{
    private String ruleName;
    private String description;
    private String className;
    private boolean ruleStatus;
    private String message;
    private String example;
    private IssueContext context = new IssueContext();
    private String solutionClassName;
    private int level;


    @Override
    public boolean isRun() {
        return isRuleStatus();
    }
}
