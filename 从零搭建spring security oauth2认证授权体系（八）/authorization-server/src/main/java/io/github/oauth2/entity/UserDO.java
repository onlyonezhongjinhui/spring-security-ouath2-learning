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
public class UserDO {

    private String id;

    private String username;

    private String password;

    private String phone;

    private boolean locked;

    private boolean enabled;

    private String openId;

}