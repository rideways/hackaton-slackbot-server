package com.bookinggo.hackaton.domain.worker;

import com.bookinggo.hackaton.domain.bla.EmptyRunnerAdapter;
import com.bookinggo.hackaton.domain.bla.ScriptRunnerAdapter;
import com.bookinggo.hackaton.domain.socket.SocketServerRunner;
import com.bookinggo.hackaton.domain.socket.SocketServerRunner.RequestHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.bookinggo.hackaton.domain.bla.ScriptRunnerAdapterStaticFactory.createAdapter;
import static com.bookinggo.hackaton.domain.common.ProcessLogger.writeLog;
import static com.bookinggo.hackaton.domain.common.ScriptRunnerSocketProtocol.ChildMessage.ERROR_LANGUAGE_NOT_RECOGNIZED;
import static com.bookinggo.hackaton.domain.common.ScriptRunnerSocketProtocol.ChildMessage.ERROR_UNKNOWN;
import static com.bookinggo.hackaton.domain.common.ScriptRunnerSocketProtocol.ChildMessage.OK;
import static com.bookinggo.hackaton.domain.common.ScriptRunnerSocketProtocol.ParentMessage.SCRIPT_BEGIN;
import static com.bookinggo.hackaton.domain.common.ScriptRunnerSocketProtocol.ParentMessage.SCRIPT_END;
import static com.bookinggo.hackaton.domain.common.ScriptRunnerSocketProtocol.ParentMessage.SCRIPT_RUN_END;
import static com.bookinggo.hackaton.domain.common.ScriptRunnerSocketProtocol.ParentMessage.SCRIPT_RUN_START;
import static java.lang.Integer.parseInt;
import static java.lang.System.getProperty;

public class ScriptExecutorWorkerApplication implements RequestHandler {

    public final static String SCRIPT_SOCKET_PORT = "script.socket.port";

    private String language;
    private StringBuilder scriptCodeBuilder;
    private ScriptRunnerAdapter runnerAdapter;
    private boolean scriptRunInProgress = false;
    private List<String> params;

    public static void main(String[] args) {
        writeLog("getting property " + getProperty(SCRIPT_SOCKET_PORT));
        int socketPort = parseInt(getProperty(SCRIPT_SOCKET_PORT));

        writeLog("starting worker listening on port " + socketPort);
        new SocketServerRunner(socketPort, new ScriptExecutorWorkerApplication()).startListening();
    }

    @Override
    public String handleRequest(String request) {
        if (language == null) {
            writeLog("Setting language to " + request);
            language = request;
            return OK.name() + "1";
        }
        else if (scriptCodeBuilder == null && request.equalsIgnoreCase(SCRIPT_BEGIN.name())) {
            writeLog("Building script code");
            scriptCodeBuilder = new StringBuilder();
            return OK.name() + "2";
        }
        else if (scriptCodeBuilder != null) {
            if (request.equalsIgnoreCase(SCRIPT_END.name())) {
                writeLog("Finalizing script");
                String scriptCode = scriptCodeBuilder.toString();
                scriptCodeBuilder = null;
                runnerAdapter = createAdapter(scriptCode, language);
                writeLog("Created runner adapter");
                if (runnerAdapter instanceof EmptyRunnerAdapter) {
                    writeLog("Adapter is empty");
                    return ERROR_LANGUAGE_NOT_RECOGNIZED.name();
                }
                return OK.name() + "3";
            }
            else {
                writeLog("Appending request as source code");
                scriptCodeBuilder.append(request);
                return OK.name() + "4";
            }
        }
        else if (request.equalsIgnoreCase(SCRIPT_RUN_START.name())) {
            writeLog("Script starts running");
            params = new ArrayList<>();
            scriptRunInProgress = true;
            return OK.name() + "5";
        }
        else if (scriptRunInProgress && !request.equalsIgnoreCase(SCRIPT_RUN_END.name())) {
            writeLog("Adding param to script " + request);
            params.add(request);
            return OK.name() + "6";
        }
        else if (request.equalsIgnoreCase(SCRIPT_RUN_END.name())) {
            scriptRunInProgress = false;
            writeLog("Script ends running on request " + request);
            writeLog("Params to use " + Arrays.toString(params.toArray()));
            Object result = runnerAdapter.runScript(params.get(0), params.get(1), params.get(2));
            String stringResult = String.valueOf(result);
            writeLog("Ending script and returning: " + stringResult);
            return stringResult;
        }
        else {
            return ERROR_UNKNOWN.name();
        }
    }
}
