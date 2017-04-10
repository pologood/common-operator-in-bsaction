package com.gomeplus.oversea.bs.service.gateway;

import com.gomeplus.oversea.bs.service.gateway.event.TokenSuccessMQEvent;
import com.gomeplus.oversea.bs.service.gateway.filter.*;
import com.gomeplus.oversea.bs.service.gateway.service.BsServiceUser;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

/**
 * 2017/1/13
 *
 * @author erdaoya
 * @since 1.0
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableZuulProxy
@ComponentScan("com.gomeplus.oversea")
public class GateWayApplication {

    public static void main(String[] args) {
        long starTime = System.currentTimeMillis();
        SpringApplication.run(GateWayApplication.class, args);
        long endTime = System.currentTimeMillis();
        long time = endTime - starTime;
        System.out.println("\nStart Time: " + (time / 1000) + " s");
        System.out.println("...............................................................");
        System.out.println("..................Service starts successfully..................");
        System.out.println("...............................................................");
    }
    @Bean
    @LoadBalanced
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    BsServiceUser bsServiceUser(){
        return new BsServiceUser();
    }
    /**
     * accessFilter:config security and so on
     *
     * @return filter
     */
    @Bean
    public AccessFilter accessFilter() {
        return new AccessFilter();
    }
    @Bean
    public ApiScopeFilter apiScopeFilter() {
        return new ApiScopeFilter();
    }

    @RefreshScope
    @Bean
    public ServiceDegradationFilter ServiceDegradationFilter() {
        return new ServiceDegradationFilter();
    }

    @Bean
    public ParamsValidateFilter ParamsValidateFilter() {
        return new ParamsValidateFilter();
    }


    @Bean
    public TokenValidateFilter TokenValidateFilter() {
        return new TokenValidateFilter();
    }

    @RefreshScope
    @Bean
    public ItemLogMQFilter ItemLogMQFilter() {
        return new ItemLogMQFilter();
    }

    @RefreshScope
    @Bean
    public TokenSuccessMQFilter TokenSuccessMQFilter() {
        return new TokenSuccessMQFilter();
    }
    /**
     * responseFilter: change api
     *
     * @return zuul filter
     */
    @Bean
    public ResponseFilter responseFilter() {
        return new ResponseFilter();
    }


}
