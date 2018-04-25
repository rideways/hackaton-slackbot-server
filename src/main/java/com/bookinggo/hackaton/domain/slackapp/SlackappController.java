package com.bookinggo.hackaton.domain.slackapp;

import com.bookinggo.hackaton.domain.slackapp.dto.request.SlackRequest;
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

import java.util.ArrayList;
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
    SlackResponse run(@RequestBody MultiValueMap<String, List<String>> requestMap) {
        log.info("Request: " + requestMap.keySet()
                                         .stream()
                                         .map(s -> s + "=" + requestMap.get(s))
                                         .reduce((s1, s2) -> s1 + "\n" + s2)
                                         .orElse("empty"));

        SlackRequest slackRequest = SlackRequest.builder(requestMap)
                                                .build();

        SlackText text = SlackText.builder(slackRequest.getText())
                                  .build();

        Command command = Command.of(text.getFirstWord());

        switch (command) {
        case ADD:
            String addScriptName = text.getSecondWord();
            String code = text.getRemainingText()
                              .orElseThrow(RuntimeException::new);
            return service.add(slackRequest.getUserName(), slackRequest.getUserId(), addScriptName, "groovy", code);
        case ADDU:
            String addURLScriptName = text.getSecondWord();

            if (text.getCodeFromURI()
                    .isPresent()) {
                return service.add(slackRequest.getUserName(), slackRequest.getUserId(), addURLScriptName, "groovy",
                                   text.getCodeFromURI()
                                       .get());
            }
            else {
                return SlackResponse.builder()
                                    .text("There were some error getting the code by URL")
                                    .build();
            }
        case RM:
            String rmScriptName = text.getSecondWord();
            return service.remove(rmScriptName);
        case RUN:
            String runScriptName = text.getSecondWord();

            List<String> args = text.getRemainingText()
                                    .map(s -> s.split(" "))
                                    .map(Arrays::asList)
                                    .orElse(Collections.emptyList());

            args = new ArrayList<>(args);
            args.add(slackRequest.getUserName());
            args.add(slackRequest.getChannelName());

            return service.run(runScriptName, args);
        case LIST:
            return service.list();
        }

        return SlackResponse.builder()
                            .text(Command.toHelp())
                            .build();
    }
}
