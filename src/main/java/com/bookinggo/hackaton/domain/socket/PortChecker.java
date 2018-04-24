package com.bookinggo.hackaton.domain.socket;

import com.bookinggo.hackaton.infrastructure.properties.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PortChecker {

    private final ApplicationProperties applicationProperties;

    public Optional<Integer> getAvailableWorkerPort() {
        int rangeBegin = applicationProperties.getWorkerPortRangeBegin();
        int rangeEnd = applicationProperties.getWorkerPortRangeEnd();
        if (rangeBegin > rangeEnd) {
            throw new IllegalArgumentException("Range end must be greater than range begin");
        }

        for (int port = rangeBegin; port <= rangeEnd; ++port) {
            if (isPortAvailable(port)) {
                return Optional.of(port);
            }
        }
        return Optional.empty();
    }

    /**
     * Checks to see if a specific port is available.
     *
     * @param port the port to check for availability
     */
    private static boolean isPortAvailable(int port) {

        ServerSocket serverSocket = null;
        DatagramSocket datagramSocket = null;

        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);
            datagramSocket = new DatagramSocket(port);
            datagramSocket.setReuseAddress(true);
            return true;
        } catch (IOException ignored) {
        } finally {
            try {
                Optional.ofNullable(datagramSocket)
                        .ifPresent(DatagramSocket::close);
                Optional.ofNullable(serverSocket)
                        .ifPresent(PortChecker::closeSocket);
            } catch (Exception ignored) {
                /* should not be thrown */
            }
        }

        return false;
    }

    @SneakyThrows
    private static void closeSocket(ServerSocket socket) {
        socket.close();
    }

}
