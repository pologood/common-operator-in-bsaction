package com.gomeplus.bs.service.lolengine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

/**
 * 直接运行这个类 可以启动工程
 * @author wangchangye
 *
 */
@SpringBootApplication
@ImportResource("config/spring-boot.xml")
@ComponentScan("com.gomeplus.bs")
public class LOLEngineApplication {
    public static void main(String[] args) {

        long starTime = System.currentTimeMillis();
        SpringApplication.run(LOLEngineApplication.class,args);
        long endTime=System.currentTimeMillis();
        long Time=endTime-starTime;
        System.out.println("\n启动时间:"+ Time/1000 +"秒");
        System.out.println("...............................................................");
        System.out.println("..................Service starts successfully..................");
        System.out.println("...............................................................");

    }
}