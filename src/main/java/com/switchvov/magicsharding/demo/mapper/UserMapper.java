package com.switchvov.magicsharding.demo.mapper;

import com.switchvov.magicsharding.demo.model.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * Mapper for user.
 *
 * @author switch
 * @since 2024/8/17
 */
@Mapper
public interface UserMapper {

    @Insert("insert into user (id, name, age) values (#{id}, #{name}, #{age})")
    int insert(User user);

    @Select("select * from user where id = #{id}")
    User findById(int id);

    @Update("update user set name = #{name}, age = #{age} where id = #{id}")
    int update(User user);

    @Delete("delete from user where id = #{id}")
    int delete(int id);
}
