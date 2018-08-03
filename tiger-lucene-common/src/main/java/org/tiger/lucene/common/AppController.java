package org.tiger.lucene.common;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppController {

    @GetMapping("/info")
    public String getInfo() {
        return "Tiger Luncene Common Version 0.1 By Qintao";
    }
}
