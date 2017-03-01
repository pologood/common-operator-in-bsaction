package com.gomeplus.oversea.bs.service.user.dao;

import com.gomeplus.oversea.bs.service.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by shangshengfang on 2017/2/6.
 */
@Repository
public interface UserDao extends JpaRepository<User,Long> {

    /**
     * 根据国际电话区号和手机号查询
     * @param countryCode
     * @param mobile
     * @return
     */
    User findByCountryCodeAndMobile(String countryCode, String mobile);

    /**
     * 根据用户账号查询用户
     * @param userName
     * @return
     */
    User findByUserName(String userName);


    @Modifying
    @Transactional
    @Query("update User set nickname=:nickname,mobile=:mobile where id=:id")
    int updateUser(@Param("nickname")String nickname,@Param("mobile")String mobile,@Param("id")String id);
}
