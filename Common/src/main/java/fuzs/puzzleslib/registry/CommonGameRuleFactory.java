package fuzs.puzzleslib.registry;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameRules;

import java.util.function.BiConsumer;

/**
 * helper class for registering custom game rules
 *
 * this is adapted from Fabric, since Forge unfortunately doesn't have anything for dealing with game rules
 * Fabric has many custom options for game rules (e.g. double and enum rules, also bounded int rules),
 * but since they don't exist in vanilla we don't use them to avoid having to implement on Forge ourselves
 *
 * name includes common as it would otherwise be the same as in Fabric Api
 */
public interface CommonGameRuleFactory {

    /**
     * register a new game rule
     *
     * @param name      name of the rule
     * @param category  category for the game rules screen
     * @param type      game rule type from one of the factory methods
     * @param <T>       rule value type (boolean or int)
     * @return          key for the game rule
     */
    <T extends GameRules.Value<T>> GameRules.Key<T> register(String name, GameRules.Category category, GameRules.Type<T> type);

    /**
     * create a boolean game rule with a default value
     *
     * @param defaultValue  default value
     * @return              the game rule
     */
    GameRules.Type<GameRules.BooleanValue> createBooleanRule(boolean defaultValue);

    /**
     * create a boolean game rule with a default value, do something when the value is changed
     *
     * @param defaultValue      default value
     * @param changedCallback   access to the server, useful for notifying clients of the change
     * @return                  the game rule
     */
    GameRules.Type<GameRules.BooleanValue> createBooleanRule(boolean defaultValue, BiConsumer<MinecraftServer, GameRules.BooleanValue> changedCallback);

    /**
     * create an int game rule with a default value
     *
     * @param defaultValue  default value
     * @return              the game rule
     */
    GameRules.Type<GameRules.IntegerValue> createIntRule(int defaultValue);

    /**
     * create an int game rule with a default value, do something when the value is changed
     *
     * @param defaultValue      default value
     * @param changedCallback   access to the server, useful for notifying clients of the change
     * @return                  the game rule
     */
    GameRules.Type<GameRules.IntegerValue> createIntRule(int defaultValue, BiConsumer<MinecraftServer, GameRules.IntegerValue> changedCallback);
}
