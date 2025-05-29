package com.green.yp.config;

import java.util.concurrent.Executor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

  @Value("${green.yp.audit.coreThreads:2}")
  private Integer auditCoreThreads;

  @Value("${green.yp.audit.maxThreads:20}")
  private Integer auditMaxThreads;

  @Value("${green.yp.audit.maxQueueSize:150}")
  private Integer maxAuditQueueSize;

  @Value("${green.yp.email.coreThreads:2}")
  private Integer emailCoreThreads;

  @Value("${green.yp.email.maxThreads:20}")
  private Integer emailMaxThreads;

  @Value("${green.yp.email.maxQueueSize:150}")
  private Integer maxQueueSize;

  @Override
  public Executor getAsyncExecutor() {
    ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
    threadPoolTaskExecutor.initialize();
    return threadPoolTaskExecutor;
  }

  @Bean(name = "threadPoolAuditTaskExecutor")
  public Executor threadPoolTaskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(auditCoreThreads);
    executor.setMaxPoolSize(auditMaxThreads);
    executor.setQueueCapacity(maxAuditQueueSize);
    return executor;
  }

  @Bean(name = "sendEmailThreadPool")
  public Executor sendEmailThreadPool() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(emailCoreThreads);
    executor.setMaxPoolSize(emailMaxThreads);
    executor.setQueueCapacity(maxQueueSize);
    return executor;
  }

//  @Bean
//  public FlywayMigrationStrategy cleanMigrateStrategy() {
//    return flyway -> {
//      flyway.repair();
//      flyway.migrate();
//    };
//  }
}
