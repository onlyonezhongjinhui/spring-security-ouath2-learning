package io.github.oauth2.spring;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import java.io.Serializable;
import java.util.Optional;

public class SpringSecurityUtils {

    public static Optional<String> getCurrentUserPhone() {
        return Optional.ofNullable((String) getAuthenticationExtensions("phone"));
    }

    public static Optional<String> getCurrentUserOpenId() {
        return Optional.ofNullable((String) getAuthenticationExtensions("openId"));
    }

    public static Optional<String> getCurrentUserId() {
        return Optional.ofNullable((String) getAuthenticationExtensions("userId"));
    }

    private static Serializable getAuthenticationExtensions(String key) {
        OAuth2Authentication auth2Authentication = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
        if (auth2Authentication == null) {
            return null;
        }
        return auth2Authentication.getOAuth2Request().getExtensions().get(key);
    }

}
