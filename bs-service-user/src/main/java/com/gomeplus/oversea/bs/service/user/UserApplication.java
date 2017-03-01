package com.gomeplus.oversea.bs.service.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * 2017/1/13
 *
 * @author erdaoya
 * @since 1.0
 */
@SpringBootApplication
@ComponentScan("com.gomeplus.oversea.bs.service.user")
@EnableDiscoveryClient
@EnableJpaRepositories("com.gomeplus.oversea.bs.service.user")
@EnableFeignClients
public class UserApplication {
    public static void main(String[] args) {
        long starTime = System.currentTimeMillis();
        SpringApplication.run(UserApplication.class, args);
        long endTime = System.currentTimeMillis();
        long time = endTime - starTime;
        System.out.println("\nStart Time: " + time / 1000 + " s");
        System.out.println("...............................................................");
        System.out.println("..................Service starts successfully..................");
        System.out.println("...............................................................");
    }
}
