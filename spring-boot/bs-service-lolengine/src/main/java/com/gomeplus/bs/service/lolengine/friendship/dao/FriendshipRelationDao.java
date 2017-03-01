/**
 * gomeo2o.com Copyright (c) 2015-2025 All Rights Reserved.
 * 
 * @Description: TODO
 * @author: lijinxuan
 * @date: 2015年4月1日 下午3:39:25
 */
package com.gomeplus.bs.service.lolengine.friendship.dao;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @Description 好友关系操作类
 * @author mojianli
 * @date 2016年11月22日 上午11:24:01
 */
@Repository("friendshipDao")
public class FriendshipRelationDao {

    @Autowired
    private SqlSession sqlsession;

    /**
     * @Description 根据userId查所有好友id
     * @author mojianli
     * @date 2016年11月22日 下午3:34:37
     * @param pageParam
     * @param userId
     * @return
     */
    public Set<Long> getFriendshipsByUserId(Long userId) {

        Set<Long> result = new HashSet<Long>();

        HashMap<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("newUserId", userId);
        paramMap.put("status", 0);

        // 获取数据集
        List<Long> list = sqlsession.selectList("getFriendshipList", paramMap);

        if (null != list && list.size() > 0) {
            result.addAll(list);
        }

        return result;
    }
}
