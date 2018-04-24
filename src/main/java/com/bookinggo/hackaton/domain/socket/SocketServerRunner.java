package com.bookinggo.hackaton.domain.socket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Function;

import static java.util.Optional.ofNullable;

@Slf4j
@RequiredArgsConstructor
public class SocketServerRunner {

    private final int port;
    private final RequestHandler requestHandler;

    public interface RequestHandler extends Function<String, String> {
        String handleRequest(String request);

        default String apply(String request) {
            return handleRequest(request);
        }
    }

    public void startListening() {
        try (
                ServerSocket serverSocket = new ServerSocket(port);
                Socket clientSocket = serverSocket.accept();
                PrintWriter outputWriter = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader inputReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {
            String inputLine;
            while ((inputLine = inputReader.readLine()) != null) {
                try {
                    String outputLine = ofNullable(requestHandler.apply(inputLine)).orElse("");
                    outputWriter.println(outputLine);
                } catch (Exception e) {
                    log.error("Exception thrown while processing line", e);
                }
            }

        } catch (Exception e) {
            log.error("Exception thrown from socket", e);
        }
    }

}
