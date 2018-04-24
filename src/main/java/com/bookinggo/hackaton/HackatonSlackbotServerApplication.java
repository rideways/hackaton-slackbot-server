package com.bookinggo.hackaton;

import com.bookinggo.hackaton.domain.process.ProcessSplitter;
import com.bookinggo.hackaton.domain.process.ProcessSplitter.ProcessForker;
import com.bookinggo.hackaton.domain.worker.ScriptExecutorWorkerApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.Optional;

import static com.bookinggo.hackaton.domain.common.ProcessLogger.writeLog;

@SpringBootApplication
public class HackatonSlackbotServerApplication {

    private static ProcessForker FORKER;

    public static void main(String[] args) {
        writeLog("Starting process with args " + Arrays.toString(args));
        ProcessSplitter.splitProcess(forker -> springAppMain(args, forker),
                                     () -> ScriptExecutorWorkerApplication.main(args));
    }

    private static void springAppMain(String[] args, ProcessForker forker) {
        FORKER = forker; // TODO: workaround for bean creation, could be improved, possibly.
        SpringApplication.run(HackatonSlackbotServerApplication.class, args);
    }

    @Bean
    ProcessForker forker() {
        return Optional.ofNullable(FORKER)
                       .orElseGet(() -> childJvmArgs -> {throw new RuntimeException("bla");});
    }
}
