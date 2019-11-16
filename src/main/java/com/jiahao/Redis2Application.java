package com.jiahao;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.jiahao.dao")
public class Redis2Application {

    public static void main(String[] args) {
        SpringApplication.run(Redis2Application.class, args);
    }

}
