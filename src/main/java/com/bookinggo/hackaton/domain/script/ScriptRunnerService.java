package com.bookinggo.hackaton.domain.script;

import com.bookinggo.hackaton.domain.common.FileHandler;
import com.bookinggo.hackaton.domain.common.exception.ScriptNotFoundException;
import com.bookinggo.hackaton.domain.process.ProcessSplitter.ProcessForker;
import com.bookinggo.hackaton.domain.socket.PortChecker;
import com.bookinggo.hackaton.domain.socket.SocketClientRunner;
import com.bookinggo.hackaton.ffstp.Message;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import static com.bookinggo.hackaton.domain.common.ScriptRunnerSocketProtocolStatus.EXECUTE;
import static com.bookinggo.hackaton.domain.common.ScriptRunnerSocketProtocolStatus.LANGUAGE;
import static com.bookinggo.hackaton.domain.common.ScriptRunnerSocketProtocolStatus.SCRIPT;
import static com.bookinggo.hackaton.domain.worker.ScriptExecutorWorkerApplication.SCRIPT_SOCKET_PORT;
import static java.util.Collections.singletonMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScriptRunnerService {

    private static final String WORKER_HOSTNAME = "localhost";

    private final ProcessForker forker;
    private final ScriptRepository scriptRepository;
    private final PortChecker portChecker;
    private final FileHandler fileHandler;
    private final ScriptClientRunnerRegistry scriptClientRunnerRegistry;

    @PostConstruct
    public void startAllScriptsWorkers() {
        scriptRepository.findAll()
                        .forEach(this::startScriptWorker);
    }

    public String runScript(String scriptName, String parameters) {
        SocketClientRunner socketClientRunner = scriptClientRunnerRegistry.get(scriptName)
                                                                          .orElseThrow(() -> new RuntimeException("script runner not found")); // TODO: custom exception
        return socketClientRunner.sendAndReceive(new Message<>(EXECUTE.name(), parameters))
                                 .getData();
    }

    public void startScriptWorker(long scriptId) {
        try {
            startScriptWorker(scriptRepository.findById(scriptId)
                                              .orElseThrow(() -> new ScriptNotFoundException(scriptId)));
        } catch (Exception e) {
            log.error("Script worker initialization failed", e);
        }
    }

    private void startScriptWorker(ScriptEntity scriptEntity) {
        String scriptContent = fileHandler.readFile(scriptEntity.getLocation());
        SocketClientRunner socketClientRunner = runWorkerGetClient();

        initializeWorker(scriptEntity, scriptContent, socketClientRunner);
    }

    @SneakyThrows
    private SocketClientRunner runWorkerGetClient() {
        int port = portChecker.getAvailableWorkerPort()
                              .orElseThrow(() -> new RuntimeException("No worker port available"));
        log.info("Starting script worker on port {}", port);
        forker.fork(singletonMap(SCRIPT_SOCKET_PORT, String.valueOf(port)));
        log.info("Waiting for script to initialize {}");
        Thread.sleep(1000);
        log.info("Creating socket client runner");
        return new SocketClientRunner(WORKER_HOSTNAME, port);
    }

    private void initializeWorker(ScriptEntity scriptEntity, String scriptContent, SocketClientRunner socketClientRunner) {
        log.info("Initializing worker");
        log.info("Sending language");
        Message<String> response = socketClientRunner.sendAndReceive(new Message<>(LANGUAGE.name(), scriptEntity.getLanguage()));
        log.info("Response is {}", response);
        log.info("Sending code");
        response = socketClientRunner.sendAndReceive(new Message<>(SCRIPT.name(), scriptContent));
        log.info("Response is {}", response);
        log.info("Adding runner to the registry");
        scriptClientRunnerRegistry.add(scriptEntity.getName(), socketClientRunner);
    }

}
