package com.tubz.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

@Configuration
public class BeanConfig {

    @Bean
    public RouteLocator myRouteLocator(RouteLocatorBuilder routeLocatorBuilder) {
        return routeLocatorBuilder
                .routes()
                .route(p -> p.path("/tbank/accounts/**")
                        .filters(f -> f.rewritePath("/tbank/accounts/(?<segment>.*)", "/${segment}")
                                .addRequestHeader("X-Response-Time", new Date().toString())).uri("lb://ACCOUNTS")).build();
    }

}
