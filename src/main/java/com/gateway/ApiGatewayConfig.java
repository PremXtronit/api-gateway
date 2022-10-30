package com.gateway;

import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
public class ApiGatewayConfig {

    @Bean
    public RouteLocator getWayRouteLocator(RouteLocatorBuilder builder) {

        Function<GatewayFilterSpec, UriSpec> filter = f -> f
                .addRequestHeader("MyHeader", "MyURL")
                .addRequestHeader("MyParam", "MyParamValue");

        Function<PredicateSpec, Buildable<Route>> fn = (p) -> p
                .path("/get")
                .filters(filter)
                .uri("http://httpbin.org:80");

        Function<PredicateSpec, Buildable<Route>> exchange = (p) -> p
                .path("/currency-exchange/**")
                .uri("lb://currency-exchange");

        Function<PredicateSpec, Buildable<Route>> conversion = (p) -> p
                .path("/currency-conversion/**")
                .uri("lb://currency-conversion");

        Function<PredicateSpec, Buildable<Route>> newUrl = (p) -> p
                .path("/currency-conversion-new/**")
                .filters(f -> f.rewritePath("/currency-conversion-new", "/currency-conversion"))
                .uri("lb://currency-conversion");

        return builder.routes()
                .route(fn)
                .route(exchange)
                .route(conversion)
                .route(newUrl)
                .build();
    }
}
