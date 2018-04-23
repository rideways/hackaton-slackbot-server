package com.bookinggo.hackaton.domain.slackapp;

import com.bookinggo.hackaton.domain.slackapp.dto.response.SlackResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;

@RunWith(MockitoJUnitRunner.class)
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
        Mockito.when(service.add("bla", "pulbics dsd{ {}SAdsad as"))
               .thenReturn(SlackResponse.builder()
                                        .text("Bla")
                                        .build());

        String value = "token=1324324&text=add%20bla%20pulbics%20dsd%7B%20%7B%7DSAdsad%20as";

        mockMvc.perform(MockMvcRequestBuilders.post("/slackapp")
                                              .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                                              .content(value))
               .andExpect(MockMvcResultMatchers.status()
                                               .isOk());
    }

    @Test
    public void removeScript() throws Exception {
        Mockito.when(service.remove("bla"))
               .thenReturn(SlackResponse.builder()
                                        .text("Bla")
                                        .build());

        String value = "token=1324324&text=rm%20bla%20pulbics%20dsd%7B%20%7B%7DSAdsad%20as";

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

        String value = "token=1324324&text=run%20%20bla%20param1%20param2";

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

        String value = "token=1324324&text=run%20bla";

        mockMvc.perform(MockMvcRequestBuilders.post("/slackapp")
                                              .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                                              .content(value))
               .andExpect(MockMvcResultMatchers.status()
                                               .isOk());
    }
}