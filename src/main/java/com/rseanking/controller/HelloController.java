package com.rseanking.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloController {
    @RequestMapping(value = "/hello", produces = "text/plain")
    @ResponseBody
    private String hello(@RequestParam(required = false, value = "name") final Optional<String> name) {
        return String.format("Hello %s!", name.orElse("World"));
    }
}
