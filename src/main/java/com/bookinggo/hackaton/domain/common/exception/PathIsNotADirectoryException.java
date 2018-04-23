package com.bookinggo.hackaton.domain.common.exception;

import java.io.File;

public class PathIsNotADirectoryException extends RuntimeException {
    public PathIsNotADirectoryException(File file) {
        super("path '" + file.getAbsolutePath() + "' must be a directory");
    }
}
