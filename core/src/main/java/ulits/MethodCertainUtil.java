package ulits;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MethodCertainUtil {
    public static List<String> classNumberUtil(CompilationUnit unit, String packageName, String className) {
        List<String> nameList = new ArrayList<>();
        Optional<PackageDeclaration> packageDeclaration = unit.getPackageDeclaration();
        if (packageDeclaration != null && packageDeclaration.isPresent()) {
            if (!(packageDeclaration.get().getName().toString().equals(packageName))) {
                int importNumber = ImportDeclarationUtil.importNumberUtil(unit, packageName, className);
                if (importNumber == 1) {
                    List<FieldDeclaration> fieldDeclarations = unit.findAll(FieldDeclaration.class);
                    for (FieldDeclaration fieldDeclaration : fieldDeclarations) {
                        List<VariableDeclarator> variableDeclarators = fieldDeclaration.getVariables();
                        for (VariableDeclarator variableDeclarator : variableDeclarators) {
                            String name = variableDeclarator.getType().getClass().getName();
                            if (name.equals("com.github.javaparser.ast.type.ClassOrInterfaceType")) {
                                ClassOrInterfaceType type = (ClassOrInterfaceType) variableDeclarator.getType();
                                if (type.getName().toString().equals(className)) {
                                    nameList.add(variableDeclarator.getNameAsString());
                                }
                            }
                        }
                    }
                    List<VariableDeclarationExpr> variableDeclarationExprs = unit.findAll(VariableDeclarationExpr.class);
                    for (VariableDeclarationExpr var : variableDeclarationExprs) {
                        for (VariableDeclarator variableDeclarator : var.getVariables()) {
                            String name = variableDeclarator.getType().getClass().getName();
                            if (name.equals("com.github.javaparser.ast.type.ClassOrInterfaceType")) {
                                ClassOrInterfaceType type = (ClassOrInterfaceType) variableDeclarator.getType();
                                if (type.getName().toString().equals(className)) {
                                    nameList.add(variableDeclarator.getNameAsString());
                                }
                            }
                        }
                    }
                }
            } else {
                int importNumber = ImportDeclarationUtil.importNumberUtil(unit, packageName, className);
                if (importNumber == 0) {
                    List<FieldDeclaration> fieldDeclarations = unit.findAll(FieldDeclaration.class);
                    for (FieldDeclaration fieldDeclaration : fieldDeclarations) {
                        List<VariableDeclarator> variableDeclarators = fieldDeclaration.getVariables();
                        for (VariableDeclarator variableDeclarator : variableDeclarators) {
                            String name = variableDeclarator.getType().getClass().getName();
                            if (name.equals("com.github.javaparser.ast.type.ClassOrInterfaceType")) {
                                ClassOrInterfaceType type = (ClassOrInterfaceType) variableDeclarator.getType();
                                if (type.getName().toString().equals(className)) {
                                    nameList.add(variableDeclarator.getNameAsString());
                                }
                            }
                        }
                    }
                    List<VariableDeclarationExpr> variableDeclarationExprs = unit.findAll(VariableDeclarationExpr.class);
                    for (VariableDeclarationExpr var : variableDeclarationExprs) {
                        for (VariableDeclarator variableDeclarator : var.getVariables()) {
                            String name = variableDeclarator.getType().getClass().getName();
                            if (name.equals("com.github.javaparser.ast.type.ClassOrInterfaceType")) {
                                ClassOrInterfaceType type = (ClassOrInterfaceType) variableDeclarator.getType();
                                if (type.getName().toString().equals(className)) {
                                    nameList.add(variableDeclarator.getNameAsString());
                                }
                            }
                        }
                    }

                }
            }
        }

        return nameList;
    }
}

