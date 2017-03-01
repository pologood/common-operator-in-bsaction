package com.gomeplus.oversea.bs.service.user.dao;

import com.gomeplus.oversea.bs.service.user.entity.SmsRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * Created by liuliyun-ds on 2017/2/14.
 */
@Repository
public interface SmsRecordDao extends JpaRepository<SmsRecord,Long> {

    /**
     * 查询同一短信业务自然日发送次数
     * @param countryCode
     * @param mobile
     * @param serviceType
     * @param businessType
     * @param beginDay
     * @param endDay
     * @return
     */
    @Query("select count(*) from SmsRecord record where record.countryCode = :countryCode and record.mobile = :mobile " +
            "and record.serviceType = :serviceType and record.businessType = :businessType and record.createTime >= :beginDay and record.createTime < :endDay ")
    Long count(@Param("countryCode") String countryCode, @Param("mobile") String mobile, @Param("serviceType") String serviceType,
               @Param("businessType") String businessType, @Param("beginDay") Date beginDay, @Param("endDay") Date endDay);

    /**
     * 根据发送token查询
     * @param smsToken
     * @return
     */
    SmsRecord findByToken(String smsToken);

    /**
     * 查询大于某一发送时间的同一短信业务的次数
     * @param countryCode
     * @param mobile
     * @param serviceType
     * @param businessType
     * @param sendTime
     * @return
     */
    @Query("select count(*) from SmsRecord record where record.countryCode = :countryCode and record.mobile = :mobile " +
            "and record.serviceType = :serviceType and record.businessType = :businessType and record.createTime > :sendTime ")
    Long countByGtSendTime(@Param("countryCode") String countryCode,@Param("mobile") String mobile,
                           @Param("serviceType") String serviceType,@Param("businessType") String businessType,@Param("sendTime") Date sendTime);

}
