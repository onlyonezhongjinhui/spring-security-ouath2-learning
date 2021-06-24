package io.github.oauth2.mapper;

import io.github.oauth2.entity.UserRoleDO;
import io.github.oauth2.query.UserRoleQuery;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author onlyonezhongjinhui
 */
@Mapper
public interface UserRoleMapper {

    List<UserRoleDO> selectByQuery(UserRoleQuery query);

}