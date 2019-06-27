package ulits;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolUtils {
    //核心数
    private static final int CORE_POOL_ISZE = 4;
    //最大线程数
    private static final int MAX_POOL_SIZE = 2000;
    //存活时间
    private static final Long KEEP_ALIVE_TIME = 0L;

    //单例
    private static class ThreadPoolHolder {
        private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(CORE_POOL_ISZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, new SynchronousQueue<>());
    }

    public static ExecutorService getExectorService() {
        return ThreadPoolHolder.executor;
    }

    public static void execute(Runnable task) {
        ThreadPoolHolder.executor.execute(getExcepThread(task));
    }

    private static Thread getExcepThread(Runnable task) {
        Thread taskThread = null;
        if (task instanceof Thread) {
            taskThread = (Thread) task;
        } else {
            taskThread = new Thread(task);
        }

        if (taskThread.getUncaughtExceptionHandler() == null) {
            taskThread.setUncaughtExceptionHandler(new ThreadUncaughtException());
        } ;
        return taskThread;
    }

    public static void execute(Runnable runnable, Thread.UncaughtExceptionHandler exceptionHandler) {
        Thread thread = getExcepThread(runnable);
        if (exceptionHandler != null) {
            thread.setUncaughtExceptionHandler(exceptionHandler);
        }
        ThreadPoolHolder.executor.execute(thread);
    }

    static class ThreadUncaughtException implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            System.out.println(t.getId());
        }
    }
}
