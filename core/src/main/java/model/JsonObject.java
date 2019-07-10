package model;

import lombok.Data;

@Data
public class JsonObject {
    private String modifyPath; //写入文件路径
    //private String copyPath; //副本文件路径
    private String FileName ; //文件名
    private String FileStatus;//区别文件和文件夹
}
