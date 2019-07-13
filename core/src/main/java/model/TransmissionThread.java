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
        File file = new File(javaModel.getReadPath());
        if (!file.exists()) {
            return;
        }
        PrintWriter printWriter = null;
        try {
            printWriter = new PrintWriter(file);
            printWriter.write(Formatter.format(javaModel.getUnit().toString(), Store.codestyle));
        } catch (IOException e) {

        } finally {
            if (printWriter != null) {
                printWriter.close();
            }
        }
    }
}