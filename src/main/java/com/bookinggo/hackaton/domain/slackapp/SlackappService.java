package com.bookinggo.hackaton.domain.slackapp;

import com.bookinggo.hackaton.domain.slackapp.dto.response.SlackResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
class SlackappService {

    SlackResponse add(String scriptName, String code) {
        log.info("added script " + scriptName + "with code:" + code);

        return SlackResponse.builder()
                            .text("added code successful")
                            .build();
    }

    SlackResponse remove(String scriptName) {
        log.info("added script " + scriptName);

        return SlackResponse.builder()
                            .text("Removed script " + scriptName + " successful")
                            .build();
    }

    SlackResponse list() {
        log.info("listing available scripts...");

        return SlackResponse.builder()
                            .text("available scripts are: []")
                            .build();
    }

    SlackResponse run(String scriptName, List<String> args) {
        log.info("running script " + scriptName + " with args " + args);

        return SlackResponse.builder()
                            .text("Running scripts")
                            .build();
    }

}
