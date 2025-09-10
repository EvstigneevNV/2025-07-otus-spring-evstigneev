package ru.otus.hw.shell;


import lombok.RequiredArgsConstructor;
import org.springframework.shell.command.annotation.Command;
import ru.otus.hw.service.TestRunnerService;

@Command(group = "Commands for testing")
@RequiredArgsConstructor
public class TestCommands {
    private final TestRunnerService testRunnerService;

    @Command(description = "Start test", command = "start", alias = "s")
    public void startTest() {
        testRunnerService.run();
    }

}
