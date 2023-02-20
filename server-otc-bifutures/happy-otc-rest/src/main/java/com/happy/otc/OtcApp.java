package com.happy.otc;


import com.bitan.common.config.MybatisDataSource;
import com.bitan.common.config.RedisPoolConfig;
import com.bitan.common.interceptor.BitanExceptionHandler;
import com.bitan.common.interceptor.RequestInterceptor;
import com.bitan.common.swagger.SwaggerConfig;
import com.happy.otc.util.AddressUtils;
import com.happy.otc.util.BTCCoinUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.TimeZone;


@EnableTransactionManagement
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@EnableDiscoveryClient
@EnableFeignClients
@ComponentScan("com.happy")
@ComponentScan("com.bitan")
@MapperScan("com.happy.otc.dao")
@Import({BitanExceptionHandler.class, RedisPoolConfig.class, RequestInterceptor.class, SwaggerConfig.class,MybatisDataSource.class})
public class OtcApp {

    private static final Logger logger = LogManager.getLogger(OtcApp.class);

    public static void main(String[] args) {
        SpringApplication.run(OtcApp.class, args);
    }

    @Bean(name = "executor")
    public ThreadPoolTaskExecutor executor() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
//        executor.setWaitForTasksToCompleteOnShutdown(true);
        return executor;
    }

    @Bean
    ProtobufHttpMessageConverter protobufHttpMessageConverter() {
        ProtobufHttpMessageConverter converter = new ProtobufHttpMessageConverter();
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.APPLICATION_JSON));
        return converter;
    }


    @PostConstruct
    void started() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }
}
