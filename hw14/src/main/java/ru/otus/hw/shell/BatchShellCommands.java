package ru.otus.hw.shell;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.Comparator;

@ShellComponent
public class BatchShellCommands {

    private final JobOperator jobOperator;

    private final JobExplorer jobExplorer;

    public BatchShellCommands(JobOperator jobOperator, JobExplorer jobExplorer) {
        this.jobOperator = jobOperator;
        this.jobExplorer = jobExplorer;
    }

    @ShellMethod(key = "jobs", value = "List known job names")
    public String jobs() {
        return String.join("\n", jobExplorer.getJobNames());
    }

    @ShellMethod(key = "start", value = "Start job by name: start <jobName> [params]")
    public String start(String jobName,
                        @ShellOption(defaultValue = "") String params) throws Exception {

        String p = params == null ? "" : params.trim();
        String runId = "run.id(long)=" + System.currentTimeMillis();
        String finalParams = p.isEmpty() ? runId : (p + "," + runId);

        long executionId = jobOperator.start(jobName, finalParams);
        return "Started. executionId=" + executionId + " params=" + finalParams;
    }

    @ShellMethod(key = "restart", value = "Restart by executionId: restart <executionId>")
    public String restart(long executionId) throws Exception {
        long newExecutionId = jobOperator.restart(executionId);
        return "Restarted. newExecutionId=" + newExecutionId;
    }

    @ShellMethod(key = "restart-last", value = "Restart last FAILED/STOPPED execution for job: restart-last <jobName>")
    public String restartLast(String jobName) throws Exception {
        var jobInstances = jobExplorer.getJobInstances(jobName, 0, 20);
        var lastExec = jobInstances.stream()
                .flatMap(ji -> jobExplorer.getJobExecutions(ji).stream())
                .max(Comparator.comparing(JobExecution::getCreateTime))
                .orElseThrow(() -> new IllegalStateException("No executions for job " + jobName));

        var st = lastExec.getStatus();
        if (st != BatchStatus.FAILED && st != BatchStatus.STOPPED) {
            return "Last execution status is " + st
                    + " (restart is allowed only for FAILED/STOPPED). executionId=" + lastExec.getId();
        }

        long newExecutionId = jobOperator.restart(lastExec.getId());
        return "Restarted last executionId=" + lastExec.getId() + " -> newExecutionId=" + newExecutionId;
    }

    @ShellMethod(key = "stop", value = "Stop execution: stop <executionId>")
    public String stop(long executionId) throws Exception {
        boolean ok = jobOperator.stop(executionId);
        return ok ? "Stop requested." : "Stop request was not accepted.";
    }

    @ShellMethod(key = "status", value = "Show execution status: status <executionId>")
    public String status(long executionId) {
        var exec = jobExplorer.getJobExecution(executionId);
        if (exec == null) {
            return "Not found";
        }
        return "job=" + exec.getJobInstance().getJobName()
                + " status=" + exec.getStatus()
                + " exit=" + exec.getExitStatus().getExitCode();
    }
}