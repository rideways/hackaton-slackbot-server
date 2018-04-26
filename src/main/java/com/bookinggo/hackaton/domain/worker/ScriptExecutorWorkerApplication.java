package com.bookinggo.hackaton.domain.worker;

import com.bookinggo.hackaton.domain.bla.ScriptRunnerAdapter;
import com.bookinggo.hackaton.domain.socket.SocketServerRunner;
import com.bookinggo.hackaton.domain.socket.SocketServerRunner.RequestHandler;
import com.bookinggo.hackaton.domain.socket.SuperSimpleSerializer;
import com.bookinggo.hackaton.ffstp.Message;

import static com.bookinggo.hackaton.domain.bla.ScriptRunnerAdapterStaticFactory.createAdapter;
import static com.bookinggo.hackaton.domain.common.ProcessLogger.writeLog;
import static com.bookinggo.hackaton.domain.common.ScriptRunnerSocketProtocolStatus.EXECUTE;
import static com.bookinggo.hackaton.domain.common.ScriptRunnerSocketProtocolStatus.LANGUAGE;
import static com.bookinggo.hackaton.domain.common.ScriptRunnerSocketProtocolStatus.SCRIPT;
import static com.bookinggo.hackaton.domain.socket.SocketServerRunner.explodeExceptionMessages;
import static com.bookinggo.hackaton.ffstp.Status.ERROR;
import static com.bookinggo.hackaton.ffstp.Status.OK;
import static java.lang.Integer.parseInt;
import static java.lang.System.getProperty;

public class ScriptExecutorWorkerApplication implements RequestHandler {

    public final static String SCRIPT_SOCKET_PORT = "script.socket.port";
    private final static Message<String> OKAY = new Message<>(OK, "");
    private final static SuperSimpleSerializer SERIALIZER = new SuperSimpleSerializer();

    private String language;
    private ScriptRunnerAdapter runnerAdapter;

    public static void main(String[] args) {
        writeLog("getting property " + getProperty(SCRIPT_SOCKET_PORT));
        int socketPort;
        try {
            socketPort = parseInt(getProperty(SCRIPT_SOCKET_PORT));
        } catch (Exception e) {
            writeLog("Catched exception " + explodeExceptionMessages(e));
            socketPort = 6000;
            writeLog("Running on fallback port " + socketPort);
        }

        writeLog("starting worker listening on port " + socketPort);
        new SocketServerRunner(socketPort, new ScriptExecutorWorkerApplication()).startListening();
    }

    @Override
    public Message<String> handleRequest(Message<String> request) {
        if (LANGUAGE.in(request)) {
            language = request.getData();
            return OKAY;
        }
        else if (SCRIPT.in(request)) {
            String scriptCode = request.getData();
            runnerAdapter = createAdapter(scriptCode, language);
            return OKAY;
        }
        else if (EXECUTE.in(request)) {
            Object result = runnerAdapter.runScript(request.getData(), null, null);
            return new Message<>(OK, SERIALIZER.serialize(result));
        }
        else {
            return new Message<>(ERROR, null);
        }
    }
}
