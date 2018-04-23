package com.bookinggo.hackaton.domain.script

import com.bookinggo.hackaton.FileSpecification
import com.bookinggo.hackaton.domain.common.FileHandler
import com.bookinggo.hackaton.domain.common.exception.PathIsNotADirectoryException
import com.bookinggo.hackaton.domain.common.exception.PathNotWriteableException
import com.bookinggo.hackaton.domain.script.dto.ScriptDto
import com.bookinggo.hackaton.infrastructure.properties.ApplicationProperties
import spock.lang.Subject

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric

class ScriptFileHandlerSpecTest extends FileSpecification {

    def fileHandler = new FileHandler()
    def applicationProperties = Mock ApplicationProperties

    @Subject
    def scriptFileHandler = new ScriptFileHandler(fileHandler, applicationProperties)

    def "should create a file containing the script contents"() {
        setup:
            def storagePath = "${testTemporaryPath}/${randomAlphanumeric(10)}/"
            createDirectories storagePath

        and:
            def scriptContents = randomAlphanumeric 100
            def ownerUsername = randomAlphanumeric 5
            def scriptDto = new ScriptDto(null, scriptContents, null, ownerUsername)

        when:
            def result = scriptFileHandler.saveToFile(scriptDto)
                                          .blockingGet()

        then:
            1 * applicationProperties.getScriptsStoragePath() >> storagePath

        and:
            scriptContents == fileHandler.readFile(result)
    }

    def "should update a file containing the script contents"() {
        setup:
            def storagePath = "${testTemporaryPath}/${randomAlphanumeric(10)}/"
            createDirectories storagePath
            String scriptName = randomAlphanumeric 5

        and:
            def scriptContents = randomAlphanumeric 100
            def ownerUsername = randomAlphanumeric 5
            def scriptDto = new ScriptDto(null, scriptContents, null, ownerUsername)

        when:
            scriptFileHandler.updateInFile(scriptDto,
                                           "/a/b/${scriptName}")
                             .blockingAwait()

        then:
            1 * applicationProperties.getScriptsStoragePath() >> storagePath

        and:
            scriptContents == fileHandler.readFile("${storagePath}/${ownerUsername}/${scriptName}")
    }

    def "should throw if storage path is not directory"() {
        setup:
            def storagePath = "${testTemporaryPath}/${randomAlphanumeric(10)}/"
            new File(storagePath).createNewFile()

        and:
            def scriptContents = randomAlphanumeric 100
            def ownerUsername = randomAlphanumeric 5
            def scriptDto = new ScriptDto(null, scriptContents, null, ownerUsername)

        when:
            scriptFileHandler.saveToFile(scriptDto)
                             .blockingGet()

        then:
            1 * applicationProperties.getScriptsStoragePath() >> storagePath

        and:
            thrown PathIsNotADirectoryException
    }

    def "should throw if storage path is not writeable"() {
        setup:
            def storagePath = "${testTemporaryPath}/${randomAlphanumeric(10)}/"
            createDirectories storagePath
            new File(storagePath).setWritable false

        and:
            def scriptContents = randomAlphanumeric 100
            def ownerUsername = randomAlphanumeric 5
            def scriptDto = new ScriptDto(null, scriptContents, null, ownerUsername)

        when:
            scriptFileHandler.saveToFile(scriptDto)
                             .blockingGet()

        then:
            1 * applicationProperties.getScriptsStoragePath() >> storagePath

        and:
            thrown PathNotWriteableException
    }
}
