package com.switchvov.magicsharding.engine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * sharding result.
 *
 * @author switch
 * @since 2024/8/17
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MagicShardingResult {
    private String targetDataSourceName;
    private String targetSqlStatement;
}
