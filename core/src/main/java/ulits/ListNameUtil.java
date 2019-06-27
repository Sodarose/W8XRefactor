package ulits;

import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.type.ClassOrInterfaceType;


public class ListNameUtil {
    public static VariableDeclarator NameUtil(VariableDeclarator node,String newClassName,String oldClassName){
        ClassOrInterfaceType exprType = (ClassOrInterfaceType) node.getType();
        ClassOrInterfaceType nameType = (ClassOrInterfaceType) exprType.getTypeArguments().get().get(0);
        nameType.setName(newClassName);
        return null;
    }
}
