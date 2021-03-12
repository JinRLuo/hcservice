package com.hcservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(scanBasePackages = {"com.hcservice"})
@RestController
@MapperScan("com.hcservice.dao")
public class HcserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(HcserviceApplication.class, args);
    }

}
