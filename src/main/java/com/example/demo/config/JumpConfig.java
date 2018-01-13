package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class JumpConfig extends WebMvcConfigurerAdapter {
    static {
        System.out.println("JumpConfigAdapter");
    }
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/front/*").setViewName("index");
        registry.addViewController("/login").setViewName("index");
        registry.addViewController("/error/*").setViewName("index");
        registry.addViewController("/").setViewName("index");
    }
}