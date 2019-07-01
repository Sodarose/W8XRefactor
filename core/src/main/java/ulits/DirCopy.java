package ulits;
import java.io.*;
import java.nio.file.Files;

public class DirCopy {
    public static void copyDir(String oldPath,String newPath) throws IOException{
        copy(oldPath,newPath);
    }
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
                copy(newPath,newCopyPath);
            }
        }
        else if(filePath.isFile()){
              File file= new File(path);
              File copyFile=new File(copyPath);
            Files.copy(file.toPath(),copyFile.toPath());
            }

        else {
            System.out.println("请输入正确的文件名或路径名");
        }
        }
}
