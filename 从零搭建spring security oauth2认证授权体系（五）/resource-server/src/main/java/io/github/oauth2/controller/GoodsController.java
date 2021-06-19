package io.github.oauth2.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/goods")
public class GoodsController {

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String post() {
        return "post goods";
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String put() {
        return "put goods";
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String delete() {
        return "delete goods";
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public String get() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return "get goods";
    }

}
