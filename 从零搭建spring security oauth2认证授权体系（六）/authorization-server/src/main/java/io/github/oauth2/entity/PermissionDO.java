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
public class PermissionDO {

    private String id;

    private String parentId;

    private String code;

    private String name;

    /**
     * resource type eg 0:main menu 1:child menu 2:function button
     */
    private int resourceType;

    /**
     * route name
     */
    private String routeName;

    /**
     * route url
     */
    private String routeUrl;

    /**
     * component
     */
    private String component;

    /**
     * icon
     */
    private String icon;

}