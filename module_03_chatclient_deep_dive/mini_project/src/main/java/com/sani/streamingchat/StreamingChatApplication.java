package com.sani.streamingchat;

import com.sani.streamingchat.config.AiChatProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AiChatProperties.class)
public class StreamingChatApplication {

    public static void main(String[] args) {
        SpringApplication.run(StreamingChatApplication.class, args);
    }
}
