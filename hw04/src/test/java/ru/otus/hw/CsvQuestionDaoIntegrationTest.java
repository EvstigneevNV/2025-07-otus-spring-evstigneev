package ru.otus.hw;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.CsvQuestionDao;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.util.List;


import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertFalse;
import static org.springframework.test.util.AssertionErrors.assertTrue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.when;

@SpringBootTest(classes = { CsvQuestionDao.class }, webEnvironment = SpringBootTest.WebEnvironment.NONE)
class CsvQuestionDaoIntegrationTest {

    @MockitoBean
    private TestFileNameProvider fileNameProvider;

    @Autowired
    private CsvQuestionDao dao;

    @Test
    void successfulFileReadingTest() {
        when(fileNameProvider.getTestFileName()).thenReturn("valid_questions.csv");

        List<Question> questions = dao.findAll();

        assertFalse("Список вопросов не должен быть пуст", questions.isEmpty());
        assertEquals( "Неверное число вопросов",3, questions.size());

        Question first = questions.get(0);
        assertEquals("Неверный текст первого вопроса",
                "Is there life on Mars?", first.text());
        assertEquals("Неверное число ответов у первого вопроса",
                3, first.answers().size());
        assertEquals("Ответ #1: текст не совпал",
                "Science doesn't know this yet", first.answers().get(0).text());
        assertTrue("Ответ #1 должен быть корректным", first.answers().get(0).isCorrect());

        assertEquals("Ответ #2: текст не совпал",
                "Certainly. The red UFO is from Mars. And green is from Venus", first.answers().get(1).text());
        assertFalse("Ответ #2 должен быть некорректным", first.answers().get(1).isCorrect());

        assertEquals("Ответ #3: текст не совпал", "Absolutely not", first.answers().get(2).text());
        assertFalse("Ответ #3 должен быть некорректным", first.answers().get(2).isCorrect());
    }

    @Test
    void failedFileReadingTest() {
        when(fileNameProvider.getTestFileName()).thenReturn("invalid_questions.csv");

        assertThrows(
                RuntimeException.class,
                dao::findAll,
                "Ожидалось исключение при попытке прочитать некорректный CSV"
        );
    }

    @Test
    void notFoundFileTest() {
        when(fileNameProvider.getTestFileName()).thenReturn("questions_not_found.csv");

        assertThrows(
                QuestionReadException.class,
                dao::findAll,
                "Ожидалось исключение при отсутствии файла"
        );
    }
}

