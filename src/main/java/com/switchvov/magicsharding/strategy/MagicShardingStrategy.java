package com.switchvov.magicsharding.strategy;

import java.util.List;
import java.util.Map;

/**
 * strategy for sharding.
 *
 * @author switch
 * @since 2024/8/17
 */
public interface MagicShardingStrategy {
    String doSharding(List<String> availableTargetNames, String logicTableName, Map<String, Object> shardingParams);
}
