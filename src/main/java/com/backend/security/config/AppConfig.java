package com.backend.security.config;
import java.time.Instant;
import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
;

@Configuration
@EnableAsync
public class AppConfig {
    

    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(100);
        executor.setMaxPoolSize(1000);
        executor.setQueueCapacity(2500);
        executor.setThreadNamePrefix("CustomThread-");
        executor.initialize();
        return executor;
    }

    @Bean
    public Gson gson() {
        return new GsonBuilder()
                .registerTypeAdapter(Instant.class, (com.google.gson.JsonSerializer<Instant>)
                        (src, typeOfSrc, context) -> new com.google.gson.JsonPrimitive(src.toString()))
                .registerTypeAdapter(Instant.class, (com.google.gson.JsonDeserializer<Instant>)
                        (json, typeOfSrc, context) -> Instant.parse(json.getAsString()))
                .setPrettyPrinting()
                .create();
    }


}
