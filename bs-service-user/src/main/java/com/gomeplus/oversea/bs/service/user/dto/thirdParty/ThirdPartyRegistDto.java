package com.gomeplus.oversea.bs.service.user.dto.thirdParty;

import com.gomeplus.oversea.bs.common.exception.code4xx.C422Exception;
import com.google.common.base.Strings;
import lombok.Data;

/**
 * Created by zhangxinxing on 2017/2/14.
 */
@Data
public class ThirdPartyRegistDto {
    /**
     * 手机区域码。例：“+86”
     */
    private String countryCode;
    /**手机号*/
    private String mobile;
    /**校验码*/
    private String smsCode;
    /**会员推荐人用户Id*/
    private String refereeUserId;
    /**用于安全性校验*/
    private String smsToken;
     /**第三方登录信息*/
    private ThirdPartyDto thirdParty;

    /**
     * 校验必填字段
     * @param thirdPartyRegistDto
     */
    public static  void  checkFieldValueNull(ThirdPartyRegistDto thirdPartyRegistDto){
        if (thirdPartyRegistDto == null){
            throw new C422Exception("数据校验失败");
        }
        if (Strings.isNullOrEmpty(thirdPartyRegistDto.getCountryCode())){
            throw new C422Exception("数据校验失败");
        }
        if (Strings.isNullOrEmpty(thirdPartyRegistDto.getMobile())){
            throw new C422Exception("数据校验失败");
        }
        if (Strings.isNullOrEmpty(thirdPartyRegistDto.getSmsCode())){
            throw new C422Exception("数据校验失败");
        }
        if (Strings.isNullOrEmpty(thirdPartyRegistDto.getSmsToken())){
            throw new C422Exception("数据校验失败");
        }
        if (thirdPartyRegistDto.getThirdParty() == null){
            throw new C422Exception("数据校验失败");
        }
        if (Strings.isNullOrEmpty(thirdPartyRegistDto.getThirdParty().getId())){
            throw new C422Exception("数据校验失败");
        }
        if (Strings.isNullOrEmpty(thirdPartyRegistDto.getThirdParty().getName())){
            throw new C422Exception("数据校验失败");
        }
        if (Strings.isNullOrEmpty(thirdPartyRegistDto.getThirdParty().getType())){
            throw new C422Exception("数据校验失败");
        }
    }
}
