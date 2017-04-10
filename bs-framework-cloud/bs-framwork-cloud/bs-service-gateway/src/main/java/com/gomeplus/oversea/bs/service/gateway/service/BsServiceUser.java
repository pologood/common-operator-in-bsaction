package com.gomeplus.oversea.bs.service.gateway.service;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.gomeplus.oversea.bs.service.gateway.model.UserTokenModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * Created by shangshengfang on 2017/2/23.
 */
@Slf4j
public class BsServiceUser {
    @Autowired
    RestTemplate restTemplate;
    public UserTokenModel tokenValidate(String userId,String token,String device) {
        HttpHeaders header = new HttpHeaders();
        header.add("X-Gomeplus-User-Id", userId);
        header.add("X-Gomeplus-Login-Token", token);
        header.add("X-Gomeplus-Device", device);
        HttpEntity http = new HttpEntity(null, header);
        String url = "http://bs-service-user/user/token";
        try{
            ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.GET, http, String.class);
            int status=exchange.getStatusCodeValue();
            UserTokenModel result=new UserTokenModel();
            result.setStatusCode(status);
            return result;
        }catch (HttpClientErrorException e){
            log.error("tokenValidate error:{}",e.getResponseBodyAsString(),e);

            UserTokenModel result=new UserTokenModel();
            result.setStatusCode(e.getRawStatusCode());
            String body = e.getResponseBodyAsString();
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            try {
                JsonNode jsonNode = mapper.readTree(body);
                JsonNode message = jsonNode.get("message");
                JsonNode messageJson = mapper.readTree(message.asText());
                JsonNode error =messageJson.get("error");
                if (error != null) {
                    result.setErrorMessage(error.get("message").asText());
                    result.setErrorCode(error.get("code").asText());
                }
            }catch(Exception ex){
                log.info("tokenValidate failed return message error format",ex);
            }
            return result;
        }catch(Exception e){
            log.info("tokenValidate error,userId:{},token:{},device:{},e",userId,token,device,e);
            UserTokenModel result=new UserTokenModel();
            result.setStatusCode(401);
            return result;
        }
    }

}
