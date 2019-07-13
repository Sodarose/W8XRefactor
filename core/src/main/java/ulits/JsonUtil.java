package ulits;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import model.JsonObject;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class JsonUtil {
    /**
     * 单位缩进字符串。
     */
    private static String SPACE = "   ";
    public static void createJson(List<JsonObject> jsonObjects, String filepath) throws IOException {
        String fullPath = filepath+ File.separator+"info.json";
        File file = new File(fullPath);
        if(!file.getParentFile().exists()){
            //如果父目录不存在;创建父目录
            file.getParentFile().mkdir();
        }
        if(file.exists()){
            file.delete();
        }
        file.createNewFile();
        //JSONObject root = new JSONObject();
        JSONArray array = new JSONArray();
        if(!jsonObjects.isEmpty()) {
            for (JsonObject object : jsonObjects) {
                if(object != null) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("modifyPath", object.getModifyPath());
                    jsonObject.put("copyPath",object.getCopyPath());
                    jsonObject.put("FileName", object.getFileName());
                    jsonObject.put("FileStatus", object.getFileStatus());
                    //jsonObject.put("javamodel", Store.javaModelMap.get(object.getCopyPath()));
                    array.add(jsonObject);
                }
            }
            //root.put("content",array);
            String jsonString = formatJson(array.toString());
            Writer write = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
            write.write(jsonString);
            write.flush();
            write.close();
        }
    }
    /**
     * 返回格式化JSON字符串。
     *
     * @param json 未格式化的JSON字符串。
     * @return 格式化的JSON字符串。
     */
    public static String formatJson(String json) {
        StringBuffer result = new StringBuffer();
        int length = json.length();
        int number = 0;
        char key = 0;

        // 遍历输入字符串。
        for (int i = 0; i < length; i++) {
            // 1、获取当前字符。
            key = json.charAt(i);

            // 2、如果当前字符是前方括号、前花括号做如下处理：
            if ((key == '[') || (key == '{')) {
                // （1）如果前面还有字符，并且字符为“：”，打印：换行和缩进字符字符串。
                if ((i - 1 > 0) && (json.charAt(i - 1) == ':')) {
                    result.append('\n');
                    result.append(indent(number));
                }

                // （2）打印：当前字符。
                result.append(key);

                // （3）前方括号、前花括号，的后面必须换行。打印：换行。
                result.append('\n');

                // （4）每出现一次前方括号、前花括号；缩进次数增加一次。打印：新行缩进。
                number++;
                result.append(indent(number));

                // （5）进行下一次循环。
                continue;
            }

            // 3、如果当前字符是后方括号、后花括号做如下处理：
            if ((key == ']') || (key == '}')) {
                // （1）后方括号、后花括号，的前面必须换行。打印：换行。
                result.append('\n');

                // （2）每出现一次后方括号、后花括号；缩进次数减少一次。打印：缩进。
                number--;
                result.append(indent(number));

                // （3）打印：当前字符。
                result.append(key);

                // （4）如果当前字符后面还有字符，并且字符不为“，”，打印：换行。
                if (((i + 1) < length) && (json.charAt(i + 1) != ',')) {
                    result.append('\n');
                }

                // （5）继续下一次循环。
                continue;
            }

            // 4、如果当前字符是逗号。逗号后面换行，并缩进，不改变缩进次数。
            if ((key == ',')) {
                result.append(key);
                result.append('\n');
                result.append(indent(number));
                continue;
            }

            // 5、打印：当前字符。
            result.append(key);
        }

        return result.toString();
    }

    /**
     * 返回指定次数的缩进字符串。每一次缩进三个空格，即SPACE。
     *
     * @param number 缩进次数。
     * @return 指定缩进次数的字符串。
     */
    private static String indent(int number) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < number; i++) {
            result.append(SPACE);
        }
        return result.toString();
    }
    public static List<JsonObject> readJson(String path) throws IOException{
        List<JsonObject> jsonObjects=new ArrayList<>();
        JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream(path)));
        Gson gson = new GsonBuilder().create();
        reader.beginArray();
        while (reader.hasNext()){
            JsonObject jsonObject = gson.fromJson(reader,JsonObject.class);
            jsonObjects.add(jsonObject);
        }
        reader.close();
        return jsonObjects;
    }
    public static boolean savemodify(String path) throws IOException {
        boolean modifyFlag=true;
        boolean copyFlag=true;
        String  jsonPath = path+File.separator+"info.json";
        List<JsonObject> jsonObjects=readJson(jsonPath);
        for (JsonObject jsonObject:jsonObjects) {
            if (jsonObject.getFileStatus().equals("file")) {
                String modifyPath = jsonObject.getModifyPath();
                String copyPath = jsonObject.getCopyPath();
                File modifyFile = new File(modifyPath);
                //File copyFile = new File(copyPath);
                File file = new File(copyPath);
                if (modifyFile.isFile() ) {
                    if (modifyFile.exists()) {
                        modifyFile.delete();
                    }
                    //if (copyFile.exists()) {
                      //  copyFile.delete();
                    //}
                    modifyPath = modifyPath.substring(0, modifyPath.lastIndexOf("\\") + 1) + jsonObject.getFileName();
                    //copyPath = copyPath.substring(0, copyPath.lastIndexOf("\\") + 1) + jsonObject.getFileName();
                    Files.copy(file.toPath(), new File(modifyPath).toPath());
                    //Files.copy(file.toPath(), new File(copyPath).toPath());
                }
            }
            if(jsonObject.getFileStatus().equals("dir")){
                String modifyPath = jsonObject.getModifyPath();
                //String copyPath = jsonObject.getCopyPath();
                File modifyFile = new File(modifyPath);
                //File copyFile = new File(copyPath);
                modifyFlag=modifyFile.renameTo(new File(modifyPath.substring(0,modifyPath.lastIndexOf("\\")+1)+jsonObject.getFileName()));
               //copyFlag=copyFile.renameTo(new File(copyPath.substring(0,copyPath.lastIndexOf("\\")+1)+jsonObject.getFileName()));
            }
        }
       return modifyFlag;
    }
    public static void main(String args[]) throws IOException {
        String  jsonPath = "C:\\w8x\\temp\\RefactorTest\\2019-07-06 20-22-32\\info.json";
        String path="C:\\w8x\\temp\\RefactorTest\\2019-07-06 20-22-32\\";
        List<JsonObject> jsonObjects=readJson(jsonPath);
        for (JsonObject jsonObject:jsonObjects) {
            if (jsonObject.getFileStatus().equals("file")) {
                String modifyPath = jsonObject.getModifyPath();
                //String copyPath = jsonObject.getCopyPath();
                File modifyFile = new File(modifyPath);
                //File copyFile = new File(copyPath);
                File file = new File(path + jsonObject.getFileName());
                if (modifyFile.isFile() ) {
                    if (modifyFile.exists()) {
                        modifyFile.delete();
                    }
                   // if (copyFile.exists()) {
                     //   copyFile.delete();
                    //}
                    modifyPath = modifyPath.substring(0, modifyPath.lastIndexOf("\\") + 1) + jsonObject.getFileName();
                    //copyPath = copyPath.substring(0, copyPath.lastIndexOf("\\") + 1) + jsonObject.getFileName();
                    Files.copy(file.toPath(), new File(modifyPath).toPath());
                    //Files.copy(file.toPath(), new File(copyPath).toPath());
                }
            }
            if(jsonObject.getFileStatus().equals("dir")){
                String modifyPath = jsonObject.getModifyPath();
               // String copyPath = jsonObject.getCopyPath();
                File modifyFile = new File(modifyPath);
                //File copyFile = new File(copyPath);
                modifyFile.renameTo(new File(modifyPath.substring(0,modifyPath.lastIndexOf("\\")+1)+jsonObject.getFileName()));
               // copyFile.renameTo(new File(copyPath.substring(0,copyPath.lastIndexOf("\\")+1)+jsonObject.getFileName()));

            }
        }

    }
}
