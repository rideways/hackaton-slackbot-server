package com.bookinggo.hackaton.domain.common;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.charset.Charset;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.apache.commons.io.FileUtils.writeStringToFile;

@Component
public class FileHandler {

    private final static Charset ENCODING = UTF_8;

    @SneakyThrows
    public void saveFile(File file, String contents) {
        writeStringToFile(file, contents, ENCODING);
    }

    @SneakyThrows
    public String readFile(String fileName) {
        return readFileToString(new File(fileName), ENCODING);
    }

}
