package io.github.oauth2.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/goods")
public class GoodsController {

    @PostMapping
    public String post() {
        return "post goods";
    }

    @PutMapping
    public String put() {
        return "put goods";
    }

    @DeleteMapping
    public String delete() {
        return "delete goods";
    }

    @GetMapping
    public String get() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return "get goods";
    }

}
