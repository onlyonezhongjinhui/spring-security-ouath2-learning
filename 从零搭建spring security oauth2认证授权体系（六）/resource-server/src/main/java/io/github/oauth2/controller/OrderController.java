package io.github.oauth2.controller;

import io.github.oauth2.spring.SpringSecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/order")
public class OrderController {

    @PostMapping
    public String post() {
        return "post order";
    }

    @PutMapping
    public String put() {
        return "put order";
    }

    @DeleteMapping
    public String delete() {
        return "delete order";
    }

    @GetMapping
    public String get() {
        String userId = SpringSecurityUtils.getCurrentUserId().orElse("");
        String phone = SpringSecurityUtils.getCurrentUserPhone().orElse("");
        String openid = SpringSecurityUtils.getCurrentUserOpenId().orElse("");
        return "userId:" + userId + ",phone:" + phone + ",openId:" + openid;
    }

}
