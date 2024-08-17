package com.switchvov.magicsharding.engine;

/**
 * core sharding engine.
 *
 * @author switch
 * @since 2024/8/17
 */
public interface MagicShardingEngine {
    MagicShardingResult sharding(String sql, Object[] args);
}
