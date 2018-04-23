package com.bookinggo.hackaton.domain.slackapp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("slackapp")
public class SlackappController {

    @ResponseBody
    @ResponseStatus(OK)
    @RequestMapping(method = RequestMethod.POST)
    void run(HttpServletRequest request, @RequestBody String requestString) throws UnsupportedEncodingException {
        String[] split = requestString.split("&");
        for (String s : split) {
            String[] split1 = s.split("=");
            String name = split1[0];
            String value = split1[1];
            String decodedValue = URLDecoder.decode(value, "UTF-8");
            System.out.println(name + "=" + decodedValue);
        }
    }
}
