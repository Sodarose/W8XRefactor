package io;

import model.JavaModel;
import model.Store;
import model.TransmissionThread;
import model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import ulits.ThreadPoolUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 文件工具
 */
public class FileUlits {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileUlits.class);

    public static String readFile(String filePath) {
        String source = null;
        try {
            source = new String(Files.readAllBytes(Paths.get(filePath)), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return source;
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

    public static void writeFile(String filePath, String source) {
        try {
            PrintWriter print = new PrintWriter(filePath);
            print.write(source);
            print.close();
        } catch (IOException e) {

        }
    }

    public static List<String> readFiles(String[] filePaths) {
        List<String> sources = null;
        try {
            sources = new ArrayList<>();
            for (String filePath : filePaths) {
                String source = new String(Files.readAllBytes(Paths.get(filePath)), "UTF-8");
                sources.add(source);
            }
        } catch (IOException e) {

        }
        return sources;
    }

    /**
     * 获得数据信息 已类加载的方式
     */
    public static InputStream readFilesByClassLoader(String path) throws IOException {
        return new ClassPathResource(path).getInputStream();
    }

    public static boolean saveProject() {
        boolean isSave = false;
        if (Store.rootNode == null) {
            return isSave;
        }
        if (Store.treeNodeMap == null) {
            return isSave;
        }
        // 遍历文件树 判断是否被改变 如果是文件夹 则改变文件夹名字 如果是文件则根据javamodel的比对 改变输出的位置
        for (Map.Entry<String, TreeNode> entry : Store.treeNodeMap.entrySet()) {
            TreeNode treeNode = entry.getValue();
            //如果文件名 被更改 那么首先更改文件名
            if (treeNode.isChange()) {
                File file = new File(treeNode.getStablePath());
                if (!file.exists()) {
                    continue;
                }
                file.renameTo(new File(treeNode.getRealPath()));
            }
            if (!treeNode.isFile()) {
                continue;
            }
            File file = new File(treeNode.getRealPath());
            if (!file.exists()) {
                LOGGER.warn("文件不存在:" + file.getPath());
                continue;
            }
            //根据文件
            JavaModel javaModel = Store.javaModelMap.get(treeNode.getRealPath());
            if (javaModel == null) {
                LOGGER.warn("找不到对应的javamodel：" + treeNode.getRealPath());
                continue;
            }
            TransmissionThread transmissionThread = new TransmissionThread(javaModel);
            ThreadPoolUtils.execute(transmissionThread);
        }
        isSave = true;
        return isSave;
    }
    public static File readSourceFile(String path) throws IOException{
        return new ClassPathResource(path).getFile();
    }
}
