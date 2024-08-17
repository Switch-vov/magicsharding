package com.switchvov.magicsharding.datasource;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.switchvov.magicsharding.config.MagicShardingProperties;
import com.switchvov.magicsharding.engine.MagicShardingContext;
import com.switchvov.magicsharding.engine.MagicShardingResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * sharding datasource.
 *
 * @author switch
 * @since 2024/8/17
 */
@Slf4j
public class MagicShardingDataSource extends AbstractRoutingDataSource {
    public MagicShardingDataSource(MagicShardingProperties properties) {
        Map<Object, Object> dataSourceMap = new LinkedHashMap<>();
        properties.getDatasources().forEach((k, v) -> {
            try {
                dataSourceMap.put(k, DruidDataSourceFactory.createDataSource(v));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        setTargetDataSources(dataSourceMap);
        setDefaultTargetDataSource(dataSourceMap.values().iterator().next());
    }

    @Override
    protected Object determineCurrentLookupKey() {
        MagicShardingResult result = MagicShardingContext.get();
        String key = Objects.isNull(result) ? null : result.getTargetDataSourceName();
        log.debug(" ===>[MagicSharding] determineCurrentLookupKey = {}", key);
        return key;
    }
}
