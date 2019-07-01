package ulits;

import java.io.File;
import java.io.IOException;

public class SaveModify {
    public static void main(String[] args) throws IOException {
        String path="H:\\test";
        String replacePath="C:\\Users\\Administrator\\Desktop\\测试源码\\RefactorTest";
        replaceContent(path,replacePath);
    }
    public static void replaceContent(String path, String replacePath) throws IOException {
           File file = new File(replacePath);
           deleteContent(file);
           file.mkdir();//重新新建文件夹
            DirCopy.copy(path,replacePath);
    }
    public static void deleteContent(File file){
        if(file == null){
            return;
        }
        else if(file.exists()){
            if(file.isFile()){
                file.delete(); //如果此路径代表的是文件，直接删除
            }
            else if(file.isDirectory()){
                String [] filesPath = file.list();
                if(filesPath == null){
                    file.delete();
                }
                else {
                    for(String filePath:filesPath){
                        File childFile = new File(file.getAbsolutePath() + "/" + filePath);
                        deleteContent(childFile);
                    }
                    file.delete();
                }
            }
        }

    }
}
