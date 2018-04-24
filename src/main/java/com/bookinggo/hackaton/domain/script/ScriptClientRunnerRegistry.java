package com.bookinggo.hackaton.domain.script;

import com.bookinggo.hackaton.domain.socket.SocketClientRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Component
public class ScriptClientRunnerRegistry {

    private final HashMap<String, SocketClientRunner> clientRunners = new HashMap<>();

    void add(String scriptName, SocketClientRunner clientRunner) {
        if (clientRunners.containsKey(scriptName)) {
            clientRunners.get(scriptName)
                         .close();
        }
        clientRunners.put(scriptName, clientRunner);
    }

    Optional<SocketClientRunner> get(String scriptName) {
        return ofNullable(clientRunners.get(scriptName));
    }

}
