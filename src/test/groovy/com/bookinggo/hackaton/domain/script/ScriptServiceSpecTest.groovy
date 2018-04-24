package com.bookinggo.hackaton.domain.script

import com.bookinggo.hackaton.domain.common.exception.ResourceAccessException
import com.bookinggo.hackaton.domain.script.dto.ScriptDto
import io.reactivex.Completable
import io.reactivex.Maybe
import spock.lang.Specification
import spock.lang.Subject

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric

class ScriptServiceSpecTest extends Specification {

    def scriptRepository = Mock ScriptRepository
    def scriptOwnerRepository = Mock ScriptOwnerRepository
    def scriptFileHandler = Mock ScriptFileHandler

    @Subject
    def scriptService = new ScriptService(scriptRepository, scriptOwnerRepository, scriptFileHandler)

    def "should save the script and not create owner if exists"() {
        given:
            def ownerUsername = randomAlphanumeric 5
            def scriptName = randomAlphanumeric 5
            def scriptLanguage = randomAlphanumeric 5
            def ownerSlackUserId = randomAlphanumeric 5

        and:
            def dto = new ScriptDto(scriptName, "testContents", scriptLanguage, ownerUsername, ownerSlackUserId)
            def scriptOwner = new ScriptOwnerEntity(1L, ownerUsername, ownerSlackUserId)

        when:
            def actualId = scriptService.createScript(dto)
                                        .blockingGet()

        then:
            1 * scriptOwnerRepository.findByUsername(ownerUsername) >> Optional.of(scriptOwner)
            0 * scriptOwnerRepository._
            1 * scriptFileHandler.saveToFile(dto) >> Maybe.just("testLocation")
            1 * scriptRepository.save(_ as ScriptEntity) >> { List<ScriptEntity> args ->
                assert args[0].name == scriptName
                assert args[0].language == scriptLanguage
                args[0].id = 666
                args[0]
            }

        and:
            actualId == 666
    }

    def "should save the script and create owner if not exists"() {
        given:
            def ownerUsername = randomAlphanumeric 5
            def scriptName = randomAlphanumeric 5
            def scriptLanguage = randomAlphanumeric 5
            def ownerSlackUserId = randomAlphanumeric 5

        and:
            def dto = new ScriptDto(scriptName, "testContents", scriptLanguage, ownerUsername, ownerSlackUserId)

        when:
            def actualId = scriptService.createScript(dto)
                                        .blockingGet()

        then:
            1 * scriptOwnerRepository.findByUsername(ownerUsername) >> Optional.empty()
            1 * scriptOwnerRepository.save(_ as ScriptOwnerEntity) >> { List<ScriptOwnerEntity> args ->
                assert args[0].username == ownerUsername
                args[0]
            }
            1 * scriptFileHandler.saveToFile(dto) >> Maybe.just("testLocation")
            1 * scriptRepository.save(_ as ScriptEntity) >> { List<ScriptEntity> args ->
                assert args[0].name == scriptName
                assert args[0].language == scriptLanguage
                args[0].id = 666
                args[0]
            }

        and:
            actualId == 666
    }

    def "should update existing script"() {
        given:
            def ownerUsername = randomAlphanumeric 5
            def scriptName = randomAlphanumeric 5
            def scriptLanguage = randomAlphanumeric 5
            def scriptLocation = randomAlphanumeric 5
            def ownerSlackUserId = randomAlphanumeric 5

        and:
            def dto = new ScriptDto(scriptName, "testContents", scriptLanguage, ownerUsername, ownerSlackUserId)
            def ownerId = 1L
            def scriptOwner = new ScriptOwnerEntity(ownerId, ownerUsername, ownerSlackUserId)
            def scriptId = 69L
            def scriptEntity = new ScriptEntity(null, null, null, null, null, scriptLocation, scriptOwner)

        when:
            def actualId = scriptService.updateScript(scriptId, dto)
                                        .blockingGet()

        then:
            1 * scriptOwnerRepository.findByUsername(ownerUsername) >> Optional.of(scriptOwner)
            0 * scriptOwnerRepository._
            1 * scriptRepository.findById(scriptId) >> Optional.of(scriptEntity)
            1 * scriptFileHandler.updateInFile(dto, scriptLocation) >> Completable.fromAction({})
            1 * scriptRepository.save(_ as ScriptEntity) >> { List<ScriptEntity> args ->
                assert args[0].name == scriptName
                assert args[0].language == scriptLanguage
                args[0].id = 666
                args[0]
            }

        and:
            actualId == 666
    }

    def "should throw when updating if script owner has a different id from adding user"() {
        given:
            def ownerUsername = randomAlphanumeric 5
            def scriptName = randomAlphanumeric 5
            def scriptLanguage = randomAlphanumeric 5
            def scriptLocation = randomAlphanumeric 5
            def ownerSlackUserId = randomAlphanumeric 5

        and:
            def dto = new ScriptDto(scriptName, "testContents", scriptLanguage, ownerUsername, ownerSlackUserId)
            def ownerId = 1L
            def scriptOwner = new ScriptOwnerEntity(ownerId, ownerUsername, ownerSlackUserId)
            def scriptId = 69L
            def scriptEntity = new ScriptEntity(null, null, null, null, null, scriptLocation, new ScriptOwnerEntity(ownerId + 1, ownerUsername, ownerSlackUserId))

        when:
            scriptService.updateScript(scriptId, dto)
                         .blockingGet()

        then:
            1 * scriptOwnerRepository.findByUsername(ownerUsername) >> Optional.of(scriptOwner)
            0 * scriptOwnerRepository._
            1 * scriptRepository.findById(scriptId) >> Optional.of(scriptEntity)

        and:
            thrown ResourceAccessException
    }

}
