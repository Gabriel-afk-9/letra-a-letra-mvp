package com.letraaletra.api.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
public class ExecutorConfig {

    @Bean
    public ExecutorService gameActorExecutor() {
        return new ThreadPoolExecutor(
                Runtime.getRuntime().availableProcessors(),
                Runtime.getRuntime().availableProcessors() * 2,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(10_000),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }
}
