package com.bookinggo.hackaton

import org.junit.runners.model.InitializationError
import spock.lang.Specification

import static java.util.UUID.randomUUID
import static org.apache.commons.io.FileUtils.deleteDirectory
import static org.apache.commons.io.FileUtils.getTempDirectoryPath

class FileSpecification extends Specification {

    private static final String temporaryTestsRoot = "${tempDirectoryPath}/slack-bot-server-tests-${randomUUID()}/"
    private String temporaryTestRoot

    def setupSpec() {
        createDirectories temporaryTestsRoot
    }

    def setup() {
        temporaryTestRoot = "${temporaryTestsRoot}/${randomUUID()}/"
        createDirectories temporaryTestRoot
    }


    def cleanupSpec() {
        deleteDirectory new File(temporaryTestsRoot)
    }

    protected getTestTemporaryPath() {
        return temporaryTestRoot
    }

    protected static createDirectories(String path) {
        if (!new File(path).mkdirs()) {
            throw new InitializationError("Temporary testing directory '${path}' could not be created")
        }
    }

}
