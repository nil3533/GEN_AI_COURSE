package com.sani.multichat;

import com.sani.multichat.config.AiProviderProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AiProviderProperties.class)
public class MultiProviderChatApplication {

    public static void main(String[] args) {
        SpringApplication.run(MultiProviderChatApplication.class, args);
    }
}
