package com.sani.firstllm;

import com.sani.firstllm.config.GroqProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(GroqProperties.class)
public class FirstLlmCallApplication {

    public static void main(String[] args) {
        SpringApplication.run(FirstLlmCallApplication.class, args);
    }
}

