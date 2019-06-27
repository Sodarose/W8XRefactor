package refer.variablerefer;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.NameExpr;
import model.Store;

import java.util.List;

public class VariNameReferRefactor {
    //NameExpr
    public static void nameExprRefactor(String oldVariName,String newVariName){
        List<CompilationUnit> units= Store.javaFiles;
        for(CompilationUnit unit:units) {
            List<NameExpr> nameExprs = unit.findAll(NameExpr.class);
            if (!(nameExprs.isEmpty())) {
                for (NameExpr nameExpr : nameExprs) {
                    if (nameExpr.getName().equals(oldVariName)) {
                        nameExpr.setName(newVariName);
                    }
                }
            }
        }
    }
}
