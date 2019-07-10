package ulits;
import model.Store;

import java.io.*;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class DirCopy {

    public static void copy(String path,String copyPath) throws IOException {
        File filePath = new File(path);
        if(filePath.isHidden()){
            return;
        }
        if(filePath.isDirectory()){
            File[] list = filePath.listFiles();
            for(int i = 0 ; i < list.length;i++){
                String newPath = path + File.separator + list[i].getName();
                String newCopyPath= copyPath + File.separator + list[i].getName();
                File newFile = new File(copyPath);
                if(!newFile.exists()){
                    newFile.mkdir();
                }
               //Store.pathMap.put(copyPath,path);
                copy(newPath,newCopyPath);
            }
        }
        else if(filePath.isFile()){
              File file= new File(path);
              File copyFile=new File(copyPath);
              //Store.pathMap.put(copyPath,path);
            Files.copy(file.toPath(),copyFile.toPath());
            }

        else {
            System.out.println("请输入正确的文件名或路径名");
        }
        }

        public static String  dirCopy(String filePath){
            Store.savePath=filePath;
            int nameIndex = filePath.lastIndexOf("\\");
            //String copyPath="c:\\w8x\\"+filePath.substring(nameIndex+1);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");//设置日期格式
            String modifypath = "c:\\w8x\\temp\\"+filePath.substring(nameIndex+1)+"\\"+df.format(new Date()).toString();
            Store.modifyPath=modifypath;
           // Store.pathMap = new HashMap<>();

            /*File file=new File(copyPath);
            if(file.exists()){
                SaveModify.deleteContent(file);
            }
            try {
                copy(filePath,copyPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return copyPath;*/
            return filePath;
        }
}
