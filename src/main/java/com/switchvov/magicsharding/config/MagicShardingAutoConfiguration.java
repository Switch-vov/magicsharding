package com.switchvov.magicsharding.config;

import com.switchvov.magicsharding.datasource.MagicShardingDataSource;
import com.switchvov.magicsharding.engine.MagicShardingEngine;
import com.switchvov.magicsharding.engine.MagicStandardShardingEngine;
import com.switchvov.magicsharding.mybatis.SqpStatementInterceptor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Sharding auto configuration.
 *
 * @author switch
 * @since 2024/8/17
 */
@Configuration
@EnableConfigurationProperties(MagicShardingProperties.class)
public class MagicShardingAutoConfiguration {

    @Bean
    public MagicShardingDataSource shardingDataSource(MagicShardingProperties properties) {
        return new MagicShardingDataSource(properties);
    }

    @Bean
    public MagicShardingEngine shardingEngine(MagicShardingProperties properties) {
        return new MagicStandardShardingEngine(properties);
    }

    @Bean
    public SqpStatementInterceptor sqpStatementInterceptor() {
        return new SqpStatementInterceptor();
    }
}
