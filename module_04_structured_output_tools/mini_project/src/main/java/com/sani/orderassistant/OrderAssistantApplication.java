package com.sani.orderassistant;

import com.sani.orderassistant.config.AiAssistantProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AiAssistantProperties.class)
public class OrderAssistantApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderAssistantApplication.class, args);
    }
}
