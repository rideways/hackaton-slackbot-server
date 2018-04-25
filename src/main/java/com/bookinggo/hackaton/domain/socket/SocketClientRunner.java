package com.bookinggo.hackaton.domain.socket;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Optional;

import static java.util.UUID.randomUUID;

@Slf4j
public class SocketClientRunner {
    private final Socket workerSocket;
    private final PrintWriter outputWriter;
    private final BufferedReader inputReader;

    public SocketClientRunner(String host, int port) {
        log.info("Opening up socket {}:{}", host, port);
        try {
            Thread.sleep(1000);
            workerSocket = new Socket(host, port);
            outputWriter = new PrintWriter(workerSocket.getOutputStream(), true);
            inputReader = new BufferedReader(new InputStreamReader(workerSocket.getInputStream()));
        } catch (Exception e) {
            log.error("Opening up socket failed", e);
            throw new RuntimeException(e);
        }
    }

    public Optional<String> sendAndReceive(Enum<?> command) {
        return sendAndReceive(command.name());
    }

    public Optional<String> sendAndReceive(String message) {
        if (message.contains("\n")) {
            throw new RuntimeException("DON'T DO THAT");
        }
        try {
            String requestId = randomUUID().toString();
            log.info("{}] Sending message: {}", requestId, message);
            outputWriter.println(message);
            Optional<String> response = Optional.of(inputReader.readLine());
            log.info("{}] Received response: {}", requestId, response);
            return response;
        } catch (Exception e) {
            log.error("Sending message failed", e);
            return Optional.empty();
        }
    }

    public void close() {
        closeCatchAnyException(Socket::close, workerSocket);
        closeCatchAnyException(PrintWriter::close, outputWriter);
        closeCatchAnyException(BufferedReader::close, inputReader);
    }

    private interface CheckedConsumer<T> {
        void accept(T t) throws Exception;
    }

    private <T> void closeCatchAnyException(CheckedConsumer<T> consumer, T t) {
        try {
            consumer.accept(t);
        } catch (Exception e) {
            log.error("Closing failed", e);
        }
    }

}
