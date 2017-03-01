package com.gomeplus.bs.thumbnail.util;

import java.sql.SQLException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.gomeplus.bs.thumbnail.db.WhiteListDao;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by daisyli on 16/4/27.
 */
@Slf4j
public class CacheUtils {

    private final static CacheUtils instance = new CacheUtils();

    private CacheUtils() {}

    public static CacheUtils getInstance() {
        return instance;
    }

    private ConcurrentHashMap<String, Boolean> cache = null;

    private WhiteListDao whiteListDao = WhiteListDao.getInstance();

    public void initCache(Set<String> whiteSet) {
        cache = new ConcurrentHashMap<String, Boolean>();
        for (String dimension : whiteSet) {
            cache.put(dimension, true);
        }

    }

    public boolean updateCache() {
        try {
            Set<String> whiteSet = whiteListDao.getWhiteSet();
            initCache(whiteSet);
            return true;
        } catch (SQLException e) {
        	log.error("updateCache SQLException" + e.getMessage());
        	return false;
        }
    }

    public boolean getCache(String params) {
    	if (cache.get(params) == null) {
    		return false;
    	}
        return true;
    }
}
