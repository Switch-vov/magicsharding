package com.switchvov.magicsharding.demo.mapper;

import com.switchvov.magicsharding.demo.model.Order;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * Mapper for order.
 *
 * @author switch
 * @since 2024/8/17
 */
@Mapper
public interface OrderMapper {

    @Insert("insert into t_order (id, uid, price) values (#{id}, #{uid}, #{price})")
    int insert(Order order);

    @Select("select * from t_order where id = #{id} and uid = #{uid}")
    Order findById(int id, int uid);

    @Update("update t_order set price = #{price} where id = #{id} and uid = #{uid}")
    int update(Order order);

    @Delete("delete from t_order where id = #{id} and uid = #{uid}")
    int delete(int id, int uid);
}
