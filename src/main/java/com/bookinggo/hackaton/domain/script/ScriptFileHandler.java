package com.bookinggo.hackaton.domain.script;

import com.bookinggo.hackaton.domain.common.FileHandler;
import com.bookinggo.hackaton.domain.common.exception.PathIsNotADirectoryException;
import com.bookinggo.hackaton.domain.common.exception.PathNotWriteableException;
import com.bookinggo.hackaton.domain.script.dto.ScriptDto;
import com.bookinggo.hackaton.infrastructure.properties.ApplicationProperties;
import io.reactivex.Completable;
import io.reactivex.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;

import static io.reactivex.Completable.fromSingle;
import static io.reactivex.Single.error;
import static io.reactivex.Single.just;
import static java.util.UUID.randomUUID;

@Slf4j
@Component
@RequiredArgsConstructor
class ScriptFileHandler {

    private final FileHandler fileHandler;
    private final ApplicationProperties applicationProperties;

    Single<String> saveToFile(ScriptDto script) {
        return saveToFile(script, randomUUID().toString());
    }

    Completable updateInFile(ScriptDto script, String scriptLocation) {
        return fromSingle(saveToFile(script, new File(scriptLocation).getName()));
    }

    private Single<String> saveToFile(ScriptDto script, String scriptFileName) {
        return just(applicationProperties).map(ApplicationProperties::getScriptsStoragePath)
                                          .map(File::new)
                                          .flatMap(this::checkStoragePermissions)
                                          .map(scriptsStorage -> buildOwnerScriptsDirectory(script, scriptsStorage))
                                          .doOnSuccess(scriptsStorage -> log.info("Building scripts storage directory {}", scriptsStorage))
                                          .doOnSuccess(File::mkdirs)
                                          .map(s -> buildScriptFile(s, scriptFileName))
                                          .doOnSuccess(scriptFile -> log.info("Saving script file {}", scriptFile.getAbsolutePath()))
                                          .doOnSuccess(scriptFile -> fileHandler.saveFile(scriptFile, script.getContents()))
                                          .map(File::getAbsolutePath);
    }

    private Single<File> checkStoragePermissions(File scriptsStorage) {
        return just(scriptsStorage).filter(File::isDirectory)
                                   .switchIfEmpty(error(() -> new PathIsNotADirectoryException(scriptsStorage)))
                                   .filter(File::canWrite)
                                   .switchIfEmpty(error(() -> new PathNotWriteableException(scriptsStorage)));
    }

    private File buildOwnerScriptsDirectory(ScriptDto script, File scriptsStorage) {
        return new File(scriptsStorage.getAbsolutePath()
                        + '/' + script.getOwnerUsername());
    }

    private File buildScriptFile(File ownerScripsStorage, String scriptFileName) {
        return new File(ownerScripsStorage.getAbsolutePath()
                        + '/' + scriptFileName);
    }

}
