package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

@RequiredArgsConstructor
@Service
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        var questions = questionDao.findAll();
        var testResult = new TestResult(student);

        for (var question: questions) {
            ioService.printLine("Enter the correct answer number");
            ioService.printLine(question.text());
            for (int i = 0; i < question.answers().size(); i++) {
                ioService.printFormattedLine("\t%s. %s", i + 1, question.answers().get(i).text());
            }
            var chooseAnswer = ioService.readIntForRange(1, question.answers().size(),
                    "An invalid response option. Please try again.");
            var isAnswerValid = question.answers().get(chooseAnswer - 1).isCorrect();
            testResult.applyAnswer(question, isAnswerValid);
        }
        return testResult;
    }
}
