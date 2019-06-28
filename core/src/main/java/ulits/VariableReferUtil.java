package ulits;

import refer.variablerefer.FieldNameReferRefactor;
import refer.variablerefer.VariNameReferRefactor;

public class VariableReferUtil {
    public static void FieldNameUtil(String oldVariName,String newVariName){
        FieldNameReferRefactor.nameReferRefactor(oldVariName,newVariName);
        refer.variablerefer.VariNameReferRefactor.nameExprRefactor(oldVariName,newVariName);
    }
    public static void VariableNameUtil(String olaVariName,String newVariName){
        VariNameReferRefactor.nameExprRefactor(olaVariName,newVariName);
    }
}
