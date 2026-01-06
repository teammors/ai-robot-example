package com.teammors.robot.example.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "mysql")
@Data
public class MySQLProperty {
    String jdbcUrl;
    String username;
    String password;
}
