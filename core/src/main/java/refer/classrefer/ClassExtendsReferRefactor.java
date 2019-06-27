package refer.classrefer;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import model.Store;
import refactor.refactorimpl.ClassNameRefactor;
import ulits.ImportDeclarationUtil;

import java.util.List;
import java.util.Optional;

public class ClassExtendsReferRefactor {
    public static void extendsRefactor(String oldClassName,String newClassName) {
        List<CompilationUnit> units = Store.javaFiles;
        for (CompilationUnit unit : units) {
            List<ClassOrInterfaceDeclaration> classOrInterfaceDeclarations = unit.findAll(ClassOrInterfaceDeclaration.class);
            if (!(classOrInterfaceDeclarations.isEmpty())) {
                for (ClassOrInterfaceDeclaration classOrInterfaceDeclaration : classOrInterfaceDeclarations) {
                    List<ClassOrInterfaceType> classOrInterfaceTypeList = classOrInterfaceDeclaration.getExtendedTypes();
                    if (!(classOrInterfaceTypeList.isEmpty())) {
                        for (ClassOrInterfaceType classOrInterfaceType : classOrInterfaceTypeList) {
                            if (classOrInterfaceType.getNameAsString().equals(oldClassName)) {
                                classOrInterfaceType.setName(newClassName);
                                classOrInterfaceDeclaration.setExtendedType(0, classOrInterfaceType);
                            }
                        }
                    }
                }
            }
        }
    }
    public static void main(String args[]){
        ClassExtendsReferRefactor.extendsRefactor("ClassVariRefactor","classVariRefactor");
    }
}

