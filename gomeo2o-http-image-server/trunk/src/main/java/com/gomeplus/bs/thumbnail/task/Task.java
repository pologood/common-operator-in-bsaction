package com.gomeplus.bs.thumbnail.task;

import com.gomeplus.bs.thumbnail.db.WhiteListDao;
import com.gomeplus.bs.thumbnail.util.CacheUtils;

/**
 * Created by daisyli on 16/4/27.
 */
public class Task implements Runnable {

    private CacheUtils cacheUtils;
    
    public Task(CacheUtils cacheUtils) {
    	this.cacheUtils = cacheUtils;
    }

    public void run() {
    	 boolean result = cacheUtils.updateCache();
    	 int i = 0;
    	 while (i < 5 && !result) {
    		 WhiteListDao.connect();
    		 result = cacheUtils.updateCache();
    		 i++;
    	 }
    }
}
