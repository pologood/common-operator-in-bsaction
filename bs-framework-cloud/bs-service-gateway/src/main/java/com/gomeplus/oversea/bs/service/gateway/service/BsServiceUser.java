package com.gomeplus.oversea.bs.service.gateway.service;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shangshengfang on 2017/2/23.
 */
@Slf4j
public class BsServiceUser {
    @Autowired
    RestTemplate restTemplate;


    public boolean tokenValidate(String userId,String token,String device) {
        HttpHeaders header = new HttpHeaders();
        header.add("X-Gomeplus-User-Id", userId);
        header.add("X-Gomeplus-Login-Token", token);
        header.add("X-Gomeplus-Device", device);
        HttpEntity http = new HttpEntity(null, header);
        String url = "http://bs-service-user/user/token";
        try{
            ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.GET, http, String.class);
            int status=exchange.getStatusCodeValue();
            if(status==200){
                return true;
            }else{
                return false;
            }
        }catch(Exception e){
            log.info("tokenValidate error,userId:{},token:{},device:{},e",userId,token,device,e);
            return false;
        }
    }
     public String startPicture() {
        ResponseEntity<String> forEntity = restTemplate.getForEntity("http://bs-service-system/system/startPicture", String.class);
        String result=forEntity.getBody();
        return result;
    }

}
