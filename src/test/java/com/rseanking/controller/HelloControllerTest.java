package com.rseanking.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@WebAppConfiguration
public class HelloControllerTest {

    @Autowired
    WebApplicationContext wac;

    private MockMvc mockMvc;

    @Configuration
    @EnableWebMvc // Loads the DefaultFormattingConversionService to support binding to Optionals (ObjectToOptionalConverter)
    @ComponentScan(basePackages = "com.rseanking")
    public static class HelloControllerConfiguration {
    }

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void shouldHandleRequetWithoutName() throws Exception {
        mockMvc.perform(get("/hello"))
            .andExpect(status().isOk())
            .andExpect(content().string("Hello World!"));
    }

    @Test
    public void shouldHandleRequetWithName() throws Exception {
        mockMvc.perform(get("/hello").param("name", "Foo"))
            .andExpect(status().isOk())
            .andExpect(content().string("Hello Foo!"));
    }
}
