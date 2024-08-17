package com.switchvov.magicsharding.strategy;

import groovy.lang.Closure;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * hash sharding strategy.
 *
 * @author switch
 * @since 2024/8/17
 */
public class HashMagicShardingStrategy implements MagicShardingStrategy {
    private final String shardingColumn;
    private final String algorithmExpression;

    public HashMagicShardingStrategy(Properties properties) {
        this.shardingColumn = properties.getProperty("shardingColumn");
        this.algorithmExpression = properties.getProperty("algorithmExpression");
    }


    @Override
    public String doSharding(List<String> availableTargetNames, String logicTableName, Map<String, Object> shardingParams) {
        String expression = InlineExpressionParser.handlePlaceHolder(algorithmExpression);
        InlineExpressionParser parser = new InlineExpressionParser(expression);
        Closure<?> closure = parser.evaluateClosure();
        closure.setProperty(shardingColumn, shardingParams.get(shardingColumn));
        return closure.call().toString();
    }
}
