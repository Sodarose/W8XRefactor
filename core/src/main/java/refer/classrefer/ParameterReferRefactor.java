package refer.classrefer;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import model.Store;

import java.util.List;

public class ParameterReferRefactor {
    public static void nameReferRefactor(String oldClassName,String newClassName) {
        List<CompilationUnit> units = Store.javaFiles;
        for (CompilationUnit unit : units) {
            List<Parameter> parameterList = unit.findAll(Parameter.class);
            if (!(parameterList.isEmpty())) {
                for (Parameter parameter : parameterList) {
                    String name = parameter.getType().getClass().getName().toString();
                    if (name.equals("com.github.javaparser.ast.type.ClassOrInterfaceType")) {
                        ClassOrInterfaceType type = (ClassOrInterfaceType) parameter.getType();
                        if (type.getNameAsString().equals(oldClassName)) {
                            type.setName(newClassName);
                            parameter.setType(type);
                        }
                    }
                }
            }
        }
    }
    public static void main (String[] args){
        ParameterReferRefactor.nameReferRefactor("ClassVariRefactor", "classVariRefactor");
    }
}
