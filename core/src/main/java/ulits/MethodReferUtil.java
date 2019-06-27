package ulits;

import refer.methodrefer.MethodReferRefactor;

public class MethodReferUtil {
    public static void referUtil(String oldMethodName,String newMethodName){
        MethodReferRefactor.nameReferRefactor(oldMethodName,newMethodName);
    }
}
