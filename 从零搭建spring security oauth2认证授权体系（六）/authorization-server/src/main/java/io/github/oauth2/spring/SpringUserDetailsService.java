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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpringUserDetailsService implements UserDetailsService {
    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final RolePermissionMapper rolePermissionMapper;
    private final PermissionMapper permissionMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDO userDO = userMapper.selectByUsername(username);
        if (Objects.isNull(userDO)) {
            throw new UsernameNotFoundException("用户不存在");
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

        return new SpringUserDetails(userDO.getUsername(), userDO.getPassword(), userDO.isEnabled(),
                true, true, !userDO.isLocked(), authorities, userDO.getPhone(), userDO.getOpenId(), userDO.getId());
    }

}
