package ru.otus.hw;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.hw.dao.CsvQuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;
import ru.otus.hw.exceptions.QuestionReadException;
import ru.otus.hw.service.IOService;
import ru.otus.hw.service.TestServiceImpl;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestServiceImplTest {

    private IOService ioService;
    private CsvQuestionDao csvQuestionDao;
    private TestServiceImpl testServiceImpl;
    private List<Question> questions;
    private Student student;

    @BeforeEach
    void setUp() {
        ioService = mock(IOService.class);
        csvQuestionDao = mock(CsvQuestionDao.class);
        testServiceImpl = new TestServiceImpl(ioService, csvQuestionDao);
        questions = setQuestions();
        student = new Student("test", "case");
    }

    private List<Question> setQuestions() {
        Question q1 = new Question("Is there life on Mars?", List.of(
                new Answer("Science doesn't know this yet", true),
                new Answer("Certainly. The red UFO is from Mars. And green is from Venus", false),
                new Answer("Absolutely not", false)
        ));
        Question q2 = new Question("How should resources be loaded form jar in Java?", List.of(
                new Answer("ClassLoader#getResourceAsStream or ClassPathResource#getInputStream", true),
                new Answer("ClassLoader#getResource#getFile + FileReader", false),
                new Answer("Wingardium Leviosa", false)
        ));
        Question q3 = new Question("Which option is a good way to handle the exception?", List.of(
                new Answer("@SneakyThrow", false),
                new Answer("e.printStackTrace()", false),
                new Answer("Rethrow with wrapping in business exception (for example, QuestionReadException)", true),
                new Answer("Ignoring exception", false)
        ));
        return List.of(q1, q2, q3);
    }

    @Test
    void allAnswersCorrect() {
        when(csvQuestionDao.findAll()).thenReturn(questions);
        when(ioService.readIntForRange(anyInt(), anyInt(), anyString()))
                .thenReturn(1, 1, 3);

        TestResult result = testServiceImpl.executeTestFor(student);

        assertThat(result).isNotNull();
        assertThat(result.getStudent()).isEqualTo(student);
        assertThat(result.getAnsweredQuestions()).containsExactly(questions.get(0), questions.get(1), questions.get(2));
        assertThat(result.getRightAnswersCount()).isEqualTo(3);
    }

    @Test
    void singleQuestionCorrect() {
        when(csvQuestionDao.findAll()).thenReturn(questions);
        when(ioService.readIntForRange(anyInt(), anyInt(), anyString()))
                .thenReturn(1, 2,2);

        TestResult result = testServiceImpl.executeTestFor(student);

        assertThat(result.getStudent()).isEqualTo(student);
        assertThat(result.getAnsweredQuestions()).containsExactly(questions.get(0), questions.get(1), questions.get(2));
        assertThat(result.getRightAnswersCount()).isEqualTo(1);
    }


    @Test
    void allAnswersWrong() {
        when(csvQuestionDao.findAll()).thenReturn(questions);
        when(ioService.readIntForRange(anyInt(), anyInt(), anyString()))
                .thenReturn(2, 2, 2);

        TestResult result = testServiceImpl.executeTestFor(student);

        assertThat(result.getStudent()).isEqualTo(student);
        assertThat(result.getAnsweredQuestions()).containsExactly(questions.get(0), questions.get(1), questions.get(2));
        assertThat(result.getRightAnswersCount()).isZero();
    }

    @Test
    void mixedAnswers() {

        when(csvQuestionDao.findAll()).thenReturn(questions);
        when(ioService.readIntForRange(anyInt(), anyInt(), anyString()))
                .thenReturn(2, 1, 2);

        TestResult result = testServiceImpl.executeTestFor(student);

        assertThat(result.getStudent()).isEqualTo(student);
        assertThat(result.getAnsweredQuestions()).containsExactly(questions.get(0), questions.get(1), questions.get(2));
        assertThat(result.getRightAnswersCount()).isEqualTo(1);
    }


    @Test
    void noQuestions() {
        when(csvQuestionDao.findAll()).thenReturn(List.of());
        TestResult result = testServiceImpl.executeTestFor(student);

        assertThat(result.getStudent()).isEqualTo(student);
        assertThat(result.getAnsweredQuestions()).isEmpty();
        assertThat(result.getRightAnswersCount()).isZero();
    }

    @Test
    void errorExecutionTest() {
        when(csvQuestionDao.findAll()).thenThrow(new QuestionReadException("Error reading the file"));

        assertThrows(RuntimeException.class, () -> testServiceImpl.executeTestFor(student));
    }
}

