package ulits;

import refer.classrefer.*;

public class ClassReferUtil {
    public static void referUtil(String oldClassName,String newClassName){
        FieldNameReferRefactor.nameReferRefactor(oldClassName,newClassName);
        ListFieldNameReferRefactor.nameReferRefactor(oldClassName,newClassName);
        MapFieldNameReferRefactor.nameReferRefactor(oldClassName,newClassName);
        SetFieldNameReferRefactor.nameReferRefactor(oldClassName,newClassName);
        ParameterReferRefactor.nameReferRefactor(oldClassName,newClassName);
        VariNameReferRefactor.nameReferRefactor(oldClassName,newClassName);
        ListVariNameReferRefactor.nameReferRefactor(oldClassName,newClassName);
        MapVariNameReferRefactor.nameReferRefactor(oldClassName,newClassName);
        SetVariNameReferRefactor.nameReferRefactor(oldClassName,newClassName);
        InterfaceImplementRefactor.implementRefactor(oldClassName,newClassName);
        ClassExtendsReferRefactor.extendsRefactor(oldClassName,newClassName);
        ClassImportReferRefactor.importRefactor(oldClassName,newClassName);

    }
}
