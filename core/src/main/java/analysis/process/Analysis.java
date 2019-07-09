package analysis.process;

import analysis.AbstractRuleVisitor;
import analysis.Rule;
import analysis.RuleLink;
import io.ParserProject;
import io.ParserProjectDome;
import model.IssueContext;
import model.JavaModel;
import model.Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import refactor.ReFactorExec;

import java.io.FileNotFoundException;
import java.util.Date;
import java.util.List;

public class Analysis {

    private static final Logger LOGGER = LoggerFactory.getLogger(Analysis.class);

    public void analysis(String path) throws FileNotFoundException {
        List<JavaModel> javaModels = ParserProjectDome.parserProject(path);
        List<Rule> rules = Store.rules;
        runAnalysis(javaModels, rules);
    }

    private void runAnalysis(List<JavaModel> javaModels, List<Rule> rules) {
        ReFactorExec reFactorExec = ReFactorExec.getInstance();
        long sumTime = 0;
        for (Rule rule : rules) {
            AbstractRuleVisitor ruleVisitor = (AbstractRuleVisitor) rule;
            if (ruleVisitor.isRuleStatus()) {
                long startTime = System.currentTimeMillis();
                LOGGER.info("执行" + ruleVisitor.getRuleName() + "规则扫描");
                IssueContext issueContext = rule.apply(javaModels);
                long endTime = System.currentTimeMillis();
                LOGGER.info(ruleVisitor.getRuleName() + "扫描完毕");
                LOGGER.info("执行时间:"+(endTime-startTime)/1000+"s");
                sumTime = sumTime + (endTime-startTime);
                LOGGER.info("执行" + ruleVisitor.getRuleName() + "的重构");
                long startTime1 = System.currentTimeMillis();
                reFactorExec.runFactor(issueContext.getIssues());
                long endTime1 = System.currentTimeMillis();
                LOGGER.info(ruleVisitor.getRuleName() + "重构完毕");
                LOGGER.info("执行时间:"+(endTime1-startTime1)/1000+"s");
                sumTime = sumTime + (endTime1-startTime1);
                if (Store.issueContext == null) {
                    Store.issueContext = new IssueContext();
                    Store.issueContext.getIssues().addAll(issueContext.getIssues());
                    continue;
                }
                Store.issueContext.getIssues().addAll(issueContext.getIssues());
            }
        }
        LOGGER.info("执行完成");
        LOGGER.info("执行时间:"+sumTime/1000+"s");
    }

    public static void main(String[] args) throws FileNotFoundException {
        String filePath = "C:/Users/Administrator/Desktop/RefactorTest-master";
        Analysis analysis = new Analysis();
        List<JavaModel> javaModels = ParserProjectDome.parserProject(filePath);
        List<Rule> rules = RuleLink.newInstance().readRuleLinkByXML();
        analysis.runAnalysis(javaModels, rules);
    }
}
