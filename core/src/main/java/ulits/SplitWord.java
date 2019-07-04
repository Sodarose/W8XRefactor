package ulits;

import io.FileUlits;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
            Map<Double,Integer> data=best_match(index,name);
            Double minKey=(Double) data.keySet().toArray()[0];
            cost.add(minKey);
        }
       int i=name.length();
       while (i>0){
           Map<Double,Integer> data=best_match(i,name);
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
    public Map<String,Double> listToMap(List<String> dataList){
        dataMap = new HashMap<String, Double>();
        for(int i = 0;i<dataList.size();i++){
            dataMap.put(dataList.get(i),Math.log((i+1.0)*Math.log(dataList.size())));
        }
        return dataMap;
    }
    public Map<Double, Integer> best_match(int index, String name){
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
        String name="testmodel";
        List<String> nameList=splitWord.split(name);
        Collections.reverse(nameList);
        for (String s:nameList){
            System.out.println(s);
        }
    }
}
