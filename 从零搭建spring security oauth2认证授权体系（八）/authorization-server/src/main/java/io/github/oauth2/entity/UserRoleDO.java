package io.github.oauth2.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author onlyonezhongjinhui
 */
@Data
@SuperBuilder
@NoArgsConstructor
public class UserRoleDO {

    private String id;

    private String userId;

    private String roleId;

}