package ulits;

import java.io.File;
import java.util.LinkedList;

public class FindFile {
    /**
     * 输入文件位置，获取该文件夹下面所有文件路径
     */
    LinkedList<StringBuffer> fileName = new LinkedList<StringBuffer>();
    static  LinkedList<StringBuffer> dname = new LinkedList<StringBuffer>();
    public LinkedList<StringBuffer> getFindFile(StringBuffer s){
        this.getFileName(new File(s.toString()));
        this.whilename();
        return fileName;
    }
    private void getFileName(File file){
        if(file.listFiles()!=null&&file.listFiles().length!=0){
                File[] files = file.listFiles();//获取目录列表
                for(File file2:files){
                    /**
                     * 判断这个是文件还是目录
                     */
                    if(file2.isFile()){
                        StringBuffer s1 = new StringBuffer(file2.toString());
                        fileName.add(s1);
                    }
                    else if(file2.isDirectory()){
                        StringBuffer s2 = new StringBuffer(file2.toString());
                        dname.add(s2);
                    }
                    else {

                    }
                }
        }

    }
    private void whilename(){
        while (dname.size()!=0){
            for (int i=0;i<dname.size();i++){
                this.getFileName(new File(dname.get(i).toString()));
                dname.remove(i);
            }
        }
    }
}
