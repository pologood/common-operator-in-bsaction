package com.gomeplus.oversea.bs.service.user.dao;

import com.gomeplus.oversea.bs.service.user.entity.UserSnsBind;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by zhangxinxing on 2017/2/14.
 */
@Repository
public interface UserSnsBindDao extends JpaRepository<UserSnsBind,Long> {
    /**
     * 根据第三方账户id查询绑定记录
     * @param snsUserId
     * @return
     */
    UserSnsBind findBySnsUserId(String snsUserId);
}
