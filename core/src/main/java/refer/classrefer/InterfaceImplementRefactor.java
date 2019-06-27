package refer.classrefer;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import io.ParserProject;
import model.Store;
import ulits.ImportDeclarationUtil;

import java.util.List;
import java.util.Optional;

public class InterfaceImplementRefactor {
    public static void implementRefactor(String oldInferfaceName, String newInferfaceName) {
        List<CompilationUnit> units = Store.javaFiles;
        for (CompilationUnit unit : units) {
            List<ClassOrInterfaceDeclaration> classOrInterfaceDeclarations = unit.findAll(ClassOrInterfaceDeclaration.class);
            if (!(classOrInterfaceDeclarations.isEmpty())) {
                for (ClassOrInterfaceDeclaration classOrInterfaceDeclaration : classOrInterfaceDeclarations) {
                    List<ClassOrInterfaceType> classOrInterfaceTypeList = classOrInterfaceDeclaration.getImplementedTypes();
                    if (!(classOrInterfaceTypeList.isEmpty())) {
                        int index = 0;//位置索引
                        for (ClassOrInterfaceType classOrInterfaceType : classOrInterfaceTypeList) {
                            if (classOrInterfaceType.getNameAsString().equals(oldInferfaceName)) {
                                classOrInterfaceType.setName(newInferfaceName);
                                classOrInterfaceDeclaration.setImplementedType(index, classOrInterfaceType);
                            }
                            index++;
                        }
                    }
                }
            }
        }
    }
    public static void main(String args[]){
       InterfaceImplementRefactor.implementRefactor("infertest","Infertest");
    }
}

