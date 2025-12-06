package fuzs.puzzleslib.api.init.v3.override;

import net.minecraft.world.level.GameRules;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Allows for setting new default values to game rules in a development environment.
 * <p>Will not apply any changes in a production environment.
 */
@Deprecated
public final class GameRuleValueOverrides {

    private GameRuleValueOverrides() {

    }

    /**
     * Set a default value for a {@link net.minecraft.world.level.GameRules.BooleanValue}.
     *
     * @param key   game rule key
     * @param value new default value
     */
    public static void setValue(GameRules.Key<GameRules.BooleanValue> key, boolean value) {
        setValue(key, (GameRules.BooleanValue booleanValue) -> {
            booleanValue.set(value, null);
        });
    }

    /**
     * Set a default value for an {@link net.minecraft.world.level.GameRules.IntegerValue}.
     *
     * @param key   game rule key
     * @param value new default value
     */
    public static void setValue(GameRules.Key<GameRules.IntegerValue> key, int value) {
        setValue(key, (GameRules.IntegerValue integerValue) -> {
            integerValue.set(value, null);
        });
    }

    /**
     * Get a game rules by the corresponding key and adjust the game rule value constructor to immediately set a new
     * value after initialization.
     *
     * @param key         game rule key
     * @param valueSetter implementation for setting the value
     * @param <T>         game rule value type
     */
    public static <T extends GameRules.Value<T>> void setValue(GameRules.Key<T> key, Consumer<T> valueSetter) {
        GameRules.Type<T> type = (GameRules.Type<T>) GameRules.GAME_RULE_TYPES.get(key);
        Function<GameRules.Type<T>, T> originalConstructor = type.constructor;
        type.constructor = (GameRules.Type<T> factoryType) -> {
            T ruleValue = originalConstructor.apply(factoryType);
            valueSetter.accept(ruleValue);
            return ruleValue;
        };
    }
}
