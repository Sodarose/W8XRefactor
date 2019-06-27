package ulits;

import refer.classrefer.FieldNameReferRefactor;

public class VariableReferUtil {
    public static void referUtil(String oldVariName,String newVariName){
        FieldNameReferRefactor.nameReferRefactor(oldVariName,newVariName);
    }
    public static void VariNameUtil(String oldVariName,String newVariName){
        VariableReferUtil.referUtil(oldVariName,newVariName);
    }
}
