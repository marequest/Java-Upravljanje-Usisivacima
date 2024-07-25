package rs.raf.demo.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
public class AsyncConfig {

    @Bean(name = "myTaskExecutor")
    public Executor taskExecutor() {
        return Executors.newCachedThreadPool();
    }
}