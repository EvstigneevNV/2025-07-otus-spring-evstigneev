package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.CsvQuestionDao;

import java.util.Scanner;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final CsvQuestionDao csvQuestionDao;

    private final Scanner scanner;


    @Override
    public void executeTest() {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        ioService.printLine("Your name: ");
        scanner.nextLine();
        ioService.printLine("Your last name: ");
        scanner.nextLine();
        csvQuestionDao.findAll().forEach(question -> {
                ioService.printLine(question.text());
                question.answers().forEach(answer ->
                        ioService.printFormattedLine("\t-%s (%s)", answer.text(), answer.isCorrect() ? "Yes" : "No"));
        });

    }
}
