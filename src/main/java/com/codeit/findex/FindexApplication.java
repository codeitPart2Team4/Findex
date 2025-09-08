package com.codeit.findex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FindexApplication {

    public static void main(String[] args) {
        // API-KEY 출력 테스트
        System.out.println("API KEY = " + System.getenv("FINDEX_API_KEY"));
        SpringApplication.run(FindexApplication.class, args);
    }

}
