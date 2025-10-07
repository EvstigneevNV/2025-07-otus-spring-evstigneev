package ru.otus.hw;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import ru.otus.hw.service.TestRunnerService;


@ComponentScan
@ComponentScan
@PropertySource("classpath:application.properties")
public class HomeWork02Application {
    public static void main(String[] args) {

        ApplicationContext context = new AnnotationConfigApplicationContext(HomeWork02Application.class);
        var testRunnerService = context.getBean(TestRunnerService.class);
        testRunnerService.run();

    }
}