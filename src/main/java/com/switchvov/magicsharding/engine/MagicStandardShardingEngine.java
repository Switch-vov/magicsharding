package com.switchvov.magicsharding.engine;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.switchvov.magicsharding.config.MagicShardingProperties;
import com.switchvov.magicsharding.strategy.HashMagicShardingStrategy;
import com.switchvov.magicsharding.strategy.MagicShardingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Standard sharding engine.
 *
 * @author switch
 * @since 2024/8/17
 */
@Slf4j
public class MagicStandardShardingEngine implements MagicShardingEngine {
    private final MultiValueMap<String, String> actualDatabaseNames = new LinkedMultiValueMap<>();
    private final MultiValueMap<String, String> actualTableNames = new LinkedMultiValueMap<>();
    private final Map<String, MagicShardingStrategy> databaseStrategies = new HashMap<>();
    private final Map<String, MagicShardingStrategy> tableStrategies = new HashMap<>();

    public MagicStandardShardingEngine(MagicShardingProperties properties) {
        properties.getTables().forEach((table, tableProperties) -> {
            tableProperties.getActualDataNodes().forEach(actualDataNode -> {
                String[] split = actualDataNode.split("\\.");
                String databaseName = split[0], tableName = split[1];
                actualDatabaseNames.add(databaseName, tableName);
                actualTableNames.add(tableName, databaseName);
            });
            databaseStrategies.put(table, new HashMagicShardingStrategy(tableProperties.getDatabaseStrategy()));
            tableStrategies.put(table, new HashMagicShardingStrategy(tableProperties.getTableStrategy()));
        });
    }

    @Override
    public MagicShardingResult sharding(String sql, Object[] args) {

        SQLStatement sqlStatement = SQLUtils.parseSingleMysqlStatement(sql);
        String table;
        Map<String, Object> shardingColumnsMap;

        // insert
        if (sqlStatement instanceof SQLInsertStatement sqlInsertStatement) {
            table = sqlInsertStatement.getTableName().getSimpleName();
            shardingColumnsMap = new HashMap<>();
            List<SQLExpr> columns = sqlInsertStatement.getColumns();
            for (int i = 0; i < columns.size(); i++) {
                SQLExpr column = columns.get(i);
                SQLIdentifierExpr columnExpr = (SQLIdentifierExpr) column;
                String columnName = columnExpr.getSimpleName();
                shardingColumnsMap.put(columnName, args[i]);
            }
        } else {
            // select/update/delete
            MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
            visitor.setParameters(List.of(args));
            sqlStatement.accept(visitor);

            LinkedHashSet<SQLName> sqlNames = new LinkedHashSet<>(visitor.getOriginalTables());
            if (sqlNames.size() > 1) {
                throw new RuntimeException("not support multi table sharding: " + sqlNames);
            }
            table = sqlNames.iterator().next().getSimpleName();
            log.debug(" ===>[MagicSharding] visitor.getOriginalTable = {}", table);
            shardingColumnsMap = visitor.getConditions().stream()
                    .collect(Collectors.toMap(c -> c.getColumn().getName(), c -> c.getValues().get(0)));
            log.debug(" ===>[MagicSharding] visitor.getConditions = {}", visitor.getConditions());
        }

        MagicShardingStrategy databaseStrategy = databaseStrategies.get(table);
        String targetDatabase = databaseStrategy.doSharding(actualDatabaseNames.get(table), table, shardingColumnsMap);
        MagicShardingStrategy tableStrategy = tableStrategies.get(table);
        String targetTable = tableStrategy.doSharding(actualTableNames.get(table), table, shardingColumnsMap);
        log.debug(" ===>>>[MagicSharding] sharding start<<<=== ");
        log.debug(" ===>>>[MagicSharding] sharding target db.table = {}.{}", targetDatabase, targetTable);
        log.debug(" ===>>>[MagicSharding] sharding end<<<=== ");

        return new MagicShardingResult(targetDatabase, sql.replace(table, targetTable));
    }
}
