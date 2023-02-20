package com.happy.otc.config;

import com.happy.otc.interceptor.CapitalCipherInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class CapitalCipherInterceptorConfig extends WebMvcConfigurerAdapter {

    @Bean
    public CapitalCipherInterceptor capitalCipherInterceptor() {
        return new CapitalCipherInterceptor();
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(capitalCipherInterceptor()).addPathPatterns("/**");
        super.addInterceptors(registry);
    }
}
