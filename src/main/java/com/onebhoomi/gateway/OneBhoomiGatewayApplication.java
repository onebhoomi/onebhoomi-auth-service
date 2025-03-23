// src/main/java/com/onebhoomi/gateway/OneBhoomiGatewayApplication.java
package com.onebhoomi.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class OneBhoomiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(OneBhoomiGatewayApplication.class, args);
    }
}
