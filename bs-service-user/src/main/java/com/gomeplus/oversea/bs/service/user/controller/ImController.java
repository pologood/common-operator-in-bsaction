package com.gomeplus.oversea.bs.service.user.controller;

import com.gomeplus.oversea.bs.common.exception.code4xx.C422Exception;
import com.gomeplus.oversea.bs.service.user.dto.ContactsRequestDTO;
import com.gomeplus.oversea.bs.service.user.dto.ContactsResponseDTO;
import com.gomeplus.oversea.bs.service.user.dto.ImDTO;
import com.gomeplus.oversea.bs.service.user.service.ImService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by liuliyun-ds on 2017/2/14.
 * im接口类
 */
@RestController
@Slf4j
@RequestMapping("/user")
public class ImController {

    @Autowired
    private ImService imService;

    /**
     * 获取imToken
     * @param userId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/im",method = RequestMethod.GET)
    public ImDTO.Data doGet(String userId) throws Exception{
        //登录校验
        if(StringUtils.isEmpty(userId)){
            throw new C422Exception("参数校验失败");
        }
        return imService.getToken(userId);
    }

    /**
     * 通讯录接口
     * @param userId
     * @param dto
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/contacts",method = RequestMethod.POST)
    public ContactsResponseDTO doPost(String userId, @RequestBody ContactsRequestDTO dto) throws Exception{
        log.info("contacts request body = [{}]" ,dto);
        //登录校验

        //入参校验
        if(dto == null || dto.getContacts() == null){
            throw new C422Exception("参数校验失败");
        }
        ContactsResponseDTO responseDTO = imService.getContacts(dto);
        log.info("contacts response body = [{}]" ,responseDTO);
        return responseDTO;
    }
}
