package refer.classrefer;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import model.Store;

import java.util.List;

public class MethodCallExprReferRefactor {
    public static void MethodCallExprRefactor(String oldClassName,String newClassName) {
        List<CompilationUnit> units = Store.javaFiles;
        for (CompilationUnit unit : units) {
                List<MethodCallExpr> methodCallExprs = unit.findAll(MethodCallExpr.class);
                if(!methodCallExprs.isEmpty()){
                    for(MethodCallExpr methodCallExpr:methodCallExprs) {
                        if (methodCallExpr.getScope().isPresent()) {
                            String classNameString = methodCallExpr.getScope().get().toString();
                            int index = classNameString.lastIndexOf(".");
                            if (index != -1) {
                                if (classNameString.substring(index + 1).equals(oldClassName)) {
                                    // Expression nameExpression = methodCallExpr.getScope().get();
                                    NameExpr nameExpr = new NameExpr();
                                    nameExpr.setName(classNameString.substring(0, index + 1) + newClassName);
                                    methodCallExpr.setScope(nameExpr);
                                    // System.out.println(methodCallExpr.getScope().get());
                                    // nameExpression.setLineComment(newClassName);
                                    // methodCallExpr.setScope(nameExpression);
                                    // System.out.println(methodCallExpr.getScope().get());
                                }
                            } else {
                                if (methodCallExpr.getScope().get().toString().equals(oldClassName)) {
                                    //Expression nameExpression = methodCallExpr.getScope().get();
                                    NameExpr nameExpr = new NameExpr();
                                    nameExpr.setName(newClassName);
                                    methodCallExpr.setScope(nameExpr);
                                    //System.out.println(methodCallExpr.getScope().get());
//                                nameExpression.setLineComment(newClassName);
//                                methodCallExpr.setScope(nameExpression);
//                                System.out.println(methodCallExpr.getScope().get());
                                }
                            }
                        }
                    }
                }
        }
    }
}
