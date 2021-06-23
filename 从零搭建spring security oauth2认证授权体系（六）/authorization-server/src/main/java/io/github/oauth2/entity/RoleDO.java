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
public class RoleDO {

    private String id;

    private String code;

    private String name;

}