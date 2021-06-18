package io.github.oauth2.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

/**
 * @author Joe Grandja
 */
@Configuration
@RequiredArgsConstructor
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("permitAll()")
                .allowFormAuthenticationForClients();
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("order")
                .authorizedGrantTypes("authorization_code", "client_credentials", "password", "implicit", "refresh_token")
                .scopes("read", "write")
                .secret("{noop}secret")
                .autoApprove(true)
                .redirectUris("http://www.baidu.com");
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                .authenticationManager(this.authenticationManager)
                .userDetailsService(this.userDetailsService)
                .tokenStore(tokenStore());
    }

    @Bean
    public TokenStore tokenStore() {
        return new InMemoryTokenStore();
    }

//    private final ClientDetailsService clientDetailsService;

//    @Override
//    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
//        endpoints
//                .authenticationManager(this.authenticationManager)
//                .tokenStore(tokenStore());
//                .userApprovalHandler(userApprovalHandler())
//                .accessTokenConverter(accessTokenConverter());
//}

//    @Bean
//    public UserApprovalHandler userApprovalHandler() {
//        ApprovalStoreUserApprovalHandler userApprovalHandler = new ApprovalStoreUserApprovalHandler();
//        userApprovalHandler.setApprovalStore(approvalStore());
//        userApprovalHandler.setClientDetailsService(this.clientDetailsService);
//        userApprovalHandler.setRequestFactory(new DefaultOAuth2RequestFactory(this.clientDetailsService));
//        return userApprovalHandler;
//    }

//    @Bean
//    public TokenStore tokenStore() {
//        JwtTokenStore tokenStore = new JwtTokenStore(accessTokenConverter());
//        tokenStore.setApprovalStore(approvalStore());
//        return tokenStore;
//    }

//    @Bean
//    public JwtAccessTokenConverter accessTokenConverter() {
//        final RsaSigner signer = new RsaSigner(KeyConfig.getSignerKey());
//
//        JwtAccessTokenConverter converter = new JwtAccessTokenConverter() {
//            private final JsonParser objectMapper = JsonParserFactory.create();
//
//            @Override
//            protected String encode(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
//                String content;
//                try {
//                    content = this.objectMapper.formatMap(getAccessTokenConverter().convertAccessToken(accessToken, authentication));
//                } catch (Exception ex) {
//                    throw new IllegalStateException("Cannot convert access token to JSON", ex);
//                }
//                Map<String, String> headers = new HashMap<>();
//                headers.put("kid", KeyConfig.VERIFIER_KEY_ID);
//                return JwtHelper.encode(content, signer, headers).getEncoded();
//            }
//        };
//        converter.setSigner(signer);
//        converter.setVerifier(new RsaVerifier(KeyConfig.getVerifierKey()));
//        return converter;
//    }

//    @Bean
//    public ApprovalStore approvalStore() {
//        return new InMemoryApprovalStore();
//    }
//
//    @Bean
//    public JWKSet jwkSet() {
//        RSAKey.Builder builder = new RSAKey.Builder(KeyConfig.getVerifierKey())
//                .keyUse(KeyUse.SIGNATURE)
//                .algorithm(JWSAlgorithm.RS256)
//                .keyID(KeyConfig.VERIFIER_KEY_ID);
//        return new JWKSet(builder.build());
//    }
}