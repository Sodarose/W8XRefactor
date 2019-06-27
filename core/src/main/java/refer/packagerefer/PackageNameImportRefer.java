package refer.packagerefer;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import model.Issue;
import model.Store;

import java.util.List;

public class PackageNameImportRefer {
    public static void importNameRefer(Issue issue,String oldPackageName, String newPackageName){
        List<CompilationUnit> units= Store.javaFiles;
        for (CompilationUnit unit:units) {
            List<ImportDeclaration> importDeclarations=unit.getImports();
            if(!(importDeclarations.isEmpty())){
                for (ImportDeclaration importDeclaration:importDeclarations){
                    int index=importDeclaration.getNameAsString().lastIndexOf(".");
                    String packageName=importDeclaration.getNameAsString().substring(0,index);
                    if(packageName.equals(oldPackageName)){
                        String className=importDeclaration.getNameAsString().substring(index);
                        importDeclaration.setName(newPackageName+className);
                    }
                }
            }
        }
    }
}
