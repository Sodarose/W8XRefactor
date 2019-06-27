package ulits;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SplitName {

    public static List<String> split(String name) throws IOException {
        String copyname = name.toLowerCase();
        String flagName = "";
        List<String> dataList = new ArrayList<String>();
        dataList = readData();
        List<String> nameList = new ArrayList<>();
        Map<Integer, String> nameMap = new ConcurrentHashMap<Integer, String>();
        for (String data : dataList) {
            if (data.trim().length() <= copyname.trim().length()) {
                BoyerMoore boyerMoore = new BoyerMoore();
                int pos = boyerMoore.match(copyname.trim(), data.trim());
                if (pos != -1) {
                    nameMap.put(pos, copyname.substring(pos, pos + data.length()));
                }
            }
        }
        nameMap = sortMapByKey(nameMap);
        if (nameMap != null) {
            for (Integer key : nameMap.keySet()) {
                nameList.add(nameMap.get(key));
            }
            for (String content : nameList) {
                flagName = flagName + content.trim();
            }
            if (flagName.equals(copyname)) {
                return nameList;
            }
        }
        return nameList;
    }
//SplitName.class.getResource("/static/data.txt").toString()
    public static List<String> readData() throws FileNotFoundException, IOException {
        List<String> nameList = new ArrayList<String>();
       InputStreamReader fileReader = new InputStreamReader(SplitName.class.getResource("/static/data").openStream());
       BufferedReader br = new BufferedReader(fileReader);
        String data = null;
        while ((data = br.readLine()) != null) {
            nameList.add(data);
        }
        br.close();
        fileReader.close();
        return nameList;
    }

    /*public static List<String> readText() throws FileNotFoundException, IOException {
        List<String> nameList=new ArrayList<String>();
        FileReader reader = new FileReader("core/src/main/resources/static/test.txt");
        BufferedReader br = new BufferedReader(reader);
        String data=null;
        while ((data = br.readLine()) != null) {
            nameList.add(data);
        }
        br.close();
        reader.close();
        return nameList;
    }*/
    public static Map<Integer, String> sortMapByKey(Map<Integer, String> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        Map<Integer, String> sortMap = new TreeMap<>(new MapKeyComparator());
        sortMap.putAll(map);
        Map<Integer, String> resultMap = new ConcurrentHashMap<Integer, String>(sortMap);
        return resultMap;
    }
}

class MapKeyComparator implements Comparator<Integer> {

    @Override
    public int compare(Integer o1, Integer o2) {
        return o1.compareTo(o2);
    }
}
