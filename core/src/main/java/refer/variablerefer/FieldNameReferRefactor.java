package refer.variablerefer;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.NameExpr;
import model.Store;

import java.util.List;

public class FieldNameReferRefactor {
    public static void nameReferRefactor(String oldVariName,String newVariName){
                FieldAccessExprRefactor(oldVariName,newVariName);
                nameExprRefactor(oldVariName,newVariName);
    }
    //FieldAccessExpr
    public static void FieldAccessExprRefactor(String oldVariName,String newVariName){
        List<CompilationUnit> units=Store.javaFiles;
        for(CompilationUnit unit:units) {
            List<FieldAccessExpr> fieldAccessExprs = unit.findAll(FieldAccessExpr.class);
            if (!(fieldAccessExprs.isEmpty())) {
                for (FieldAccessExpr fieldAccessExpr : fieldAccessExprs) {
                    if (fieldAccessExpr.getName().equals(oldVariName)) {
                        fieldAccessExpr.setName(newVariName);
                    }
                }
            }
        }
    }
    //NameExpr
    public static void nameExprRefactor(String oldVariName,String newVariName){
        List<CompilationUnit> units= Store.javaFiles;
        for(CompilationUnit unit:units){
            List<NameExpr> nameExprs=unit.findAll(NameExpr.class);
            if(!(nameExprs.isEmpty())) {
                for (NameExpr nameExpr : nameExprs) {
                    if (nameExpr.getName().equals(oldVariName)) {
                        nameExpr.setName(newVariName);
                    }
                }
            }
        }
    }
}

