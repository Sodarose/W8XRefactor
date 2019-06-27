package ulits;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;

import java.util.List;
/*
* 查找import中的类。
* */
public class ImportDeclarationUtil {

    public static int importNumberUtil(CompilationUnit unit,String packageName,String className){
        List<ImportDeclaration> importDeclarations=unit.getImports();
        int importNumber=0;
        if (!(importDeclarations.isEmpty())) {
            for (ImportDeclaration importDeclaration : importDeclarations) {
                if (importDeclaration.getName().toString().equals(packageName+"."+className)) {
                    importNumber=importNumber+1;
                }
            }
            if(importNumber>0){
                return importNumber;
            }
        }
        return importNumber;
    }
}
