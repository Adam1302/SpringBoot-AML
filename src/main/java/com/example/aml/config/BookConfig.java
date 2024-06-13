package com.example.aml.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties
@Component
@Getter
public class BookConfig {
    @Value("${spring.profiles.active}")
    private String activeProfile;
}
