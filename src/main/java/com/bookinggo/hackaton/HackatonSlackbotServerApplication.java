package com.bookinggo.hackaton;

import com.bookinggo.hackaton.domain.process.ProcessSplitter;
import com.bookinggo.hackaton.domain.process.ProcessSplitter.ProcessForker;
import com.bookinggo.hackaton.domain.worker.ScriptExecutorWorkerApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HackatonSlackbotServerApplication {

    public static void main(String[] args) {
        ProcessSplitter.splitProcess(forker -> springAppMain(args, forker),
                                     () -> ScriptExecutorWorkerApplication.main(args));
    }

    public static void springAppMain(String[] args, ProcessForker forker) {
        SpringApplication.run(HackatonSlackbotServerApplication.class, args);

        // TODO: put forker into a bean
    }
}
