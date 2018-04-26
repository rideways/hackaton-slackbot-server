package com.bookinggo.hackaton.ffstp;

import com.bookinggo.hackaton.ffstp.Message;
import com.bookinggo.hackaton.ffstp.exception.InvalidHeaderException;
import com.bookinggo.hackaton.ffstp.exception.InvalidMessageLengthException;
import com.bookinggo.hackaton.ffstp.exception.MissingDataException;
import com.bookinggo.hackaton.ffstp.exception.RethrownException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;
import java.util.function.Function;

public class FriendlyForkedSocketProtocol implements AutoCloseable {

    private final static String PROTOCOL_HEADER = "FFS";
    private final static char MESSAGE_DELIMITER = ';';

    private final PrintWriter outputWriter;
    private final BufferedReader inputReader;

    FriendlyForkedSocketProtocol(PrintWriter outputWriter, BufferedReader inputReader) {
        this.outputWriter = outputWriter;
        this.inputReader = inputReader;
    }

    public Message<String> sendAndAwaitResponse(Message<String> requestMessage) {
        writeMessage(requestMessage);
        return readMessageRethrowErrors();
    }

    public void waitForRequestAndReply(Function<Message<String>, Message<String>> requestHandler) {
        final Message<String> requestMessage = readMessageRethrowErrors();
        final Message<String> responseMessage = requestHandler.apply(requestMessage);
        writeMessage(responseMessage);
    }

    Message<String> readMessageRethrowErrors() {
        try {
            return readMessage();
        } catch (Exception e) {
            throw new RethrownException(e);
        }
    }

    private Message<String> readMessage() throws IOException {
        final String messageHeader = readDataToBuffer(4);
        if (!Objects.equals(messageHeader, PROTOCOL_HEADER + ";")) {
            throw new InvalidHeaderException(messageHeader);
        }

        final String status = readUntilDelimiter();
        final String dataBytesAmount = readUntilDelimiter();
        int dataBytesAmountAsInt;
        try {
            dataBytesAmountAsInt = Integer.parseInt(dataBytesAmount);
        } catch (NumberFormatException e) {
            throw new InvalidMessageLengthException(dataBytesAmount, e);
        }
        if (dataBytesAmountAsInt < 0) {
            throw new InvalidMessageLengthException(dataBytesAmountAsInt);
        }
        final String messageBody = readDataToBuffer(dataBytesAmountAsInt);
        readUntilDelimiter();
        return new Message<>(status, messageBody);
    }

    private String readUntilDelimiter() throws IOException {
        int currentCharacter;
        StringBuilder resultBuilder = new StringBuilder();
        while ((currentCharacter = inputReader.read()) != MESSAGE_DELIMITER) {
            if (currentCharacter == -1) {
                throw new MissingDataException(resultBuilder.toString());
            }
            resultBuilder.append((char) currentCharacter);
        }
        return resultBuilder.toString();
    }

    private String readDataToBuffer(int bufferSize) throws IOException {
        char[] buffer = new char[bufferSize];
        if (inputReader.read(buffer, 0, bufferSize) == -1) {
            throw new MissingDataException(buffer);
        }
        return new String(buffer);
    }

    void writeMessage(Message<String> message) {
        final int dataBytesAmount = message.getData()
                                           .getBytes().length;

        final String messageToSend = PROTOCOL_HEADER + MESSAGE_DELIMITER +
                                     message.getStatus() + MESSAGE_DELIMITER +
                                     dataBytesAmount + MESSAGE_DELIMITER +
                                     message.getData() + MESSAGE_DELIMITER;

        outputWriter.print(messageToSend);
        outputWriter.flush();
    }

    @Override
    public void close() throws Exception {
        try {
            inputReader.close();
        } finally {
            outputWriter.close();
        }
    }
}
