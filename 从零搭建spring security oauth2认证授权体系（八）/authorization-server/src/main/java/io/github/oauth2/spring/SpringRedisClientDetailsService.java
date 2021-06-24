package io.github.oauth2.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class SpringRedisClientDetailsService implements ClientDetailsService {
    private static final String CLIENT_FIELDS_FOR_UPDATE = "resource_ids, scope, "
            + "authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, "
            + "refresh_token_validity, additional_information, autoapprove";
    private static final String CLIENT_FIELDS = "client_secret, " + CLIENT_FIELDS_FOR_UPDATE;
    private static final String BASE_FIND_STATEMENT = "select client_id, " + CLIENT_FIELDS
            + " from oauth_client_details";
    private static final String DEFAULT_SELECT_STATEMENT = BASE_FIND_STATEMENT + " where client_id = ?";
    private String selectClientDetailsSql = DEFAULT_SELECT_STATEMENT;
    private final RedisTemplate<String, ClientDetails> redisTemplate;
    private final JdbcTemplate jdbcTemplate;
    private static final ReentrantLock LOCK = new ReentrantLock();

    public SpringRedisClientDetailsService(RedisTemplate<String, ClientDetails> redisTemplate, DataSource dataSource) {
        Assert.notNull(redisTemplate, "RedisTemplate required");
        Assert.notNull(dataSource, "DataSource required");
        this.redisTemplate = redisTemplate;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        ClientDetails details = redisTemplate.opsForValue().get("oauth2:client:" + clientId);
        if (Objects.nonNull(details)) {
            return details;
        }

        LOCK.lock();
        try {
            try {
                details = jdbcTemplate.queryForObject(selectClientDetailsSql, new SpringRedisClientDetailsService.ClientDetailsRowMapper(), clientId);
            } catch (EmptyResultDataAccessException e) {
                throw new NoSuchClientException("No client with requested id: " + clientId);
            }
            assert details != null;
            redisTemplate.opsForValue().set("oauth2:client:" + clientId, details);
            return details;
        } finally {
            LOCK.unlock();
        }
    }

    private static class ClientDetailsRowMapper implements RowMapper<ClientDetails> {
        private final SpringRedisClientDetailsService.JsonMapper mapper = createJsonMapper();

        public ClientDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
            BaseClientDetails details = new BaseClientDetails(rs.getString(1), rs.getString(3), rs.getString(4),
                    rs.getString(5), rs.getString(7), rs.getString(6));
            details.setClientSecret(rs.getString(2));
            if (rs.getObject(8) != null) {
                details.setAccessTokenValiditySeconds(rs.getInt(8));
            }
            if (rs.getObject(9) != null) {
                details.setRefreshTokenValiditySeconds(rs.getInt(9));
            }
            String json = rs.getString(10);
            if (json != null) {
                try {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> additionalInformation = mapper.read(json, Map.class);
                    details.setAdditionalInformation(additionalInformation);
                } catch (Exception e) {
                    log.warn("Could not decode JSON for additional information: " + details, e);
                }
            }
            String scopes = rs.getString(11);
            if (scopes != null) {
                details.setAutoApproveScopes(StringUtils.commaDelimitedListToSet(scopes));
            }
            return details;
        }
    }

    interface JsonMapper {
        String write(Object input) throws Exception;

        <T> T read(String input, Class<T> type) throws Exception;
    }

    private static SpringRedisClientDetailsService.JsonMapper createJsonMapper() {
        if (ClassUtils.isPresent("org.codehaus.jackson.map.ObjectMapper", null)) {
            return new SpringRedisClientDetailsService.JacksonMapper();
        } else if (ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper", null)) {
            return new SpringRedisClientDetailsService.Jackson2Mapper();
        }
        return new SpringRedisClientDetailsService.NotSupportedJsonMapper();
    }

    private static class JacksonMapper implements SpringRedisClientDetailsService.JsonMapper {
        private final org.codehaus.jackson.map.ObjectMapper mapper = new org.codehaus.jackson.map.ObjectMapper();

        @Override
        public String write(Object input) throws Exception {
            return mapper.writeValueAsString(input);
        }

        @Override
        public <T> T read(String input, Class<T> type) throws Exception {
            return mapper.readValue(input, type);
        }
    }

    private static class Jackson2Mapper implements SpringRedisClientDetailsService.JsonMapper {
        private final com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

        @Override
        public String write(Object input) throws Exception {
            return mapper.writeValueAsString(input);
        }

        @Override
        public <T> T read(String input, Class<T> type) throws Exception {
            return mapper.readValue(input, type);
        }
    }

    private static class NotSupportedJsonMapper implements SpringRedisClientDetailsService.JsonMapper {
        @Override
        public String write(Object input) {
            throw new UnsupportedOperationException(
                    "Neither Jackson 1 nor 2 is available so JSON conversion cannot be done");
        }

        @Override
        public <T> T read(String input, Class<T> type) {
            throw new UnsupportedOperationException(
                    "Neither Jackson 1 nor 2 is available so JSON conversion cannot be done");
        }
    }
}
