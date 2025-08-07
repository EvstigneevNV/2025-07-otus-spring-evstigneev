import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.otus.hw.dao.CsvQuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.service.IOService;
import ru.otus.hw.service.TestServiceImpl;

import java.util.List;
import java.util.Scanner;

import static org.mockito.Mockito.*;

class TestServiceImplTest {

    @Mock
    private IOService ioService;

    @Mock
    private Scanner scanner;

    @Mock
    private CsvQuestionDao csvQuestionDao;

    private TestServiceImpl testServiceImpl;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        testServiceImpl = new TestServiceImpl(ioService, csvQuestionDao, scanner);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void executeTest() {
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
        when(csvQuestionDao.findAll()).thenReturn(List.of(q1, q2, q3));

        testServiceImpl.executeTest();

        InOrder inOrder = inOrder(ioService, scanner, csvQuestionDao);

        inOrder.verify(ioService).printLine("");
        inOrder.verify(ioService).printFormattedLine("Please answer the questions below%n");
        inOrder.verify(ioService).printLine("Your name: ");
        inOrder.verify(scanner).nextLine();
        inOrder.verify(ioService).printLine("Your last name: ");
        inOrder.verify(scanner).nextLine();

        inOrder.verify(ioService).printLine("Is there life on Mars?");
        inOrder.verify(ioService).printFormattedLine("\t-%s (%s)",
                "Science doesn't know this yet", "Yes");
        inOrder.verify(ioService).printFormattedLine("\t-%s (%s)",
                "Certainly. The red UFO is from Mars. And green is from Venus", "No");
        inOrder.verify(ioService).printFormattedLine("\t-%s (%s)",
                        "Absolutely not", "No");

        inOrder.verify(ioService).printLine("How should resources be loaded form jar in Java?");
        inOrder.verify(ioService).printFormattedLine("\t-%s (%s)",
                        "ClassLoader#getResourceAsStream or ClassPathResource#getInputStream", "Yes");
        inOrder.verify(ioService).printFormattedLine("\t-%s (%s)",
                        "ClassLoader#getResource#getFile + FileReader", "No");
        inOrder.verify(ioService).printFormattedLine("\t-%s (%s)",
                        "Wingardium Leviosa", "No");

        inOrder.verify(ioService).printLine("Which option is a good way to handle the exception?");
        inOrder.verify(ioService).printFormattedLine("\t-%s (%s)",
                        "@SneakyThrow", "No");
        inOrder.verify(ioService).printFormattedLine("\t-%s (%s)",
                        "e.printStackTrace()", "No");
        inOrder.verify(ioService).printFormattedLine("\t-%s (%s)",
                        "Rethrow with wrapping in business exception (for example, QuestionReadException)", "Yes");
        inOrder.verify(ioService).printFormattedLine("\t-%s (%s)",
                        "Ignoring exception", "No");

        inOrder.verifyNoMoreInteractions();
    }
}
