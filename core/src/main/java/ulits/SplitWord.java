package ulits;

import io.FileUlits;
import java.io.*;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;

public class SplitWord {
    private final Pattern SPLITRE= Pattern.compile("[^a-zA-Z0-9']+");
    private List<Double> cost = new ArrayList<Double>();
    private int maxlength;
    private Map<String,Double> dataMap;
    private List<String> out = new ArrayList<>();
    public  List<String> split(String name) throws IOException {
        cost.add(0.0);
        List<String> dataList = readData();
        dataMap = listToMap(dataList);
       for(int index=1;index<=name.length();index++){
            Map<Double,Integer> data=bestMatch(index,name);
            Double minKey=(Double) data.keySet().toArray()[0];
            cost.add(minKey);
        }
       int i=name.length();
       while (i>0){
           Map<Double,Integer> data=bestMatch(i,name);
           Double d=(Double) data.keySet().toArray()[0];
           Integer k =(Integer) data.values().toArray()[0];
           if(d.equals(cost.get(i))) {
               boolean newToken = true;
               if(!name.substring(i-k,i).equals("'")){
                   if(out.size()>0){

                   }
               }
               if(newToken){
                   out.add(name.substring(i-k,i));
               }
               i -= k;
           }

       }
         return out;
    }

    public List<String> readData() throws IOException {
        List<String> dataList = new ArrayList<>();
        InputStreamReader fileReader = new InputStreamReader(FileUlits.readFilesByClassLoader("static/wordninja_words.txt"));
        BufferedReader bufferedReader= new BufferedReader(fileReader);
        String data = null;
        while ((data = bufferedReader.readLine()) != null  ){
            dataList.add(data);
        }
        maxlength=dataList.get(maxList(dataList)).length();
        bufferedReader.close();
        fileReader.close();
        return  dataList;
    }

//    public List<String> readData() throws IOException {
//        int maxBtye = 1024;
//        List<String> dataList = new ArrayList<>();
//        File file=FileUlits.readSourceFile("static/wordninja_words.txt");
//        //获取文件管道
//        FileChannel fc = new FileInputStream(file).getChannel();
//        byte changeLine = 10;
//        //换行符前面字符组合，可解决乱码
//        ByteBuffer rBuffer = ByteBuffer.allocate(maxBtye);
//        ByteBuffer elseBuffer = ByteBuffer.allocate(maxBtye*3);
//        //将缓冲区中数据读到字符数组
//        byte[] bs=null;
//        while (fc.read(rBuffer)!=-1){
//            // 根据读取到的字节数确定即将获取的字节数组大小
//            bs = new byte[rBuffer.position()];
//            rBuffer.rewind();
//            // 相对读，从position位置读取一个byte[]
//            rBuffer.get(bs);
//            // 清楚缓冲区，供下次使用
//            rBuffer.clear();
//            for (byte b : bs) {
//                // 遭遇换行符
//                if (b == changeLine) {
//                    // 字符缓冲区位置，用于确定字节数组大小
//                    int byteSize = elseBuffer.position();
//                    // 确定出字节数组大小
//                    byte[] byteLine = new byte[byteSize];
//                    // position=0 准备读取数据
//                    elseBuffer.rewind();
//                    // 从position开始读取byteLine.length个字节
//                    elseBuffer.get(byteLine);
//                    // 如果是 剔除 /r 前的 /n 后转换为字符串
//                    String line = new String(byteLine, 0, byteLine.length - 1);
//                    //    添加到返回集合中
//                    dataList.add(line);
//                    elseBuffer.clear();
//                    continue;
//                }
//                try {
//                    // 缓存住未匹配上的数据
//                    elseBuffer.put(b);
//                    //    由于提前确定的字节缓冲区太小导致错误
//                } catch (BufferOverflowException e) {
//                   e.printStackTrace();
//                    return null;
//                }
//
//            }
//        }
//        //    循环完毕，处理剩余数据,注释同上
//        if (elseBuffer.position() > 0) {
//            int byteSize = elseBuffer.position();
//            byte[] byteLine = new byte[byteSize];
//            elseBuffer.rewind();
//            elseBuffer.get(byteLine);
//            String line = new String(byteLine, 0, byteLine.length);
//            dataList.add(line);
//            elseBuffer.clear();
//        }
//        fc.close();
//        rBuffer.clear();
//        maxlength=dataList.get(maxList(dataList)).length();
//        return dataList;
//    }
    public Map<String,Double> listToMap(List<String> dataList){
        dataMap = new HashMap<String, Double>();
        for(int i = 0;i<dataList.size();i++){
            dataMap.put(dataList.get(i),Math.log((i+1.0)*Math.log(dataList.size())));
        }
        return dataMap;
    }
    public Map<Double, Integer> bestMatch(int index, String name){
        List<Double> dataList = new ArrayList<>();
        int maxIndex=Math.max(0,index-maxlength);
        for(int i=maxIndex;i<index;i++){
            dataList.add(cost.get(i));
        }
        Collections.reverse(dataList);
        Map<Double,Integer> matchMap = new HashMap<Double, Integer>(dataList.size());
        for(int i=0;i<dataList.size();i++){
            matchMap.put(dataList.get(i)+dataMap.getOrDefault(name.substring(index-i-1,index).toLowerCase(),Math.pow(9,999)),i+1);
        }
        Set<Double> keySet=matchMap.keySet();
       Double minKey=Collections.min(keySet);
        Integer minValue = matchMap.get(minKey);
        return new HashMap<Double,Integer>(){{
            put(minKey,minValue);
        }};
    }
    public int maxList(List<String> dataList){
       int index=0;
       for(int i=0;i<dataList.size();i++){
           if(dataList.get(i).length()>dataList.get(index).length()){
               index=i;
           }
       }
       return index;
    }
    public static void main(String args[]) throws IOException{
        SplitWord splitWord=new SplitWord();
        String name="wethepeopleoftheunitedstatesinordertoformamoreperfectunionestablishjusticeinsuredomestictranquilityprovideforthecommondefencepromotethegeneralwelfareandsecuretheblessingsoflibertytoourselvesandourposteritydoordainandestablishthisconstitutionfortheunitedstatesofamerica";
        List<String> nameList=splitWord.split(name);
        Collections.reverse(nameList);
        for (String s:nameList){
            System.out.println(s);
        }
    }
}
