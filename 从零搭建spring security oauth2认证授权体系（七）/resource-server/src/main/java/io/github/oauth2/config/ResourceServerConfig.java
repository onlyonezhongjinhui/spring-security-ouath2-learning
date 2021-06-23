package io.github.oauth2.config;


import io.github.oauth2.spring.SpringAccessTokenConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;

/**
 * @author Joe Grandja
 */
@Configuration
@RequiredArgsConstructor
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
    private static final String RESOURCE_ID = "order";

    @Override
    public void configure(ResourceServerSecurityConfigurer security) throws Exception {
        security
                .resourceId(RESOURCE_ID)
                .tokenServices(tokenServices());
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/api/order").hasAuthority("order:query")
                .antMatchers(HttpMethod.POST, "/api/order").hasAuthority("order:add")
                .antMatchers(HttpMethod.PUT, "/api/order").hasAuthority("order:update")
                .antMatchers(HttpMethod.DELETE, "/api/order").hasAuthority("order:delete")
                .antMatchers(HttpMethod.GET, "/api/goods").hasAuthority("goods:query")
                .antMatchers(HttpMethod.POST, "/api/goods").hasAuthority("goods:add")
                .antMatchers(HttpMethod.PUT, "/api/goods").hasAuthority("goods:update")
                .antMatchers(HttpMethod.DELETE, "/api/goods").hasAuthority("goods:delete")
                .anyRequest().authenticated()
                .and().csrf().disable().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Bean
    public ResourceServerTokenServices tokenServices() {
        // 使用远程服务请求授权服务器校验token,必须指定校验token 的url、client_id，client_secret
        RemoteTokenServices tokenServices = new RemoteTokenServices();
        tokenServices.setCheckTokenEndpointUrl("http://localhost:8090/oauth/check_token");
        tokenServices.setClientId("order");
        tokenServices.setClientSecret("secret");
        tokenServices.setAccessTokenConverter(new SpringAccessTokenConverter());
        return tokenServices;
    }


}