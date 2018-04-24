package com.bookinggo.hackaton.domain.common;

import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Random;

import static java.lang.String.format;
import static java.lang.System.out;
import static java.lang.management.ManagementFactory.getRuntimeMXBean;
import static java.nio.file.Files.write;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.time.Instant.now;
import static java.util.Arrays.stream;
import static org.apache.commons.codec.digest.DigestUtils.md2Hex;

public class ProcessLogger {

    private static final String LOG_FILE = "/tmp/pid-" + getPid() + ".log";
    private static final String COLOR = getRandomColorAnsi();

    static {
        File file = new File(LOG_FILE);
        if (!file.exists()) {
            try {
                boolean createdNew = file.createNewFile();
                if (!createdNew) {
                    throw new RuntimeException("Log file " + LOG_FILE + " already exists, I don't want to mess anything up so I'm aborting");
                }
            } catch (IOException e) {
                throw new RuntimeException("Cannot create log file " + LOG_FILE + ", cause " + e);
            }
        }
    }

    @SneakyThrows
    public static void writeLog(String contents) {
        long now = now().toEpochMilli();
        out.println("[" + format("%d", now) + "] " + contents);
        contents = stream(contents.split("\n")).map(c -> "[" + format("%d", now) + "|" + getHumanPid() + "] " + COLOR + c + getResetColorAnsi())
                                               .reduce((a, b) -> a + '\n' + b)
                                               .orElse(contents);
        write(Paths.get(LOG_FILE), ("\n" + contents).getBytes(), APPEND);
    }

    private static String getResetColorAnsi() {
        return "\\e[0m";
    }

    private static String getRandomColorAnsi() {
        return "\\e[0;3" + (new Random().nextInt(8) + 1) + "m";
    }

    private static String getHumanPid() {
        return md2Hex(getPid().getBytes()).substring(0, 3)
                                          .toUpperCase();
    }

    static String getPid() {
        return getRuntimeMXBean().getName()
                                 .split("@")[0];
    }

}
