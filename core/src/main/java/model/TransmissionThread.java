package model;


import formatter.Formatter;

import java.io.*;

/**
 * 写文件线程
 */
public class TransmissionThread implements Runnable {

    private JavaModel javaModel;

    public TransmissionThread(JavaModel javaModel) {
        this.javaModel = javaModel;
    }

    @Override
    public void run() {
        String modifyPath = javaModel.getReadPath();
        String fileName = Store.treeNodeMap.get(javaModel.getReadPath()).getFileName();
        String fileStatus = "file";
        JsonObject jsonObject = new JsonObject();
        jsonObject.setModifyPath(modifyPath);
        //jsonObject.setCopyPath(javaModel.getReadPath());
        jsonObject.setFileName(fileName);
        jsonObject.setFileStatus(fileStatus);
        Store.jsonObjectList.add(jsonObject);
        String filePath = Store.modifyPath + "\\" + fileName;
        File file = new File(filePath);
        if (!file.getParentFile().exists()) {
            //如果父目录不存在;创建父目录
            file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        /*if (!file.exists()) {
            return;
        }*/
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            writer.write(Formatter.format(javaModel.getUnit().toString(), Store.codestyle));
        } catch (IOException e) {

        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {

                }
            }
        }
    }
}