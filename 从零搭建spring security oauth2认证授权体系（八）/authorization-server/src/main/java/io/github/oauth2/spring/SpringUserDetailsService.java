package io.github.oauth2.spring;

import io.github.oauth2.entity.RolePermissionDO;
import io.github.oauth2.entity.UserDO;
import io.github.oauth2.entity.UserRoleDO;
import io.github.oauth2.mapper.PermissionMapper;
import io.github.oauth2.mapper.RolePermissionMapper;
import io.github.oauth2.mapper.UserMapper;
import io.github.oauth2.mapper.UserRoleMapper;
import io.github.oauth2.query.RolePermissionQuery;
import io.github.oauth2.query.UserRoleQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpringUserDetailsService implements UserDetailsService {
    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final RolePermissionMapper rolePermissionMapper;
    private final PermissionMapper permissionMapper;
    private final RedisTemplate<String, SpringUserDetails> redisTemplate;
    private static final ReentrantLock reentrantLock = new ReentrantLock();

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SpringUserDetails userDetails = loadUserByUsernameFromRedis(username);
        if (Objects.nonNull(userDetails)) {
            return userDetails;
        }

        reentrantLock.lock();
        try {
            userDetails = loadUserByUsernameFromRedis(username);
            if (Objects.nonNull(userDetails)) {
                return userDetails;
            }

            UserDO userDO = userMapper.selectByUsername(username);
            if (Objects.isNull(userDO)) {
                throw new UsernameNotFoundException("???????????????");
            }

            Set<SimpleGrantedAuthority> authorities = new HashSet<>();

            UserRoleQuery userRoleQuery = new UserRoleQuery();
            userRoleQuery.setUserId(userDO.getId());
            Set<String> roleIds = userRoleMapper.selectByQuery(userRoleQuery).stream().map(UserRoleDO::getRoleId).collect(Collectors.toSet());

            if (!roleIds.isEmpty()) {
                RolePermissionQuery rolePermissionQuery = new RolePermissionQuery();
                rolePermissionQuery.setRoleIds(roleIds);
                Set<String> permissionIds = rolePermissionMapper.selectByQuery(rolePermissionQuery).stream().map(RolePermissionDO::getPermissionId).collect(Collectors.toSet());

                authorities = permissionMapper.selectBatchIds(permissionIds).stream().map(e -> new SimpleGrantedAuthority(e.getCode())).collect(Collectors.toSet());
            }

            userDetails = new SpringUserDetails(userDO.getUsername(), userDO.getPassword(), userDO.isEnabled(),
                    true, true, !userDO.isLocked(), authorities, userDO.getPhone(), userDO.getOpenId(), userDO.getId());

            redisTemplate.opsForValue().set(redisKey(username), userDetails);

            return userDetails;
        } finally {
            reentrantLock.unlock();
        }
    }

    private SpringUserDetails loadUserByUsernameFromRedis(final String username) {
        return redisTemplate.opsForValue().get(redisKey(username));
    }

    private String redisKey(final String username) {
        return "oauth2:user_details:" + username;
    }

}
