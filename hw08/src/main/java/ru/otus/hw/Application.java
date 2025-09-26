package ru.otus.hw;

import io.mongock.driver.mongodb.springdata.v4.SpringDataMongoV4Driver;
import io.mongock.runner.springboot.MongockSpringboot;
import io.mongock.runner.springboot.base.MongockInitializingBeanRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.shell.command.annotation.CommandScan;


@SpringBootApplication
@CommandScan
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

    @Profile("!test")
    @Bean
    public MongockInitializingBeanRunner mongock(ApplicationContext context,
                                                 MongoTemplate mongoTemplate) {
        return MongockSpringboot.builder()
                .setDriver(SpringDataMongoV4Driver.withDefaultLock(mongoTemplate))
                .setSpringContext(context)
                .addMigrationScanPackage("ru.otus.hw.migrations")
                .buildInitializingBeanRunner();
    }
}
