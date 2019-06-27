package refer.methodrefer;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;
import model.Store;
import ulits.MethodCertainUtil;

import java.util.List;

public class MethodReferRefactor {
    public static void nameReferRefactor(String oldMethodName,String newMethodName){
        List<CompilationUnit> units= Store.javaFiles;
        for (CompilationUnit unit:units) {
            List<MethodCallExpr> methodCallExprList = unit.findAll(MethodCallExpr.class);
            if (!(methodCallExprList.isEmpty())) {
                for (MethodCallExpr methodCallExpr : methodCallExprList) {
                    if (methodCallExpr.getNameAsString().equals(oldMethodName)) {
                        methodCallExpr.setName(newMethodName);
                    }
                }
            }
        }
    }
}
