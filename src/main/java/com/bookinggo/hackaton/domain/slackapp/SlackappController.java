package com.bookinggo.hackaton.domain.slackapp;

import com.bookinggo.hackaton.domain.slackapp.dto.response.SlackResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("slackapp")
class SlackappController {

    @Autowired
    private final SlackappService service;

    @ResponseBody
    @ResponseStatus(OK)
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    SlackResponse run(@RequestBody MultiValueMap<String, List<String>> slackRequest) {
        if (slackRequest.get("text") == null) {
            throw new IllegalArgumentException("missing text key");
        }

        SlackText text = SlackText.builder(String.valueOf(slackRequest.get("text")
                                                                      .get(0)))
                                  .build();
        Command command = Command.of(text.getFirstWord());

        switch (command) {
        case ADD:
            String addScriptName = text.getSecondWord();
            String code = text.getRemainingText();
            return service.add(addScriptName, code);
        case RM:
            String rmScriptName = text.getSecondWord();
            return service.remove(rmScriptName);
        case RUN:
            String runScriptName = text.getSecondWord();

            List<String> args = Optional.ofNullable(text.getRemainingText())
                                        .map(s -> s.split(" "))
                                        .map(Arrays::asList)
                                        .orElse(Collections.emptyList());

            return service.run(runScriptName, args);
        case LIST:
            return service.list();
        }

        return SlackResponse.builder()
                            .text(Command.toHelp())
                            .build();
    }

}
