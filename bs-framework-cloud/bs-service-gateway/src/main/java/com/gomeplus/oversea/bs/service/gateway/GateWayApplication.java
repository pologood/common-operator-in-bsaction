package com.gomeplus.oversea.bs.service.gateway;

import com.gomeplus.oversea.bs.service.gateway.filter.AccessFilter;
import com.gomeplus.oversea.bs.service.gateway.filter.ResponseFilter;
import com.gomeplus.oversea.bs.service.gateway.service.BsServiceUser;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
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
