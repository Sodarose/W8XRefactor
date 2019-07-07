package io;

import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 *  文件工具
 * */
public class FileUlits {

    public static String readFile(String filePath){
      String source = null;
      try{
        source = new String(Files.readAllBytes(Paths.get(filePath)),"UTF-8");
      }catch (IOException e){
        e.printStackTrace();
      }
      return source;
    }

    public static String readFile(File file){
        String source = null;
        try{
            source = new String(Files.readAllBytes(file.toPath()),"UTF-8");
        }catch (IOException e){
            e.printStackTrace();
        }
        return source;
    }

    public static void writeFile(String filePath,String source){
      try{
        PrintWriter print = new PrintWriter(filePath);
        print.write(source);
        print.close();
      }catch (IOException e){

      }
    }

    public static List<String> readFiles(String [] filePaths){
      List<String> sources = null;
      try{
          sources = new ArrayList<>();
        for (String filePath:filePaths) {
          String source = new String(Files.readAllBytes(Paths.get(filePath)),"UTF-8");
          sources.add(source);
        }
      }catch (IOException e){

      }
      return sources;
    }

    /**
     *  获得数据信息 已类加载的方式
     * */
    public static InputStream readFilesByClassLoader(String path) throws IOException {
        return new ClassPathResource(path).getInputStream();
    }
    public static File readSourceFile(String path) throws IOException{
        return new ClassPathResource(path).getFile();
    }
}
