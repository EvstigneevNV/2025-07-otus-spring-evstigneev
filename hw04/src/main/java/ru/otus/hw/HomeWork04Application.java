package ru.otus.hw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.shell.command.annotation.CommandScan;

@CommandScan
@SpringBootApplication
public class HomeWork04Application {

    public static void main(String[] args) {
        SpringApplication.run(HomeWork04Application.class, args);
    }

}