package io.github.oauth2.mapper;

import io.github.oauth2.entity.RolePermissionDO;
import io.github.oauth2.query.RolePermissionQuery;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author onlyonezhongjinhui
 */
@Mapper
public interface RolePermissionMapper {

    List<RolePermissionDO> selectByQuery(RolePermissionQuery query);

}