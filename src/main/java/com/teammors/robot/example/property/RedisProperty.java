package com.teammors.robot.example.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "redis")
@Data
public class RedisProperty {
    private String ip;
    private int port;
    private String user;
    private String password;
    private int db;
    private int iscluster;
}
