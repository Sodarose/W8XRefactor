package ulits;

import model.Issue;
import refer.packagerefer.PackageNameImportRefer;
import refer.packagerefer.PackageNameReferRefactor;

public class PackageReferUtil {
    public static void referUtil(Issue issue,String oldPackageName, String newPackageName){
        PackageNameReferRefactor.nameRefactor(issue,oldPackageName,newPackageName);
        PackageNameImportRefer.importNameRefer(issue,oldPackageName,newPackageName);
        DirNameRename.nameRename(issue,oldPackageName,newPackageName);
    }
}
