package fuzs.puzzleslib.impl.registration;

import fuzs.puzzleslib.api.registration.v2.GameRulesFactory;
import fuzs.puzzleslib.mixin.accessor.BooleanValueForgeAccessor;
import fuzs.puzzleslib.mixin.accessor.IntegerValueForgeAccessor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameRules;

import java.util.function.BiConsumer;

/**
 * implementation on Forge, we need to use custom accessors as Forge has nothing to help us unfortunately
 */
public final class ForgeGameRulesFactory implements GameRulesFactory {

    @Override
    public <T extends GameRules.Value<T>> GameRules.Key<T> register(String name, GameRules.Category category, GameRules.Type<T> type) {
        return GameRules.register(name, category, type);
    }

    @Override
    public GameRules.Type<GameRules.BooleanValue> createBooleanRule(boolean defaultValue) {
        return BooleanValueForgeAccessor.puzzleslib$callCreate(defaultValue, (minecraftServer, booleanValue) -> {});
    }

    @Override
    public GameRules.Type<GameRules.BooleanValue> createBooleanRule(boolean defaultValue, BiConsumer<MinecraftServer, GameRules.BooleanValue> changedCallback) {
        return BooleanValueForgeAccessor.puzzleslib$callCreate(defaultValue, changedCallback);
    }

    @Override
    public GameRules.Type<GameRules.IntegerValue> createIntRule(int defaultValue) {
        return IntegerValueForgeAccessor.puzzleslib$callCreate(defaultValue, (minecraftServer, integerValue) -> {});
    }

    @Override
    public GameRules.Type<GameRules.IntegerValue> createIntRule(int defaultValue, BiConsumer<MinecraftServer, GameRules.IntegerValue> changedCallback) {
        return IntegerValueForgeAccessor.puzzleslib$callCreate(defaultValue, changedCallback);
    }
}
