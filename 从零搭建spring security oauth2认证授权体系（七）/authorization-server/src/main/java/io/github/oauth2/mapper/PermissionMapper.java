package io.github.oauth2.mapper;

import io.github.oauth2.entity.PermissionDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * @author onlyonezhongjinhui
 */
@Mapper
public interface PermissionMapper {

    List<PermissionDO> selectBatchIds(@Param("ids") Collection<String> ids);

}