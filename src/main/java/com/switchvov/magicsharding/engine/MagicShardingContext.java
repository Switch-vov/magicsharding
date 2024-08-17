package com.switchvov.magicsharding.engine;

/**
 * Sharding context.
 *
 * @author switch
 * @since 2024/8/17
 */
public class MagicShardingContext {
    private static final ThreadLocal<MagicShardingResult> LOCAL = new ThreadLocal<>();

    public static MagicShardingResult get() {
        return LOCAL.get();
    }

    public static void set(MagicShardingResult result) {
        LOCAL.set(result);
    }
}
