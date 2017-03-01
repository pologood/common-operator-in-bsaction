package com.gomeplus.bs.thumbnail;

import com.gomeplus.bs.thumbnail.task.Task;
import com.gomeplus.bs.thumbnail.util.CacheUtils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by daisyli on 16/4/28.
 */
public class CrontabBootstrap implements Runnable {

    private ScheduledExecutorService executorService;
    
    private CacheUtils cacheUtils;
    
    public CrontabBootstrap(CacheUtils cacheUtils) {
        this.cacheUtils = cacheUtils;
    }

    public void run() {
        executorService = Executors.newScheduledThreadPool(1);
        // 5分钟执行一次
        executorService.scheduleAtFixedRate(new Task(cacheUtils), 0, 5, TimeUnit.MINUTES);
    }
}
