package refer.classrefer;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import io.ParserProject;
import model.Store;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;
import ulits.ImportDeclarationUtil;

import java.util.List;
import java.util.Optional;

public class VariNameReferRefactor {
    public static void nameReferRefactor(String oldClassName,String newClassName) {
        List<CompilationUnit> units = Store.javaFiles;
        for (CompilationUnit unit : units) {
            List<VariableDeclarationExpr> variableDeclarationExprs = unit.findAll(VariableDeclarationExpr.class);
            if (!(variableDeclarationExprs.isEmpty())) {
                for (VariableDeclarationExpr var : variableDeclarationExprs) {
                    for (VariableDeclarator variableDeclarator : var.getVariables()) {
                        String name = variableDeclarator.getType().getClass().getName();
                        if (name.equals("com.github.javaparser.ast.type.ClassOrInterfaceType")) {
                            ClassOrInterfaceType type = (ClassOrInterfaceType) variableDeclarator.getType();
                            if (type.getName().toString().equals(oldClassName)) {
                                type.setName(newClassName);
                                Optional<Expression> initializer = variableDeclarator.getInitializer();
                                if (initializer != null && initializer.isPresent()) {
                                    if (initializer.get().getClass().toString().equals("class com.github.javaparser.ast.expr.ObjectCreationExpr")) {
                                        ObjectCreationExpr objectCreationExpr = (ObjectCreationExpr) initializer.get();
                                        if (objectCreationExpr.getType().toString().equals(oldClassName)) {
                                            objectCreationExpr.setType(type);
                                            variableDeclarator.setInitializer(objectCreationExpr);
                                        }
                                    }
                                }
                                variableDeclarator.setType(type);
                            }
                        }
                    }
                }
            }
        }
    }
        public static void main (String[]args){
            VariNameReferRefactor.nameReferRefactor("ClassVariRefactor", "classVariRefactor");
        }
    }


