package fuzs.puzzleslib.init;

import fuzs.puzzleslib.mixin.accessor.BooleanValueAccessor;
import fuzs.puzzleslib.mixin.accessor.IntegerValueAccessor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameRules;

import java.util.function.BiConsumer;

/**
 * implementation on Forge, we need to use custom accessors as Forge has nothing to help us unfortunately
 */
public class ForgeGameRuleFactory implements CommonGameRuleFactory {

    @Override
    public <T extends GameRules.Value<T>> GameRules.Key<T> register(String name, GameRules.Category category, GameRules.Type<T> type) {
        return GameRules.register(name, category, type);
    }

    @Override
    public GameRules.Type<GameRules.BooleanValue> createBooleanRule(boolean defaultValue) {
        return BooleanValueAccessor.callCreate(defaultValue, (minecraftServer, booleanValue) -> {});
    }

    @Override
    public GameRules.Type<GameRules.BooleanValue> createBooleanRule(boolean defaultValue, BiConsumer<MinecraftServer, GameRules.BooleanValue> changedCallback) {
        return BooleanValueAccessor.callCreate(defaultValue, changedCallback);
    }

    @Override
    public GameRules.Type<GameRules.IntegerValue> createIntRule(int defaultValue) {
        return IntegerValueAccessor.callCreate(defaultValue, (minecraftServer, integerValue) -> {});
    }

    @Override
    public GameRules.Type<GameRules.IntegerValue> createIntRule(int defaultValue, BiConsumer<MinecraftServer, GameRules.IntegerValue> changedCallback) {
        return IntegerValueAccessor.callCreate(defaultValue, changedCallback);
    }
}
