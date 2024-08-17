package com.switchvov.magicsharding.demo;

import com.switchvov.magicsharding.config.MagicShardingAutoConfiguration;
import com.switchvov.magicsharding.demo.mapper.OrderMapper;
import com.switchvov.magicsharding.demo.mapper.UserMapper;
import com.switchvov.magicsharding.demo.model.Order;
import com.switchvov.magicsharding.demo.model.User;
import com.switchvov.magicsharding.mybatis.ShardingMapperFactoryBean;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(MagicShardingAutoConfiguration.class)
@MapperScan(value = "com.switchvov.magicsharding.demo.mapper", factoryBean = ShardingMapperFactoryBean.class)
@Slf4j
public class MagicShardingApplication {

    public static void main(String[] args) {
        SpringApplication.run(MagicShardingApplication.class, args);
    }

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Bean
    public ApplicationRunner applicationRunner() {
        return x -> {

            log.info(" ===============>  ===============>  ===============>");
            log.info(" ===============> test user sharding ===============>");
            log.info(" ===============>  ===============>  ===============>");
            for (int id = 1; id <= 60; id++) {
                testUser(id);
            }

            log.info("\n\n\n\n");
            log.info(" ===============>  ===============>   ===============>");
            log.info(" ===============> test order sharding ===============>");
            log.info(" ===============>  ===============>   ===============>");
            for (int id = 1; id <= 40; id++) {
                testOrder(id);
            }

        };
    }

    private void testUser(int id) {
        log.info("\n\n ===============> id = {}", id);
        log.info(" ===> 1. test insert ...");
        int inserted = userMapper.insert(new User(id, "switch", 20));
        log.info(" ===> inserted = {}", inserted);

        log.info(" ===> 2. test find ...");
        User user = userMapper.findById(id);
        log.info(" ===> find = {}", user);

        log.info(" ===> 3. test update ...");
        user.setName("SS");
        int updated = userMapper.update(user);
        log.info(" ===> updated = {}", updated);

        log.info(" ===> 4. test new find ...");
        User user2 = userMapper.findById(id);
        log.info(" ===> find = {}", user2);

        log.info(" ===> 5. test delete ...");
        int deleted = userMapper.delete(id);
        log.info(" ===> deleted = {}", deleted);
    }

    private void testOrder(int id) {
        int id2 = id + 100;
        log.info("\n\n ===============> id = " + id);
        log.info(" ===> 1. test insert ...");
        int inserted = orderMapper.insert(new Order(id, 1, 10d));
        log.info(" ===> inserted = {}", inserted);
        inserted = orderMapper.insert(new Order(id2, 2, 20d));
        log.info(" ===> inserted = {}", inserted);

        log.info(" ===> 2. test find ...");
        Order order1 = orderMapper.findById(id, 1);
        log.info(" ===> find = {}", order1);
        Order order2 = orderMapper.findById(id2, 2);
        log.info(" ===> find = {}", order2);

        log.info(" ===> 3. test update ...");
        order1.setPrice(11d);
        int updated = orderMapper.update(order1);
        log.info(" ===> updated = {}", updated);
        order2.setPrice(22d);
        updated = orderMapper.update(order2);
        log.info(" ===> updated = {}", updated);

        log.info(" ===> 4. test new find ...");
        Order order11 = orderMapper.findById(id, 1);
        log.info(" ===> find = {}", order11);
        Order order22 = orderMapper.findById(id2, 2);
        log.info(" ===> find = {}", order22);

        log.info(" ===> 5. test delete ...");
        int deleted = orderMapper.delete(id, 1);
        log.info(" ===> deleted = {}", deleted);
        deleted = orderMapper.delete(id2, 2);
        log.info(" ===> deleted = {}", deleted);
    }

}
