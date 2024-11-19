package com.escass.meltube;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class) // security 쓰면 강제로 /login 으로 맵핑 시켜버리기 때문에 막기 위한 용도
public class MeltubeApplication {
    public static void main(String[] args) {
        SpringApplication.run(MeltubeApplication.class, args);
    }

}
