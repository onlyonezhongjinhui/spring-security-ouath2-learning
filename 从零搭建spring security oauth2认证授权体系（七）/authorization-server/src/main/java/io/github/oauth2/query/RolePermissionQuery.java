package io.github.oauth2.query;

import lombok.Data;

import java.util.Collection;

/**
 * @author onlyonezhongjinhui
 */
@Data
public class RolePermissionQuery {

    private Collection<String> roleIds;

}