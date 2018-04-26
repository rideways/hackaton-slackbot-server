package com.bookinggo.hackaton.ffstp;

import com.bookinggo.hackaton.ffstp.FriendlyForkedSocketProtocol;
import com.bookinggo.hackaton.ffstp.Message;
import com.bookinggo.hackaton.ffstp.exception.RethrownException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Function;

public class FriendlyTemplate implements AutoCloseable {

    public interface Serializer {
        <T> String serialize(T data);

        <T> T deserialize(String data, Class<T> clazz);
    }

    private final FriendlyForkedSocketProtocol ffstp;
    private final Serializer serializer;

    public FriendlyTemplate(Socket socket, Serializer serializer) {
        try {
            PrintWriter outputWriter = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            ffstp = new FriendlyForkedSocketProtocol(outputWriter, inputReader);
        } catch (Exception e) {
            throw new RethrownException(e);
        }
        this.serializer = serializer;
    }

    public <Request, Response> Message<Response> sendAndAwaitResponse(Message<Request> requestMessage, Class<Response> responseClass) {
        final Message<String> serializedRequest = new Message<>(requestMessage.getStatus(), serializer.serialize(requestMessage.getData()));
        final Message<String> serializedResponse = ffstp.sendAndAwaitResponse(serializedRequest);
        return new Message<>(serializedResponse.getStatus(), serializer.deserialize(serializedResponse.getData(), responseClass));
    }

    public <Request> void waitForRequestAndReply(Class<Request> requestClass, Function<Message<Request>, Message<?>> requestHandler) {
        ffstp.waitForRequestAndReply(serializedRequest -> {
            final Message<Request> request = new Message<>(serializedRequest.getStatus(), serializer.deserialize(serializedRequest.getData(), requestClass));
            final Message<?> response = requestHandler.apply(request);
            return new Message<>(response.getStatus(), serializer.serialize(request.getData()));
        });
    }

    public <Response> Message<Response> readMessage(Class<Response> responseClass) {
        final Message<String> serializedMessage = ffstp.readMessageRethrowErrors();
        return new Message<>(serializedMessage.getStatus(), serializer.deserialize(serializedMessage.getData(), responseClass));
    }

    public void writeMessage(Message<?> message) {
        final Message<String> serializedMessage = new Message<>(message.getStatus(), serializer.serialize(message.getData()));
        ffstp.writeMessage(serializedMessage);
    }

    @Override
    public void close() throws Exception {
        ffstp.close();
    }

}
