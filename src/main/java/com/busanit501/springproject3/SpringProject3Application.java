package com.busanit501.springproject3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SpringProject3Application {

    public static void main(String[] args) {
        SpringApplication.run(SpringProject3Application.class, args);
    }
}
