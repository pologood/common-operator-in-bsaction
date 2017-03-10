package com.gomeplus.oversea.bi.service.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

/**
 * 2017/1/13
 *
 * @author fanhailong
 * @since 1.0
 */
@SpringBootApplication
@ImportResource("spring/spring-boot.xml")
@ComponentScan("com.gomeplus.oversea.bi.service.search")
@EnableDiscoveryClient
@EnableFeignClients
@EnableHystrix
public class SearchServiceApplication {
    public static void main(String[] args) {
        long starTime = System.currentTimeMillis();
        SpringApplication.run(SearchServiceApplication.class, args);
        long endTime = System.currentTimeMillis();
        long time = endTime - starTime;
        System.out.println("\nStart Time: " + time / 1000 + " s");
        System.out.println("...............................................................");
        System.out.println("..................Service starts successfully..................");
        System.out.println("...............................................................");
    }
}
