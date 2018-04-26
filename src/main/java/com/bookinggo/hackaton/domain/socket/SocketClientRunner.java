package com.bookinggo.hackaton.domain.socket;

import com.bookinggo.hackaton.ffstp.FriendlyTemplate;
import com.bookinggo.hackaton.ffstp.Message;
import lombok.extern.slf4j.Slf4j;

import java.net.Socket;

import static java.util.UUID.randomUUID;

@Slf4j
public class SocketClientRunner {
    private final Socket workerSocket;
    private final FriendlyTemplate ffstp;

    public SocketClientRunner(String host, int port) {
        log.info("Opening up socket {}:{}", host, port);
        try {
            Thread.sleep(1000);
            workerSocket = new Socket(host, port);
            ffstp = new FriendlyTemplate(workerSocket, new SuperSimpleSerializer());
        } catch (Exception e) {
            log.error("Opening up socket failed", e);
            throw new RuntimeException(e);
        }
    }

    public Message<String> sendAndReceive(Message<String> message) {
        try {
            String requestId = randomUUID().toString();
            log.info("{}] Sending message: {}", requestId, message);
            Message<String> response = ffstp.sendAndAwaitResponse(message, String.class);
            log.info("{}] Response message: {}", requestId, response);
            return response;
        } catch (Exception e) {
            log.error("Sending message failed", e);
            return Message.empty();
        }
    }

    public void close() {
        closeCatchAnyException(Socket::close, workerSocket);
        closeCatchAnyException(FriendlyTemplate::close, ffstp);
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
