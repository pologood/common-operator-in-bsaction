package com.gomeplus.bs.common.dubbor.rabbitmq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author zhaozhou
 * @Description
 * @date 2016/12/13
 */
@SpringBootApplication
@ComponentScan("com.gomeplus.bs.common.dubbor")
public class Application {
    public static void main(String[] args) {

        long starTime = System.currentTimeMillis();
        SpringApplication.run(Application.class,args);
        long endTime=System.currentTimeMillis();
        long Time=endTime-starTime;
        System.out.println("\nStart Time: "+ Time/1000 +" s");
        System.out.println("...............................................................");
        System.out.println("..................Service starts successfully..................");
        System.out.println("...............................................................");
    }
}