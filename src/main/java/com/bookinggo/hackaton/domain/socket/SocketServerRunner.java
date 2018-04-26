package com.bookinggo.hackaton.domain.socket;

import com.bookinggo.hackaton.ffstp.FriendlyTemplate;
import com.bookinggo.hackaton.ffstp.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Function;

import static com.bookinggo.hackaton.domain.common.ProcessLogger.writeLog;
import static com.bookinggo.hackaton.ffstp.Message.empty;
import static com.bookinggo.hackaton.ffstp.Status.DIE;
import static com.bookinggo.hackaton.ffstp.Status.OK;

@Slf4j
@RequiredArgsConstructor
public class SocketServerRunner {

    private final int port;
    private final RequestHandler requestHandler;

    public interface RequestHandler extends Function<Message<String>, Message<String>> {
        Message<String> handleRequest(Message<String> request);

        default Message<String> apply(Message<String> request) {
            return handleRequest(request);
        }
    }

    public void startListening() {
        try (
                ServerSocket serverSocket = new ServerSocket(port);
                Socket clientSocket = serverSocket.accept();
                FriendlyTemplate ffstp = new FriendlyTemplate(clientSocket, new SuperSimpleSerializer())
        ) {
            writeLog("Sockets created, waiting for request");

            for (Message<String> message = empty();
                 message.getStatusAsEnum() != DIE;
                 message = ffstp.readMessage(String.class)) {
                if (message.getStatus() == null) {
                    continue;
                }
                writeLog("Server received " + message);
                Message<String> response = requestHandler.apply(message);
                ffstp.writeMessage(response);
            }
            ffstp.writeMessage(new Message<>(OK, "x_x"));
        } catch (Exception e) {
            writeLog("Exception thrown from socket:\n" + explodeExceptionMessages(e));
        }
    }

    public static String explodeExceptionMessages(Exception exception) {
        Throwable cause = exception;
        StringBuilder messagesBuilder = new StringBuilder();
        while (cause != null) {
            messagesBuilder.append('\t')
                           .append(cause.getMessage())
                           .append("\n");
            cause = cause.getCause();
        }
        return messagesBuilder.toString();
    }

}
