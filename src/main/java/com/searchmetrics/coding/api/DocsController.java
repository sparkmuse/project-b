package com.searchmetrics.coding.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DocsController {

    @GetMapping("/docs")
    public String getDocs() {
        return "docs/index.html";
    }
}
