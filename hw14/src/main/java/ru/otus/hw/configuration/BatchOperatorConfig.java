package ru.otus.hw.configuration;

import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.configuration.support.MapJobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.support.JobOperatorFactoryBean;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BatchOperatorConfig {

    @Bean
    public JobRegistry jobRegistry() {
        return new MapJobRegistry();
    }

    @Bean
    public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry) {
        var processor = new JobRegistryBeanPostProcessor();
        processor.setJobRegistry(jobRegistry);
        return processor;
    }

    @Bean
    public JobOperator jobOperator(
            JobExplorer jobExplorer,
            JobRepository jobRepository,
            JobLauncher jobLauncher,
            JobRegistry jobRegistry
    ) throws Exception {
        var factoryBean = new JobOperatorFactoryBean();
        factoryBean.setJobExplorer(jobExplorer);
        factoryBean.setJobRepository(jobRepository);
        factoryBean.setJobLauncher(jobLauncher);
        factoryBean.setJobRegistry(jobRegistry);
        factoryBean.afterPropertiesSet();
        return factoryBean.getObject();
    }
}
