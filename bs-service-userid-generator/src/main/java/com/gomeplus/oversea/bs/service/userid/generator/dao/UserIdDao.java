package com.gomeplus.oversea.bs.service.userid.generator.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * 2017/1/13
 *
 * @author erdaoya
 * @since 1.0
 */
@Repository
public class UserIdDao {

    @Autowired
    private SqlSession sqlSession;

    @Transactional
    public long nextId() {
        sqlSession.update("increaseId");
        return sqlSession.selectOne("getId");
    }
}
