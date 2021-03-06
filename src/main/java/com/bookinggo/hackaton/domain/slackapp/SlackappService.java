package com.bookinggo.hackaton.domain.slackapp;

import com.bookinggo.hackaton.domain.script.ScriptFacade;
import com.bookinggo.hackaton.domain.script.ScriptRunnerService;
import com.bookinggo.hackaton.domain.script.dto.ScriptDto;
import com.bookinggo.hackaton.domain.slackapp.dto.response.SlackResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
class SlackappService {

    private final ScriptFacade scriptFacade;
    private final ScriptRunnerService scriptRunnerService;

    SlackResponse add(String ownerUsername, String slackUserId, String scriptName, String language, String code) {
        log.info("added script " + scriptName + "with code:" + code);

        Long scriptId = scriptFacade.saveScript(ScriptDto.builder()
                                                         .name(scriptName)
                                                         .contents(code)
                                                         .language(language)
                                                         .ownerUsername(ownerUsername)
                                                         .ownerSlackUserId(slackUserId)
                                                         .build());

        scriptRunnerService.startScriptWorker(scriptId);

        return SlackResponse.builder()
                            .text("added code successful id [" + scriptId + "]")
                            .build();
    }

    SlackResponse remove(String scriptName) {
        log.info("removed script " + scriptName);

        return SlackResponse.builder()
                            .text("Removed script " + scriptName + " successful")
                            .build();
    }

    SlackResponse list() {
        log.info("listing available scripts...");

        String text = scriptFacade.getScripts()
                                  .stream()
                                  .map(s -> "script [" + s.getName() + "] created by <@" + s.getOwnerSlackUserId() + "> with language " + s.getLanguage())
                                  .reduce((s1, s2) -> s1 + "\n" + s2)
                                  .orElse("There aren't any script");

        return SlackResponse.builder()
                            .text(text)
                            .build();
    }

    SlackResponse run(String scriptName, List<String> args) {
        log.info("running script " + scriptName + " with args " + args);

        String response = scriptRunnerService.runScript(scriptName, args)
                                             .orElse("Running scripts didn't return anything");

        return SlackResponse.builder()
                            .text(response)
                            .build();
    }

}
