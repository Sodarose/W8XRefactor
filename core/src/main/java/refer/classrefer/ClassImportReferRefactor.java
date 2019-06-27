package refer.classrefer;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import model.Store;

import java.util.List;

public class ClassImportReferRefactor {
    public static void importRefactor(String oldClassName,String newClassName) {
        List<CompilationUnit> units = Store.javaFiles;
        for (CompilationUnit unit : units) {
            List<ImportDeclaration> importDeclarations = unit.getImports();
            if (!(importDeclarations.isEmpty())) {
                if (!(importDeclarations.isEmpty())) {
                    for (ImportDeclaration importDeclaration : importDeclarations) {
                        int index = importDeclaration.getNameAsString().lastIndexOf(".");
                        String className = importDeclaration.getNameAsString().substring(index + 1);
                        if (className.equals(oldClassName)) {
                            importDeclaration.setName(importDeclaration.getNameAsString().substring(0, index + 1) + newClassName);
                        }
                    }
                }
            }
        }
    }
        public static void main(String args[]){
            ClassImportReferRefactor.importRefactor("ClassVariRefactor","classVariRefactor");
        }
    }
