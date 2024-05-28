package com.company.stories.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class ClientForwardController {

    @GetMapping(value = "/**/{path:[^\\.]*}")
    public String forward() {
        log.info("Getting frontend");
        return "forward:/";
    }
}
