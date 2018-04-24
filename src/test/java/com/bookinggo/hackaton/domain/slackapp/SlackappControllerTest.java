package com.bookinggo.hackaton.domain.slackapp;

import com.bookinggo.hackaton.domain.slackapp.dto.response.SlackResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collections;

@RunWith(SpringRunner.class)
public class SlackappControllerTest {

    @Mock
    private SlackappService service;

    @InjectMocks
    private SlackappController controller;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.controller = new SlackappController(service);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                                 .build();
    }

    @Test
    public void addScript() throws Exception {
        String userName = "name";
        String userId = "UAAG60W49";

        Mockito.when(service.add(userName, userId, "bla", "groovy", "pulbics dsd{ {}SAdsad as"))
               .thenReturn(SlackResponse.builder()
                                        .text("Bla")
                                        .build());

        String value = getRequest(userName, userId, "add bla pulbics dsd{ {}SAdsad as");

        mockMvc.perform(MockMvcRequestBuilders.post("/slackapp")
                                              .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                                              .content(value))
               .andExpect(MockMvcResultMatchers.status()
                                               .isOk())
               .andExpect(MockMvcResultMatchers.content()
                                               .contentType(MediaType.APPLICATION_JSON_UTF8))
               .andExpect(MockMvcResultMatchers.content()
                                               .string("{\"text\":\"Bla\",\"attachments\":null}"));

    }

    @Test
    public void adduScript() throws Exception {
        String userName = "name";
        String userId = "123";

        Mockito.when(service.add(userName, userId, "bla", "groovy", getCode()))
               .thenReturn(SlackResponse.builder()
                                        .text("Bla")
                                        .build());

        String value = getRequest(userName, userId, "addu bla <" + getURL() + ">");

        mockMvc.perform(MockMvcRequestBuilders.post("/slackapp")
                                              .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                                              .content(value))
               .andExpect(MockMvcResultMatchers.status()
                                               .isOk())
               .andExpect(MockMvcResultMatchers.content()
                                               .contentType(MediaType.APPLICATION_JSON_UTF8))
               .andExpect(MockMvcResultMatchers.content()
                                               .string("{\"text\":\"Bla\",\"attachments\":null}"));
    }

    private String getURL() {
        return "https://raw.githubusercontent.com/rideways/hackaton-slackbot-server/master/src/main/java/com/bookinggo/hackaton/HackatonSlackbotServerApplication.java";
    }

    private String getCode() {
        return "package com.bookinggo.hackaton;\n" +
               "\n" +
               "import org.springframework.boot.SpringApplication;\n" +
               "import org.springframework.boot.autoconfigure.SpringBootApplication;\n" +
               "\n" +
               "@SpringBootApplication\n" +
               "public class HackatonSlackbotServerApplication {\n" +
               "\n" +
               "    public static void main(String[] args) {\n" +
               "        SpringApplication.run(HackatonSlackbotServerApplication.class, args);\n" +
               "    }\n" +
               "}\n";
    }

    @Test
    public void removeScript() throws Exception {
        Mockito.when(service.remove("bla"))
               .thenReturn(SlackResponse.builder()
                                        .text("Bla")
                                        .build());

        String value = getRequest("name", "ABC123", "rm bla pulbics dsd{ {}SAdsad as");

        mockMvc.perform(MockMvcRequestBuilders.post("/slackapp")
                                              .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                                              .content(value))
               .andExpect(MockMvcResultMatchers.status()
                                               .isOk());
    }

    @Test
    public void runScript() throws Exception {
        Mockito.when(service.run("bla", Arrays.asList("param1", "param2")))
               .thenReturn(SlackResponse.builder()
                                        .text("Bla")
                                        .build());

        String value = getRequest("name", "ABC123", "run  bla param1 param2");

        mockMvc.perform(MockMvcRequestBuilders.post("/slackapp")
                                              .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                                              .content(value))
               .andExpect(MockMvcResultMatchers.status()
                                               .isOk());
    }

    @Test
    public void runScriptWithoutParameters() throws Exception {
        Mockito.when(service.run("bla", Collections.emptyList()))
               .thenReturn(SlackResponse.builder()
                                        .text("Bla")
                                        .build());

        String value = getRequest("name", "ABC123", "run  bla");

        mockMvc.perform(MockMvcRequestBuilders.post("/slackapp")
                                              .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                                              .content(value))
               .andExpect(MockMvcResultMatchers.status()
                                               .isOk());
    }

    @SneakyThrows
    private String getRequest(String userName, String userId, String text) {

        return "team_domain=" + URLEncoder.encode("bgohackaton", "UTF-8") +
               "&channel_id=" + URLEncoder.encode("DAARFSN9J", "UTF-8") +
               "&channel_name=" + URLEncoder.encode("directmessage", "UTF-8") +
               "&user_id=" + URLEncoder.encode(userId, "UTF-8") +
               "&user_name=" + URLEncoder.encode(userName, "UTF-8") +
               "&command=" + URLEncoder.encode("/superbot", "UTF-8") +
               "&text=" + URLEncoder.encode(text, "UTF-8") +
               "&response_url=" + URLEncoder.encode("https://hooks.slack.com/commands/TABV3F3FG/351535594417/xSvpeAqCfBhPmqnTyvqF9Pu5", "UTF-8") +
               "&trigger_id=" + URLEncoder.encode("351535594465.351989513526.dffcee5c6adee435f0fcc37e2aed7a72", "UTF-8");
    }

    @Test
    public void list() throws Exception {
        Mockito.when(service.list())
               .thenReturn(SlackResponse.builder()
                                        .text("Bla")
                                        .build());

        String value = getRequest("name", "ABC123", "list");

        mockMvc.perform(MockMvcRequestBuilders.post("/slackapp")
                                              .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                                              .content(value))
               .andExpect(MockMvcResultMatchers.status()
                                               .isOk())
               .andExpect(MockMvcResultMatchers.content()
                                               .contentType(MediaType.APPLICATION_JSON_UTF8))
               .andExpect(MockMvcResultMatchers.content()
                                               .string("{\"text\":\"Bla\",\"attachments\":null}"));
    }
}