package com.bookinggo.hackaton.domain.process;

import com.sun.akuma.Daemon;
import com.sun.akuma.JavaVMArguments;
import lombok.SneakyThrows;

import java.util.Map;
import java.util.function.Consumer;

import static java.util.Collections.emptyMap;

public class ProcessSplitter {

    public interface ProcessForker {
        /**
         * Forks this process with only current JVM arguments passed to the child process.
         *
         * @see #splitProcess(Consumer, Runnable)
         */
        default void fork() {
            fork(emptyMap());
        }

        /**
         * Forks this process and appends given JVM arguments to the child process.
         *
         * @param childJvmArgs to append to the child process
         *
         * @see #splitProcess(Consumer, Runnable)
         */
        void fork(Map<String, String> childJvmArgs);
    }

    /**
     * If custom JVM parameters will be passed to the children processes, at least one
     * custom JVM (-D) parameter must be passed to the main process.
     * Otherwise we cannot easily determine where a custom parameter should be inserted.
     * All custom parameters will be injected after the first custom one found.
     * If no custom parameters will be passed during forking, parent can start without any.
     */
    public static void splitProcess(Consumer<ProcessForker> parentFlow, Runnable childFlow) {
        splitProcess(new Daemon(), new JvmArgumentsProvider(), parentFlow, childFlow);
    }

    @SneakyThrows
    static void splitProcess(Daemon daemon, JvmArgumentsProvider jvmArgumentsProvider, Consumer<ProcessForker> parentFlow, Runnable childFlow) {
        if (daemon.isDaemonized()) {
            daemon.init();
            childFlow.run();
        }
        else {
            parentFlow.accept(childJvmArgs -> daemon.daemonize(appendJvmArgsToCurrent(jvmArgumentsProvider, childJvmArgs)));
        }
    }

    @SneakyThrows
    private static JavaVMArguments appendJvmArgsToCurrent(JvmArgumentsProvider jvmArgumentsProvider, Map<String, String> childJvmArgs) {
        JavaVMArguments jvmArgs = new JavaVMArguments();
        boolean userArgsAdded = childJvmArgs.isEmpty();
        for (String currentJvmArg : jvmArgumentsProvider.getCurrentArguments()) {
            jvmArgs.add(currentJvmArg);
            if (!userArgsAdded && currentJvmArg.startsWith("-D")) {
                childJvmArgs.entrySet()
                            .stream()
                            .map(entry -> "-D" + entry.getKey() + "=" + entry.getValue())
                            .forEach(jvmArgs::add);
                userArgsAdded = true;
            }
        }
        if (!userArgsAdded) {
            throw new JvmArgumentsConcatenationException();
        }
        return jvmArgs;
    }

}
