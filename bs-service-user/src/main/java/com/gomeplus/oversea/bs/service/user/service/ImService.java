package com.gomeplus.oversea.bs.service.user.service;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.gomeplus.oversea.bs.common.exception.code4xx.C404Exception;
import com.gomeplus.oversea.bs.common.exception.code4xx.C422Exception;
import com.gomeplus.oversea.bs.common.exception.code5xx.C500Exception;
import com.gomeplus.oversea.bs.service.user.dao.UserDao;
import com.gomeplus.oversea.bs.service.user.dto.ContactsDTO;
import com.gomeplus.oversea.bs.service.user.dto.ContactsRequestDTO;
import com.gomeplus.oversea.bs.service.user.dto.ContactsResponseDTO;
import com.gomeplus.oversea.bs.service.user.dto.ImDTO;
import com.gomeplus.oversea.bs.service.user.entity.User;
import com.gomeplus.oversea.bs.service.user.util.HttpUtil;
import com.gomeplus.oversea.bs.service.user.util.Md5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by liuliyun-ds on 2017/2/15.
 * im接口Service类
 */
@Service("imService")
public class ImService {

    @Value("${user.im.centerImApi}")
    private String centerImApi;
//    private static final String centerImApi = "http://10.125.3.61:8080/center-im-api/";
    @Value("${user.im.appId}")
    private String appId;
//    private static final String appId = "gomeplus_dev";
    @Value("${user.im.appKey}")
    private String appKey;
//    private static final String appKey = "2ffa9c361c4447e8b1fe81dbb07d0db5";

    @Autowired
    private UserDao userDao;

    /**
     * 注册IM用户
     * @param userId
     * @return imUserId
     */
    public String imUserRegister(Long userId){
        if(userId == null || userId <= 0){
            throw new C422Exception("参数校验失败");
        }
        String content = null;
        try{
            String url = getUrl(1);
            String body = "{\"reqUser\": {\"uid\":\""+ userId +"\"}}";
            Map<String,String> headers = new HashMap<>();
            headers.put("Content-Type","application/json");
            content = HttpUtil.doPost(url,body,headers);
        }catch (Exception e){
            throw new C500Exception("注册IM用户失败");
        }
        JSONObject object = JSONObject.parseObject(content);
        return object.getJSONObject("data").getString("imUserId");
    }

    /**
     * 获取im用户token,新用户会直接注册
     * @param userId
     * @return
     */
    public ImDTO.Data getToken(String userId){
        User user = userDao.findOne(Long.parseLong(userId));
        if(user == null){
            throw new C404Exception("用户不存在");
        }
        ImDTO imDto = null;
        try{
            String url = getUrl(2);
            String imUserId = StringUtils.isEmpty(user.getImUserId())?userId:user.getImUserId();
            url = url + "&imUserId=" + imUserId;
            String content = HttpUtil.doGet(url,null);
            imDto = JSONObject.parseObject(content,ImDTO.class);
            imDto.getData().setUid(userId);
        }catch (Exception e){
            throw new C500Exception("获取IM用户token失败");
        }
        return imDto.getData();
    }

    /**
     * 根据通讯录匹配注册用户信息
     * @param dto
     * @return
     */
    public ContactsResponseDTO getContacts(ContactsRequestDTO dto){
        ContactsResponseDTO responseDto = new ContactsResponseDTO();
        responseDto.setContacts(new ArrayList<>());
        if(dto.getContacts().size() == 0){
            return responseDto;
        }
        ContactsResponseDTO.Contacts response = null;
        User user = null;
        for(ContactsDTO contacts : dto.getContacts()){
            user = userDao.findByCountryCodeAndMobile(contacts.getCountryCode(),contacts.getMobile());
            response = responseDto.new Contacts();
            response.setCountryCode(contacts.getCountryCode());
            response.setMobile(contacts.getMobile());
            response.setName(contacts.getName());
            if(user == null){
                response.setBound(false);
            }else {
                response.setBound(true);
                response.setUser(response.new User());
                response.getUser().setId(user.getId().toString());
                response.getUser().setImUserId(user.getImUserId());
                response.getUser().setUserName(user.getUserName());
                response.getUser().setNickname(user.getNickname());
                response.getUser().setFacePicUrl(user.getHeadImageUrl());
                response.getUser().setRegisterTime(user.getCreateTime().getTime());
            }
            responseDto.getContacts().add(response);
        }
        return responseDto;
    }

    /**
     * 组装im请求url
     * @return
     * @throws Exception
     */
    private String getUrl(int type) throws Exception{
        Long timestamp = System.currentTimeMillis();
        String signature = Md5Util.md5Encode(appId + "|" +timestamp+ "|" + appKey);
        StringBuilder url = new StringBuilder();
        if(type == 1){
            url.append(centerImApi).append("user/register.json?appId=");
        }else {
            url.append(centerImApi).append("user/updateUserToken.json?appId=");
        }
        url.append(appId).append("&timestamp=");
        url.append(timestamp).append("&signature=").append(signature).append("&traceId=").append(UUID.randomUUID().toString().replace("-",""));
        return url.toString();
    }

}
