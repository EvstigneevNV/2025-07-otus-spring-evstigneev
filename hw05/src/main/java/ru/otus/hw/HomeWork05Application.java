package ru.otus.hw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.shell.command.annotation.CommandScan;


@SpringBootApplication
@CommandScan
public class HomeWork05Application {

	public static void main(String[] args) {
		SpringApplication.run(HomeWork05Application.class, args);
	}

}
