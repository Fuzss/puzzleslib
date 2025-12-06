package fuzs.puzzleslib.api.init.v3;

import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

import java.util.function.BiConsumer;

/**
 * Helper class for registering custom game rules.
 * <p>Fabric has many custom options for game rules (mainly double and enum rules), but since they don't exist on Forge
 * we don't support them.
 */
public interface GameRulesFactory {
    /**
     * the instance
     */
    GameRulesFactory INSTANCE = ProxyImpl.get().getGameRulesFactory();

    /**
     * Register a new boolean game rule.
     *
     * @param name         name of the rule, used for the <code>/gamerule</code> command
     * @param category     category for the game rules screen shown during world creation
     * @param defaultValue the default value for the game rule
     * @return key for accessing the game rule via {@link Level#getGameRules()}
     */
    default GameRules.Key<GameRules.BooleanValue> registerBooleanRule(String name, GameRules.Category category, boolean defaultValue) {
        return this.register(name, category, this.createBooleanRule(defaultValue));
    }

    /**
     * Register a new integer game rule.
     *
     * @param name         name of the rule, used for the <code>/gamerule</code> command
     * @param category     category for the game rules screen shown during world creation
     * @param defaultValue the default value for the game rule
     * @return key for accessing the game rule via {@link Level#getGameRules()}
     */
    default GameRules.Key<GameRules.IntegerValue> registerIntRule(String name, GameRules.Category category, int defaultValue) {
        return this.register(name, category, this.createIntRule(defaultValue));
    }

    /**
     * Register a new game rule.
     *
     * @param name     name of the rule, used for the <code>/gamerule</code> command
     * @param category category for the game rules screen shown during world creation
     * @param type     game rule type from one of the factory methods
     * @param <T>      rule value type (boolean or int)
     * @return key for accessing the game rule via {@link Level#getGameRules()}
     */
    <T extends GameRules.Value<T>> GameRules.Key<T> register(String name, GameRules.Category category, GameRules.Type<T> type);

    /**
     * Create a new {@link net.minecraft.world.level.GameRules.BooleanValue}.
     *
     * @param defaultValue the default value for the game rule
     * @return the game rule
     */
    default GameRules.Type<GameRules.BooleanValue> createBooleanRule(boolean defaultValue) {
        return this.createBooleanRule(defaultValue, (MinecraftServer server, GameRules.BooleanValue booleanValue) -> {
            // NO-OP
        });
    }

    /**
     * Create a new {@link net.minecraft.world.level.GameRules.BooleanValue}.
     *
     * @param defaultValue the default value for the game rule
     * @param callback     access to the server, useful for notifying clients of the change
     * @return the game rule
     */
    GameRules.Type<GameRules.BooleanValue> createBooleanRule(boolean defaultValue, BiConsumer<MinecraftServer, GameRules.BooleanValue> callback);

    /**
     * Create a new {@link net.minecraft.world.level.GameRules.IntegerValue}.
     *
     * @param defaultValue the default value for the game rule
     * @return the game rule
     */
    default GameRules.Type<GameRules.IntegerValue> createIntRule(int defaultValue) {
        return this.createIntRule(defaultValue, Integer.MIN_VALUE);
    }

    /**
     * Create a new {@link net.minecraft.world.level.GameRules.IntegerValue}.
     * <p>Note that any bounds are not supported on Forge and are therefore simply ignored when creating the new game
     * rule.
     *
     * @param defaultValue the default value for the game rule
     * @param minimumValue the minimum value for this rule, usually {@link Integer#MIN_VALUE}
     * @return the game rule
     */
    default GameRules.Type<GameRules.IntegerValue> createIntRule(int defaultValue, int minimumValue) {
        return this.createIntRule(defaultValue, minimumValue, Integer.MAX_VALUE);
    }

    /**
     * Create a new {@link net.minecraft.world.level.GameRules.IntegerValue}.
     * <p>Note that any bounds are not supported on Forge and are therefore simply ignored when creating the new game
     * rule.
     *
     * @param defaultValue the default value for the game rule
     * @param minimumValue the minimum value for this rule, usually {@link Integer#MIN_VALUE}
     * @param maximumValue the maximum value for this rule, usually {@link Integer#MAX_VALUE}
     * @return the game rule
     */
    default GameRules.Type<GameRules.IntegerValue> createIntRule(int defaultValue, int minimumValue, int maximumValue) {
        return this.createIntRule(defaultValue,
                minimumValue,
                maximumValue,
                (MinecraftServer server, GameRules.IntegerValue integerValue) -> {
                    // NO-OP
                });
    }

    /**
     * Create a new {@link net.minecraft.world.level.GameRules.IntegerValue}.
     *
     * @param defaultValue the default value for the game rule
     * @param callback     access to the server, useful for notifying clients of the change
     * @return the game rule
     */
    default GameRules.Type<GameRules.IntegerValue> createIntRule(int defaultValue, BiConsumer<MinecraftServer, GameRules.IntegerValue> callback) {
        return this.createIntRule(defaultValue, Integer.MIN_VALUE, callback);
    }

    /**
     * Create a new {@link net.minecraft.world.level.GameRules.IntegerValue}.
     * <p>Note that any bounds are not supported on Forge and are therefore simply ignored when creating the new game
     * rule.
     *
     * @param defaultValue the default value for the game rule
     * @param minimumValue the minimum value for this rule, usually {@link Integer#MIN_VALUE}
     * @param callback     access to the server, useful for notifying clients of the change
     * @return the game rule
     */
    default GameRules.Type<GameRules.IntegerValue> createIntRule(int defaultValue, int minimumValue, BiConsumer<MinecraftServer, GameRules.IntegerValue> callback) {
        return this.createIntRule(defaultValue, minimumValue, Integer.MAX_VALUE, callback);
    }

    /**
     * Create a new {@link net.minecraft.world.level.GameRules.IntegerValue}.
     * <p>Note that any bounds are not supported on Forge and are therefore simply ignored when creating the new game
     * rule.
     *
     * @param defaultValue the default value for the game rule
     * @param minimumValue the minimum value for this rule, usually {@link Integer#MIN_VALUE}
     * @param maximumValue the maximum value for this rule, usually {@link Integer#MAX_VALUE}
     * @param callback     access to the server, useful for notifying clients of the change
     * @return the game rule
     */
    GameRules.Type<GameRules.IntegerValue> createIntRule(int defaultValue, int minimumValue, int maximumValue, BiConsumer<MinecraftServer, GameRules.IntegerValue> callback);
}
