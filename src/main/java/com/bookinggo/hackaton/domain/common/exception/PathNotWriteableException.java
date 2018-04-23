package com.bookinggo.hackaton.domain.common.exception;

import java.io.File;

public class PathNotWriteableException extends RuntimeException {
    public PathNotWriteableException(File file) {
        super("write permissions are required for '" + file.getAbsolutePath() + "'");
    }
}
