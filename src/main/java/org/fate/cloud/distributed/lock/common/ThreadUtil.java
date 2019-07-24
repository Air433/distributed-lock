package org.fate.cloud.distributed.lock.common;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author ouyanggang
 * @Date 2019/7/24 - 15:19
 */
public class ThreadUtil {

    private static ExecutorService executorService =  Executors.newCachedThreadPool();

    public static void submit(Runnable runnable){
        executorService.submit(runnable);
    }
}
