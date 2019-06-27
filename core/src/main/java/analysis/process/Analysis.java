package analysis.process;

import analysis.AbstractRuleVisitor;
import analysis.Rule;
import analysis.RuleLink;
import io.ParserProject;
import model.IssueContext;
import model.JavaModel;
import model.Store;
import refactor.ReFactorExec;

import java.util.List;

public class Analysis {
    public void analysis(String path) {
        List<JavaModel> javaModels = ParserProject.parserProject(path);
        List<Rule> rules = RuleLink.newInstance().readRuleLinkByXML();
        runAnalysis(javaModels, rules);
    }

    private void runAnalysis(List<JavaModel> javaModels, List<Rule> rules) {
        ReFactorExec reFactorExec = ReFactorExec.getInstance();
        for (Rule rule : rules) {
            AbstractRuleVisitor ruleVisitor = (AbstractRuleVisitor) rule;
            if (ruleVisitor.isRuleStatus()) {
                IssueContext issueContext = rule.apply(javaModels);
                reFactorExec.runFactor(issueContext.getIssues());
                if (Store.issueContext == null) {
                    Store.issueContext = new IssueContext();
                    Store.issueContext.getIssues().addAll(issueContext.getIssues());
                    continue;
                }
                Store.issueContext.getIssues().addAll(issueContext.getIssues());
            }
        }
    }
}
