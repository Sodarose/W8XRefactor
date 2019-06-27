package analysis.rule;

import analysis.AbstractRuleVisitor;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.stmt.*;
import model.Issue;
import model.IssueContext;
import model.JavaModel;

import java.util.List;

/**
 * 这是个前置工作 rule 和
 * 代码风格校验
 * 确保 if while
 * 大括号与if, else, for, do, while语句一起使用，即使只有一条语句(或是空)，也应该把大括号写上。
 */
public class CodeStyleRule extends AbstractRuleVisitor {

    @Override
    public IssueContext apply(List<JavaModel> javaModels) {
        for(JavaModel javaModel:javaModels){
            correct(javaModel);
        }
        return getContext();
    }

    private void correct(JavaModel javaModel) {
        List<DoStmt> doStmts =javaModel.getUnit().findAll(DoStmt.class);
        List<IfStmt> ifStmts = javaModel.getUnit().findAll(IfStmt.class);
        List<ForStmt> forStmts = javaModel.getUnit().findAll(ForStmt.class);
        List<WhileStmt> whileStmts = javaModel.getUnit().findAll(WhileStmt.class);
        List<ForEachStmt> forEachStmts = javaModel.getUnit().findAll(ForEachStmt.class);
        for(DoStmt doStmt:doStmts){
            if(!doStmt.getBody().isBlockStmt()){
                getContext().getIssues().add(createIssue(doStmt,javaModel));
            }
        }
        for(IfStmt ifStmt:ifStmts){
            if(!ifStmt.getThenStmt().isBlockStmt()||ifStmt.hasElseBlock()){
                getContext().getIssues().add(createIssue(ifStmt,javaModel));
            }
        }
        for(ForStmt forStmt:forStmts){
            if(!forStmt.getBody().isBlockStmt()){
                getContext().getIssues().add(createIssue(forStmt,javaModel));
            }
        }
        for(WhileStmt whileStmt:whileStmts){
            if(!whileStmt.getBody().isBlockStmt()){
                getContext().getIssues().add(createIssue(whileStmt,javaModel));
            }
        }
        for(ForEachStmt forEachStmt:forEachStmts){
            if(!forEachStmt.getBody().isBlockStmt()){
                getContext().getIssues().add(createIssue(forEachStmt,javaModel));
            }
        }
    }

    private Issue createIssue(Statement statement,JavaModel javaModel){
        Issue issue = new Issue();
        issue.setRuleName(getRuleName());
        issue.setJavaModel(javaModel);
        issue.setDescription(getDescription());
        issue.setIssueNode(statement);
        issue.setRefactorName(getSolutionClassName());
        return issue;
    }
}
