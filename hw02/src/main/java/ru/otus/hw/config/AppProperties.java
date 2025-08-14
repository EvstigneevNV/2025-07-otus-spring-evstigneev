package ru.otus.hw.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class AppProperties implements TestConfig, TestFileNameProvider {

    private int rightAnswersCountToPass;

    private String testFileName;

    public AppProperties(@Value("${test.rightAnswersCountToPass}") String rightAnswersCountToPass,
                         @Value("${test.fileName}") String testFileName) {
        System.out.println(rightAnswersCountToPass + testFileName);
        this.rightAnswersCountToPass = Integer.parseInt(rightAnswersCountToPass);
        this.testFileName = testFileName;
    }
}
