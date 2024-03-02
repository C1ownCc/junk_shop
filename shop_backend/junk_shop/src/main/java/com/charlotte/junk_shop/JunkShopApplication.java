package com.charlotte.junk_shop;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@MapperScan("com.charlotte.junk_shop.Dao")
public class JunkShopApplication {

    public static void main(String[] args) {
        SpringApplication.run(JunkShopApplication.class, args);
    }

}
