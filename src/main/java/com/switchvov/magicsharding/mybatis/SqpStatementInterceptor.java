package com.switchvov.magicsharding.mybatis;

import com.switchvov.magicsharding.engine.MagicShardingContext;
import com.switchvov.magicsharding.engine.MagicShardingResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.springframework.objenesis.instantiator.util.UnsafeUtils;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.Objects;

/**
 * intercept sql.
 *
 * @author switch
 * @since 2024/8/17
 */
@Slf4j
@Intercepts(@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class}))
public class SqpStatementInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MagicShardingResult result = MagicShardingContext.get();
        if (Objects.nonNull(result)) {
            StatementHandler handler = (StatementHandler) invocation.getTarget();
            BoundSql boundSql = handler.getBoundSql();
            String sql = boundSql.getSql();
            log.debug(" ===>[MagicSharding] SqpStatementInterceptor:{}", sql);
            String targetSqlStatement = result.getTargetSqlStatement();
            if (!sql.equalsIgnoreCase(targetSqlStatement)) {
                replaceSql(boundSql, targetSqlStatement);
            }
        }
        return invocation.proceed();
    }

    private static void replaceSql(BoundSql boundSql, String sql) throws NoSuchFieldException {
        Field field = boundSql.getClass().getDeclaredField("sql");
        Unsafe unsafe = UnsafeUtils.getUnsafe();
        long fieldOffset = unsafe.objectFieldOffset(field);
        unsafe.putObject(boundSql, fieldOffset, sql);
    }
}
