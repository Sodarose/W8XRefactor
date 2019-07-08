package analysis.rule;

import analysis.AbstractRuleVisitor;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import model.Issue;
import model.IssueContext;
import model.JavaModel;

import java.util.List;

public class AvoidUserTimeRule extends AbstractRuleVisitor {
    @Override
    public IssueContext apply(List<JavaModel> javaModels) {
        for (JavaModel javaModel:javaModels){
            checkType(javaModel);
        }
        return getContext();
    }
    private void checkType(JavaModel javaModel){
        List<VariableDeclarationExpr> variableDeclarationExprList =javaModel.getUnit().findAll(VariableDeclarationExpr.class);
        for(VariableDeclarationExpr variableDeclarationExpr :variableDeclarationExprList){
           if(variableDeclarationExpr.getVariable(0).getType().toString().equals("Timer")){
               Issue issue = new Issue();
               issue.setIssueNode(variableDeclarationExpr);
               issue.setJavaModel(javaModel);
               issue.setRefactorName(getSolutionClassName());
               issue.setDescription(getDescription());
               issue.setRuleName(getRuleName());
               getContext().getIssues().add(issue);
           }
        }
    }
}
