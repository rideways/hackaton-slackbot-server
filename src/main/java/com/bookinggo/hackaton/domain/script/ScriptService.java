package com.bookinggo.hackaton.domain.script;

import com.bookinggo.hackaton.domain.common.exception.ResourceAccessException;
import com.bookinggo.hackaton.domain.script.dto.ScriptDto;
import io.reactivex.Single;
import io.reactivex.functions.BiFunction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.time.Instant.now;

@Slf4j
@Service
@RequiredArgsConstructor
class ScriptService {

    private final ScriptRepository scriptRepository;
    private final ScriptOwnerRepository scriptOwnerRepository;
    private final ScriptFileHandler scriptFileHandler;

    Single<Long> createScript(ScriptDto scriptDto) {
        return saveScript(scriptDto, this::createNewScript);
    }

    private ScriptEntity createNewScript(ScriptOwnerEntity scriptOwner, ScriptDto script) {
        java.util.Date now = Date.from(now());
        return ScriptEntity.builder()
                           .createdDate(now)
                           .lastUpdatedDate(now)
                           .name(script.getName())
                           .language(script.getLanguage())
                           .location(scriptFileHandler.saveToFile(script)
                                                      .blockingGet())
                           .owner(scriptOwner)
                           .build();
    }

    Single<Long> updateScript(Long scriptId, ScriptDto scriptDto) {
        return saveScript(scriptDto, (owner, dto) -> updateExistingScript(owner, dto, scriptId));
    }

    private ScriptEntity updateExistingScript(ScriptOwnerEntity scriptOwner, ScriptDto script, Long scriptId) {
        ScriptEntity scriptEntity = scriptRepository.findById(scriptId)
                                                    .filter(s -> Objects.equals(s.getOwner()
                                                                                 .getId(), scriptOwner.getId()))
                                                    .orElseThrow(() -> new ResourceAccessException("script with ID " + scriptId));
        scriptFileHandler.updateInFile(script, scriptEntity.getLocation())
                         .blockingAwait();
        scriptEntity.setLastUpdatedDate(Date.from(now()));
        scriptEntity.setLanguage(script.getLanguage());
        scriptEntity.setName(script.getName());
        return scriptEntity;
    }

    private Single<Long> saveScript(ScriptDto scriptDto,
                                    BiFunction<ScriptOwnerEntity, ScriptDto, ScriptEntity> scriptCreator) {
        return Single.just(scriptDto)
                     .doOnSuccess(dto -> log.info("Saving script {}", dto))
                     .map(ScriptDto::getOwnerUsername)
                     .doOnSuccess(username -> log.info("Looking up user with name {}", username))
                     .map(scriptOwnerRepository::findByUsername)
                     .filter(Optional::isPresent)
                     .map(Optional::get)
                     .doOnSuccess(user -> log.info("User exists {}", user))
                     .switchIfEmpty(Single.just(scriptDto)
                                          .map(s -> createNewScriptOwner(s.getOwnerUsername(), s.getOwnerSlackUserId()))
                                          .map(scriptOwnerRepository::save)
                                          .doOnSuccess(user -> log.info("User created {}", user)))
                     .doOnSuccess(ignore -> log.info("Creating script"))
                     .zipWith(Single.just(scriptDto), scriptCreator)
                     .doOnSuccess(script -> log.info("Saving script {} in repository", script))
                     .map(scriptRepository::save)
                     .map(ScriptEntity::getId)
                     .doOnSuccess(id -> log.info("Script saved, id is {}", id));
    }

    private ScriptOwnerEntity createNewScriptOwner(String username, String slackUserId) {
        return ScriptOwnerEntity.builder()
                                .username(username)
                                .slackUserId(slackUserId)
                                .build();
    }

    List<ScriptDto> getScripts() {
        return scriptRepository.findAll()
                               .stream()
                               .map(e -> ScriptDto.builder()
                                                  .name(e.getName())
                                                  .language(e.getLanguage())
                                                  .ownerUsername(e.getOwner()
                                                                  .getUsername())
                                                  .ownerSlackUserId(e.getOwner()
                                                                     .getSlackUserId())
                                                  .build())
                               .collect(Collectors.toList());
    }

}
