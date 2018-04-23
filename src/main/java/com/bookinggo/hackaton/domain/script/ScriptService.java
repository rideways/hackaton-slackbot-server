package com.bookinggo.hackaton.domain.script;

import com.bookinggo.hackaton.domain.common.exception.ResourceAccessException;
import com.bookinggo.hackaton.domain.script.dto.ScriptDto;
import io.reactivex.Single;
import io.reactivex.functions.BiFunction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.Objects;
import java.util.Optional;

import static io.reactivex.Single.just;
import static java.time.Instant.now;

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
        return just(scriptDto).map(ScriptDto::getOwnerUsername)
                              .map(scriptOwnerRepository::findByUsername)
                              .filter(Optional::isPresent)
                              .map(Optional::get)
                              .switchIfEmpty(just(scriptDto).map(ScriptDto::getOwnerUsername)
                                                            .map(this::createNewScriptOwner)
                                                            .map(scriptOwnerRepository::save))
                              .zipWith(just(scriptDto), scriptCreator)
                              .map(scriptRepository::save)
                              .map(ScriptEntity::getId);
    }

    private ScriptOwnerEntity createNewScriptOwner(String username) {
        return ScriptOwnerEntity.builder()
                                .username(username)
                                .build();
    }

}
