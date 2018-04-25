package com.bookinggo.hackaton.domain.process;

import com.sun.akuma.Daemon;
import com.sun.akuma.JavaVMArguments;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.function.Consumer;

import static java.util.Collections.emptyMap;

@Slf4j
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
        jvmArgs.add("-Xdebug");
        jvmArgs.add("-Xrunjdwp:server=y,transport=dt_socket,address=8081,suspend=n");
        childJvmArgs.entrySet()
                    .stream()
                    .map(entry -> "-D" + entry.getKey() + "=" + entry.getValue())
                    .forEach(jvmArgs::add);
        jvmArgs.add("-jar");

        String jarLocation = getParentDirectoryFromJar();
        log.info("jar location: " + jarLocation);

        jvmArgs.add(jarLocation);
        return jvmArgs;
    }

    public static String getParentDirectoryFromJar() {
        String dirtyPath = ProcessSplitter.class.getProtectionDomain()
                                                .getCodeSource()
                                                .getLocation()
                                                .getPath();
        String jarPath = dirtyPath.replaceAll("^.*file:/", "/"); //removes file:/ and everything before it
        jarPath = jarPath.replaceAll("jar!.*", "jar"); //removes everything after .jar, if .jar exists in dirtyPath
        jarPath = jarPath.replaceAll("%20", " "); //necessary if path has spaces within
        if (!jarPath.endsWith(".jar")) { // this is needed if you plan to run the app using Spring Tools Suit play button.
            jarPath = jarPath.replaceAll("/classes/.*", "/classes/");
        }
        return jarPath;
    }

}
