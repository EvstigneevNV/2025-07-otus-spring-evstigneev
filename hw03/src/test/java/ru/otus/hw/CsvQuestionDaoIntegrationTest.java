package ru.otus.hw;

import org.junit.jupiter.api.Test;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.CsvQuestionDao;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.util.List;


import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertTrue;

class CsvQuestionDaoIntegrationTest {

    private CsvQuestionDao dao;

    @Test
    void successfulFileReadingTest() {
        TestFileNameProvider provider = () -> "valid_questions.csv";
        dao = new CsvQuestionDao(provider);

        List<Question> questions = dao.findAll();

        assertTrue("Список вопросов пуст", !questions.isEmpty());
        assertEquals("Неверное число вопросов", 3, questions.size());

        Question first = questions.get(0);
        assertEquals("Неверный текст первого вопроса",
                "Is there life on Mars?", first.text());

        assertEquals("Неверное число ответов у первого вопроса",
                3, first.answers().size());

        assertEquals("Ответ #1: текст не совпал",
                "Science doesn't know this yet", first.answers().get(0).text());
        assertTrue("Ответ #1 должен быть корректным", first.answers().get(0).isCorrect());

        assertEquals("Ответ #2: текст не совпал",
                "Certainly. The red UFO is from Mars. And green is from Venus",
                first.answers().get(1).text());
        assertTrue("Ответ #2 должен быть некорректным",
                !first.answers().get(1).isCorrect());

        assertEquals("Ответ #3: текст не совпал",
                "Absolutely not", first.answers().get(2).text());
        assertTrue("Ответ #3 должен быть некорректным",
                !first.answers().get(2).isCorrect());
    }

    @Test
    void failedFileReadingTest() {
        TestFileNameProvider badProvider = () -> "invalid_questions.csv";
        dao = new CsvQuestionDao(badProvider);

        assertThrows(
                RuntimeException.class,
                () -> dao.findAll(),
                "Ожидалось исключение при попытке прочитать некорректный CSV"
        );
    }

    @Test
    void notFoundFileTest() {
        TestFileNameProvider badProvider = () -> "questions_not_found.csv";
        dao = new CsvQuestionDao(badProvider);

        assertThrows(
                QuestionReadException.class,
                () -> dao.findAll(),
                "Ожидалось исключение при попытке прочитать некорректный CSV"
        );
    }
}
