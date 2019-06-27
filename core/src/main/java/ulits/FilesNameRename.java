package ulits;

import model.Issue;
import model.JavaModel;
import model.Store;

import java.io.File;
import java.util.Map;

public class FilesNameRename {
    public static void nameRename(Issue issue,String oldFileName,String newFileName){
        JavaModel javaModel=issue.getJavaModel();
        String path=javaModel.getReadPath();
        File file=new File(path);
        File parentPath=file.getParentFile();
        File newFile=new File(parentPath+"\\"+newFileName+".java");
        file.renameTo(newFile);
        Map<String,JavaModel> modelMap=Store.javaModelMap;
       javaModel.setReadPath(parentPath+"\\"+newFileName+".java");
       String newPath=parentPath+"\\"+newFileName+".java";
       modelMap.remove(parentPath+"\\"+oldFileName+".java");
       modelMap.put(newPath,javaModel);
    }
}
