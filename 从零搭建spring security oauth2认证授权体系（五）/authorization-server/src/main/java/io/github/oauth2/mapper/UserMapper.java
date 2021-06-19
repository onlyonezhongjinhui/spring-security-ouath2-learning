package io.github.oauth2.mapper;

import io.github.oauth2.entity.UserDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author onlyonezhongjinhui
 */
@Mapper
public interface UserMapper {

    UserDO selectByUsername(@Param("username") String username);

}