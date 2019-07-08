package ulits;


import model.*;


import javax.xml.soap.Node;
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
            Map<String, TreeNode> treeNode = Store.treeNodeMap;
            TreeNode dirNode = treeNode.get(parentPath);
            dirNode.setFileName(newName[newName.length-1]);
            JsonObject jsonObject = new JsonObject();
            String modifyPath = Store.pathMap.get(parentPath);
            jsonObject.setModifyPath(modifyPath);
            jsonObject.setCopyPath(parentPath);
            jsonObject.setFileStatus("dir");
            jsonObject.setFileName(newName[newName.length-1]);
            Store.jsonObjectList.add(jsonObject);
            treeNode.remove(parentPath.substring(0, pathindex)+oldPackageName);
            treeNode.put(newParentPath,dirNode);
            //renameDirectory(parentPath, newParentPath);
            Map<String, JavaModel> modelMap = Store.javaModelMap;
            List<String> fileList = readFildName(parentPath);
            for (String filePath : fileList) {
                int fileIndex = filePath.lastIndexOf("\\");
                String newFilePath = newParentPath +"\\"+ filePath.substring(fileIndex + 1);
                JavaModel model = modelMap.get(filePath);
                model.setReadPath(newFilePath);
                modelMap.remove(filePath);
                modelMap.put(newFilePath,model);
                String modifypath=Store.pathMap.get(filePath);
                Store.pathMap.remove(filePath);
                Store.pathMap.put(newFilePath,modifypath);
                //System.out.println(filePath);
                //System.out.println(newFilePath);
                TreeNode fileNode=treeNode.get(filePath);
                //System.out.println(fileNode.getRealPath());
                fileNode.setRealPath(newFilePath);
                treeNode.remove(filePath);
                treeNode.put(newFilePath,fileNode);

            }
            Store.treeNodeMap = treeNode;
            Store.javaModelMap=modelMap;
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
    /*
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
    }*/
    /*
    public static void main(String[] args){
        nameRename("C:\\Users\\Administrator\\Desktop\\MyBlog");
    }
    */
}
