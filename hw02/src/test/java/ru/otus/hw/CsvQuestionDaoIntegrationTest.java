package ru.otus.hw;

import org.junit.jupiter.api.Test;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.CsvQuestionDao;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.util.List;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CsvQuestionDaoIntegrationTest {

    private CsvQuestionDao dao;

    @Test
    void successfulFileReadingTest() {
        TestFileNameProvider provider = () -> "valid_questions.csv";
        dao = new CsvQuestionDao(provider);
        List<Question> questions = dao.findAll();

        assertThat(questions)
                .isNotEmpty()
                .hasSize(3);

        Question first = questions.get(0);
        assertThat(first.text())
                .isEqualTo("Is there life on Mars?");
        assertThat(first.answers())
                .hasSize(3)
                .extracting("text", "correct")
                .containsExactly(
                        tuple("Science doesn't know this yet", true),
                        tuple("Certainly. The red UFO is from Mars. And green is from Venus", false),
                        tuple("Absolutely not", false)
                );
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
