package com.w8x.web.ulits;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.system.ApplicationHome;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUlits {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileUlits.class);

    /**
     * 从项目jar目录读取文件
     */
    public static File readFileByJarContents(String path) {
        ApplicationHome home = new ApplicationHome(FileUlits.class);
        File homeDir = home.getSource();
        String filePath = homeDir.getParent() + File.separator + path;
        LOGGER.info("读取配置文件：" + filePath);
        File file = new File(filePath);
        return file;
    }

    public static String readFile(File file) {
        String source = null;
        try {
            source = new String(Files.readAllBytes(file.toPath()), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return source;
    }
}
