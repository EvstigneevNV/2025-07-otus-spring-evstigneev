package ru.otus.hw;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;
import ru.otus.hw.exceptions.QuestionReadException;
import ru.otus.hw.service.LocalizedIOService;
import ru.otus.hw.service.TestService;
import ru.otus.hw.service.TestServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


@SpringBootTest(classes = {TestServiceImpl.class})
@Import(TestServiceImpl.class)
@TestPropertySource(properties = {
        "spring.main.banner-mode=off",
        "spring.main.web-application-type=none"
})
class TestServiceImplTest {

    @MockitoBean
    private LocalizedIOService ioService;

    @MockitoBean
    private QuestionDao questionDao;

    @Autowired
    private TestServiceImpl testService;

    private List<Question> questions;
    private Student student;

    @BeforeEach
    void setUp() {
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

    @DisplayName("Все ответы верны")
    @Test
    void allAnswersCorrect() {
        when(questionDao.findAll()).thenReturn(questions);
        when(ioService.readIntForRangeLocalized(anyInt(), anyInt(), anyString()))
                .thenReturn(1, 1, 3);

        TestResult result = testService.executeTestFor(student);

        assertNotNull(result, "Результат не должен быть null");
        assertEquals(student, result.getStudent(), "Студент не совпал");
        assertEquals(questions, result.getAnsweredQuestions(), "Список отвеченных вопросов не совпал");
        assertEquals(3, result.getRightAnswersCount(), "Количество правильных ответов должно быть 3");
    }

    @DisplayName("Один ответ верный")
    @Test
    void singleQuestionCorrect() {
        when(questionDao.findAll()).thenReturn(questions);
        when(ioService.readIntForRangeLocalized(anyInt(), anyInt(), anyString()))
                .thenReturn(1, 2, 2);

        TestResult result = testService.executeTestFor(student);

        assertEquals(student, result.getStudent(), "Студент не совпал");
        assertEquals(questions, result.getAnsweredQuestions(), "Список отвеченных вопросов не совпал");
        assertEquals(1, result.getRightAnswersCount(), "Должен быть 1 правильный ответ");
    }

    @DisplayName("Все ответы не правильные")
    @Test
    void allAnswersWrong() {
        when(questionDao.findAll()).thenReturn(questions);
        when(ioService.readIntForRangeLocalized(anyInt(), anyInt(), anyString()))
                .thenReturn(2, 2, 2);

        TestResult result = testService.executeTestFor(student);

        assertEquals(student, result.getStudent(), "Студент не совпал");
        assertEquals(questions, result.getAnsweredQuestions(), "Список отвеченных вопросов не совпал");
        assertEquals(0, result.getRightAnswersCount(), "Должно быть 0 правильных ответов");
    }

    @DisplayName("Разные ответы")
    @Test
    void mixedAnswers() {
        when(questionDao.findAll()).thenReturn(questions);
        when(ioService.readIntForRangeLocalized(anyInt(), anyInt(), anyString()))
                .thenReturn(2, 1, 2);

        TestResult result = testService.executeTestFor(student);

        assertEquals(student, result.getStudent(), "Студент не совпал");
        assertEquals(questions, result.getAnsweredQuestions(), "Список отвеченных вопросов не совпал");
        assertEquals(1, result.getRightAnswersCount(), "Должен быть 1 правильный ответ");
    }

    @DisplayName("Не найдены вопросы")
    @Test
    void noQuestions() {
        when(questionDao.findAll()).thenReturn(List.of());

        TestResult result = testService.executeTestFor(student);

        assertEquals(student, result.getStudent(), "Студент не совпал");
        assertTrue(result.getAnsweredQuestions().isEmpty(), "Список отвеченных вопросов должен быть пуст");
        assertEquals(0, result.getRightAnswersCount(), "Должно быть 0 правильных ответов");
    }

    @DisplayName("Ошибка при получении списка вопросов")
    @Test
    void errorExecutionTest() {
        when(questionDao.findAll()).thenThrow(new QuestionReadException("Error reading the file"));

        assertThrows(RuntimeException.class, () -> testService.executeTestFor(student));
    }
}


