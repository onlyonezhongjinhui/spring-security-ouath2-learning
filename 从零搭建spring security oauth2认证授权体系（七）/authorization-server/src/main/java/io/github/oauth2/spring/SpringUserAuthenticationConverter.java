package io.github.oauth2.spring;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;

import java.util.LinkedHashMap;
import java.util.Map;

public class SpringUserAuthenticationConverter extends DefaultUserAuthenticationConverter {

    @Override
    public Map<String, ?> convertUserAuthentication(Authentication authentication) {
        Map<String, Object> response = new LinkedHashMap<String, Object>();
        SpringUserDetails userDetails = (SpringUserDetails) authentication.getPrincipal();
        response.put(USERNAME, authentication.getName());
        if (authentication.getAuthorities() != null && !authentication.getAuthorities().isEmpty()) {
            response.put(AUTHORITIES, AuthorityUtils.authorityListToSet(authentication.getAuthorities()));
        }
        response.put("phone", userDetails.getPhone());
        response.put("openId", userDetails.getOpenId());
        response.put("userId", userDetails.getUserId());
        return response;
    }

}
