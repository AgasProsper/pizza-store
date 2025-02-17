package com.pizza.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(
                        "kitchen-service",
                        r -> r.path("/kitchen/**")
                                .filters(f -> f.rewritePath("/kitchen/(?<segment>.*)",
                                        "/${segment}"))
                                .uri("lb://kitchen-service"))
                .route(
                        "tracker-service",
                        r -> r.path("/tracker/**")
                                .filters(f -> f.rewritePath("/tracker/(?<segment>.*)",
                                        "/${segment}"))
                                .uri("lb://tracker-service"))
                .build();
    }
}
