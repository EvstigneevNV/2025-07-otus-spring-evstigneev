package ru.otus.hw.dao;

import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CsvQuestionDao implements QuestionDao {
    private final TestFileNameProvider fileNameProvider;

    @Override
    public List<Question> findAll() {

        InputStream is = getClass().getClassLoader().getResourceAsStream(fileNameProvider.getTestFileName());
        if (is == null) {
            throw new QuestionReadException("Resource not found: " + fileNameProvider.getTestFileName());
        }

        try (var isCloseable = is; Reader reader = new InputStreamReader(isCloseable, StandardCharsets.UTF_8)) {
            CsvToBean<QuestionDto> csvToBean = getCsvToBean(reader);
            csvToBean.getCapturedExceptions().forEach(e -> {
                throw new QuestionReadException(e.getMessage(), e);
            });
            return csvToBean.parse().stream().map(QuestionDto::toDomainObject).collect(Collectors.toList());
        } catch (IOException e) {
            throw new QuestionReadException("Error reading the file", e);
        }
    }

    private CsvToBean<QuestionDto> getCsvToBean(Reader reader) {
        ColumnPositionMappingStrategy<QuestionDto> strategy = new ColumnPositionMappingStrategy<>();
        strategy.setType(QuestionDto.class);
        return new CsvToBeanBuilder<QuestionDto>(reader)
                .withMappingStrategy(strategy)
                .withSeparator(';')
                .withSkipLines(1)
                .withIgnoreLeadingWhiteSpace(true)
                .withIgnoreEmptyLine(true)
                .withThrowExceptions(false)
                .build();
    }
}
