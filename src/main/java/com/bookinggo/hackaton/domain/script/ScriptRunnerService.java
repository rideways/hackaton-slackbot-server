package com.bookinggo.hackaton.domain.script;

import com.bookinggo.hackaton.domain.common.FileHandler;
import com.bookinggo.hackaton.domain.common.exception.ScriptNotFoundException;
import com.bookinggo.hackaton.domain.process.ProcessSplitter.ProcessForker;
import com.bookinggo.hackaton.domain.socket.PortChecker;
import com.bookinggo.hackaton.domain.socket.SocketClientRunner;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static com.bookinggo.hackaton.domain.common.ScriptRunnerSocketProtocol.ChildMessage.OK;
import static com.bookinggo.hackaton.domain.common.ScriptRunnerSocketProtocol.ParentMessage.SCRIPT_BEGIN;
import static com.bookinggo.hackaton.domain.common.ScriptRunnerSocketProtocol.ParentMessage.SCRIPT_END;
import static com.bookinggo.hackaton.domain.common.ScriptRunnerSocketProtocol.ParentMessage.SCRIPT_RUN_END;
import static com.bookinggo.hackaton.domain.common.ScriptRunnerSocketProtocol.ParentMessage.SCRIPT_RUN_START;
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

    public Optional<String> runScript(String scriptName, List<String> parameters) {
        SocketClientRunner socketClientRunner = scriptClientRunnerRegistry.get(scriptName)
                                                                          .orElseThrow(() -> new RuntimeException("script runner not found")); // TODO: custom exception

        sendAndFailIfNotOk(SCRIPT_RUN_START, socketClientRunner::sendAndReceive, socketClientRunner::close);
        parameters.forEach(parameter -> sendAndFailIfNotOk(parameter, socketClientRunner::sendAndReceive, socketClientRunner::close));
        return socketClientRunner.sendAndReceive(SCRIPT_RUN_END);
    }

    public void startScriptWorker(long scriptId) {
        startScriptWorker(scriptRepository.findById(scriptId)
                                          .orElseThrow(() -> new ScriptNotFoundException(scriptId)));
    }

    public void startScriptWorker(ScriptEntity scriptEntity) {
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
        Thread.sleep(1000);
        return new SocketClientRunner(WORKER_HOSTNAME, port);
    }

    private void initializeWorker(ScriptEntity scriptEntity, String scriptContent, SocketClientRunner socketClientRunner) {
        sendAndFailIfNotOk(scriptEntity.getLanguage(), socketClientRunner::sendAndReceive, socketClientRunner::close);
        sendAndFailIfNotOk(SCRIPT_BEGIN, socketClientRunner::sendAndReceive, socketClientRunner::close);

        for (String line : scriptContent.split("\n")) {
            sendAndFailIfNotOk(line, socketClientRunner::sendAndReceive, socketClientRunner::close);
        }

        sendAndFailIfNotOk(SCRIPT_END, socketClientRunner::sendAndReceive, socketClientRunner::close);

        scriptClientRunnerRegistry.add(scriptEntity.getName(), socketClientRunner);
    }

    private <T> void sendAndFailIfNotOk(T message, Function<T, Optional<String>> sender, Runnable closeFunction) {
        log.info("Sending " + message);
        sender.apply(message)
              .map(reply -> {
                  log.info("RES_: " + reply);
                  return reply;
              })
              .filter(m -> m.startsWith(OK.name()))
              .orElseThrow(() -> {
                  closeFunction.run();
                  return new RuntimeException("Socket unresponsive");
              });
    }

}
