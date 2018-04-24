package com.bookinggo.hackaton.domain.script;

import com.bookinggo.hackaton.domain.common.FileHandler;
import com.bookinggo.hackaton.domain.common.exception.ScriptNotFoundException;
import com.bookinggo.hackaton.domain.process.ProcessSplitter.ProcessForker;
import com.bookinggo.hackaton.domain.socket.PortChecker;
import com.bookinggo.hackaton.domain.socket.SocketClientRunner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

@Service
@RequiredArgsConstructor
public class ScriptRunnerService {

    private static final String WORKER_HOSTNAME = "localhost";

    private final ProcessForker forker;
    private final ScriptRepository scriptRepository;
    private final PortChecker portChecker;
    private final FileHandler fileHandler;
    private final ScriptClientRunnerRegistry scriptClientRunnerRegistry;

    public Optional<String> runScript(String scriptName, List<String> parameters) {
        SocketClientRunner socketClientRunner = scriptClientRunnerRegistry.get(scriptName)
                                                                          .orElseThrow(() -> new RuntimeException("script runner not found")); // TODO: custom exception

        sendAndFailIfNotOk(SCRIPT_RUN_START, socketClientRunner::sendAndReceive, socketClientRunner::close);
        parameters.forEach(parameter -> sendAndFailIfNotOk(parameter, socketClientRunner::sendAndReceive, socketClientRunner::close));
        return socketClientRunner.sendAndReceive(SCRIPT_RUN_END);
    }

    public void startScriptWorker(long scriptId) {
        ScriptEntity scriptEntity = scriptRepository.findById(scriptId)
                                                    .orElseThrow(() -> new ScriptNotFoundException(scriptId));
        String scriptContent = fileHandler.readFile(scriptEntity.getLocation());
        SocketClientRunner socketClientRunner = runWorkerGetClient();

        initializeWorker(scriptEntity, scriptContent, socketClientRunner);
    }

    private SocketClientRunner runWorkerGetClient() {
        int port = portChecker.getAvailableWorkerPort()
                              .orElseThrow(() -> new RuntimeException("No worker port available"));
        forker.fork(singletonMap(SCRIPT_SOCKET_PORT, String.valueOf(port)));

        return new SocketClientRunner(WORKER_HOSTNAME, port);
    }

    private void initializeWorker(ScriptEntity scriptEntity, String scriptContent, SocketClientRunner socketClientRunner) {
        sendAndFailIfNotOk(scriptEntity.getLanguage(), socketClientRunner::sendAndReceive, socketClientRunner::close);
        sendAndFailIfNotOk(SCRIPT_BEGIN, socketClientRunner::sendAndReceive, socketClientRunner::close);

//        for (String line : scriptContent.split("\n")) { // TODO in case
        sendAndFailIfNotOk(scriptContent, socketClientRunner::sendAndReceive, socketClientRunner::close);
//        }

        sendAndFailIfNotOk(SCRIPT_END, socketClientRunner::sendAndReceive, socketClientRunner::close);

        scriptClientRunnerRegistry.add(scriptEntity.getName(), socketClientRunner);
    }

    private <T> void sendAndFailIfNotOk(T message, Function<T, Optional<String>> sender, Runnable closeFunction) {
        sender.apply(message)
              .filter(OK.name()::equals)
              .orElseThrow(() -> {
                  closeFunction.run();
                  return new RuntimeException("Socket unresponsive");
              });
    }

}
