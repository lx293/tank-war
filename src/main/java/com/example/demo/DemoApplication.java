package com.example.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@MapperScan("com.example.demo.mapper")
@EnableAsync
public class DemoApplication {

    public static void main(String[] args) {
        System.setProperty("jdk.reflect.allowNativeAccess", "ALL-UNNAMED");
        SpringApplication.run(DemoApplication.class, args);
        System.out.println("=================================");
        System.out.println("  小说网站启动成功！");
        System.out.println("  访问地址: http://localhost:8080");
        System.out.println("=================================");
    }
}