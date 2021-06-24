package io.github.oauth2.spring;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;

import java.io.Serializable;
import java.util.*;

public class SpringAccessTokenConverter extends DefaultAccessTokenConverter {
    private final UserAuthenticationConverter userTokenConverter = new DefaultUserAuthenticationConverter();

    @Override
    public OAuth2Authentication extractAuthentication(Map<String, ?> map) {
        Map<String, String> parameters = new HashMap<>();
        Set<String> scope = extractScope(map);
        Authentication user = userTokenConverter.extractAuthentication(map);
        String clientId = (String) map.get(CLIENT_ID);
        parameters.put(CLIENT_ID, clientId);
        if (map.containsKey(GRANT_TYPE)) {
            parameters.put(GRANT_TYPE, (String) map.get(GRANT_TYPE));
        }
        Set<String> resourceIds = new LinkedHashSet<>(map.containsKey(AUD) ? getAudience(map)
                : Collections.emptySet());

        Collection<? extends GrantedAuthority> authorities = null;
        if (user == null && map.containsKey(AUTHORITIES)) {
            @SuppressWarnings("unchecked")
            String[] roles = ((Collection<String>) map.get(AUTHORITIES)).toArray(new String[0]);
            authorities = AuthorityUtils.createAuthorityList(roles);
        }

        Map<String, Serializable> extensions = new HashMap<>();
        extensions.put("phone", (String) map.get("phone"));
        extensions.put("openId", (String) map.get("openId"));
        extensions.put("userId", (String) map.get("userId"));

        OAuth2Request request = new OAuth2Request(parameters, clientId, authorities, true, scope, resourceIds, null, null,
                extensions);
        return new OAuth2Authentication(request, user);
    }

    private Set<String> extractScope(Map<String, ?> map) {
        Set<String> scope = Collections.emptySet();
        if (map.containsKey(SCOPE)) {
            Object scopeObj = map.get(SCOPE);
            if (scopeObj instanceof String) {
                scope = new LinkedHashSet<>(Arrays.asList(((String) scopeObj).split(" ")));
            } else if (Collection.class.isAssignableFrom(scopeObj.getClass())) {
                @SuppressWarnings("unchecked")
                Collection<String> scopeColl = (Collection<String>) scopeObj;
                scope = new LinkedHashSet<>(scopeColl);    // Preserve ordering
            }
        }
        return scope;
    }

    private Collection<String> getAudience(Map<String, ?> map) {
        Object auds = map.get(AUD);
        if (auds instanceof Collection) {
            @SuppressWarnings("unchecked")
            Collection<String> result = (Collection<String>) auds;
            return result;
        }
        return Collections.singleton((String) auds);
    }

}
