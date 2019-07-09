package ulits;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class CompareFile {
    //file1用来放文件的绝对路径
    LinkedList<StringBuffer> file1 = new LinkedList<>();
    static String savePath;
    FindFile findFile = new FindFile();
    static Map<String,String> reMap = new HashMap<String,String>();
    private Map<String,String> getMap(StringBuffer path1){
        /**
         * 获取文件路径，给map赋值，key是文件相对路径，v是文件md5
         */
        Map<String,String> map = new HashMap<>();
        file1 = findFile.getFindFile(path1);
        //截取多余的目录名
        IsOS os = new IsOS();
        StringBuffer stringBuffer=null;
        int stLength =0;
        if (os.isOs().equals("linux")) {
            stringBuffer =new StringBuffer(path1.substring(0, path1.lastIndexOf("/")));
            stLength = stringBuffer.length();
        }else if (os.isOs().equals("win")) {
            stringBuffer =new StringBuffer(path1.substring(0, path1.lastIndexOf("\\")));
            stLength = stringBuffer.length();
        }else {
            return null;
        }
        int i =0;
        for (StringBuffer stringBuffer1:file1){
            /*
             * 获取文件相对根目录
             *data-file-name,则获取neme目录下所有的文件名，不加上data-file
             */
            StringBuffer stringBuffer2 = new StringBuffer();
            stringBuffer2 = new StringBuffer(stringBuffer1.substring(stLength+1, stringBuffer1.length()));
            map.put(stringBuffer2.toString(), getMD5(new File(stringBuffer1.toString())));
        }
        return map;
    }
    public String getMD5(File file){
        /**
         * 输入一个文件类，返回文件的md5
         */
        BigInteger bigInteger = null;
        try {

            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            FileInputStream fin = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int fr;
            while((fr=fin.read(buffer))!=-1) {
                messageDigest.update(buffer,0,fr);
            }
            bigInteger = new BigInteger(1, messageDigest.digest());

        } catch (Exception e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }
//		System.out.println("DM5:"+bigInteger.toString(16));
        return bigInteger.toString(16);

    }
    public void comparePath(String path,String copyPath){
        CompareFile compareFile=new CompareFile();
        CompareFile compareFile1=new CompareFile();
        Map<String,String> map1 = compareFile1.getMap(new StringBuffer(path));
        Map<String,String> map2 = compareFile.getMap(new StringBuffer(copyPath));
        if(map1!=null&&map2!=null){
            for (String key:map1.keySet()){
                if(map2.get(key) == null){
                        System.out.println("不存在该路径");
                }else if(! map2.get(key).equals(map1.get(key))){
                    System.out.println("文件不相同");
                }
                else {

                }
            }
        }
    }
    public  static void main(String agrs[]){
        String path="C:\\Users\\Administrator\\Desktop\\测试源码\\RefactorTest";
        String copyPath="C:\\w8x\\RefactorTest";
        CompareFile compareFile = new CompareFile();
        compareFile.comparePath(path,copyPath);
    }
}
