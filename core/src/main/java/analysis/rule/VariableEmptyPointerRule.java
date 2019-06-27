package analysis.rule;

import analysis.AbstractRuleVisitor;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import model.IssueContext;
import model.JavaModel;

import java.util.List;

/**
 * 空指针检测
 * */
public class VariableEmptyPointerRule extends AbstractRuleVisitor {

    @Override
    public IssueContext apply(List<JavaModel> javaModels) {
        for(JavaModel javaModel:javaModels){
            scanEmptyPointer(javaModel);
        }
        return getContext();
    }

    private void scanEmptyPointer(JavaModel javaModel){
        //局部变量声明
        List<VariableDeclarationExpr> variables = javaModel.getUnit().findAll(VariableDeclarationExpr.class);
        for(VariableDeclarationExpr variable:variables){
            NodeList<VariableDeclarator> varors = variable.getVariables();
        }
    }
}
