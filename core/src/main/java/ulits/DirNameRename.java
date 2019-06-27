package ulits;


import model.Issue;
import model.JavaModel;
import model.Store;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DirNameRename {
    public static void nameRename(Issue issue,String oldPackageName,String newPackageName){
       String[] oldName=oldPackageName.split("\\.");
       String[] newName=newPackageName.split("\\.");
       oldPackageName="";
       newPackageName="";
       for (int i=0;i<oldName.length;i++){
           if(oldPackageName.equals("")) {
               oldPackageName = oldPackageName + oldName[i];
           }
           else {
               oldPackageName=oldPackageName+"\\"+oldName[i];
           }
       }
       for (int i=0;i<newName.length;i++){
           if(newPackageName.equals("")) {
               newPackageName = newPackageName + newName[i];
           }else {
               newPackageName=newPackageName+"\\"+newName[i];
           }
       }
        JavaModel javaModel=issue.getJavaModel();
        String path=javaModel.getReadPath();
        File file=new File(path);
        String parentPath=file.getParent();
        int pathindex=parentPath.indexOf(oldPackageName);
        if(pathindex!=-1) {
            String newParentPath = parentPath.substring(0, pathindex) + newPackageName;
            renameDirectory(parentPath, newParentPath);
            Map<String, JavaModel> modelMap = Store.javaModelMap;
            List<String> fileList = readFildName(parentPath);
            for (String filePath : fileList) {
                int fileIndex = filePath.lastIndexOf("\\");
                String newFilePath = newParentPath +"\\"+ filePath.substring(fileIndex + 1);
                JavaModel model = modelMap.get(filePath);
                model.setReadPath(newFilePath);
                modelMap.remove(filePath);
                modelMap.put(newFilePath, javaModel);
            }
        }
    }
    public static List<String> readFildName(String path){
        List<String> fileList=new ArrayList<>();
        Map<String,JavaModel> javaModelMap= Store.javaModelMap;
        for (String file:javaModelMap.keySet()){
            BoyerMoore boyerMoore=new BoyerMoore();
            int pos=boyerMoore.match(file,path);
            if(pos!=-1){
                fileList.add(file);
            }
        }
        return fileList;
    }
    public static void renameDirectory(String fromDir,String toDir){
        File from=new File(fromDir);
        if(!from.exists()){
            System.out.println("不存在");
            return;
        }
        if(!from.isDirectory()){
            System.out.println("不是目录");
            return;
        }
        File to =new File(toDir);

        if(from.renameTo(to)){
            System.out.println("Success");
        }
        else {
            System.out.println("Error");
        }
    }
    /*
    public static void main(String[] args){
        nameRename("C:\\Users\\Administrator\\Desktop\\MyBlog");
    }
    */
}
