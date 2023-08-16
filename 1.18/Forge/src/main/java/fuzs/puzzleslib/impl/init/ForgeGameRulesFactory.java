package fuzs.puzzleslib.impl.init;

import fuzs.puzzleslib.api.init.v2.GameRulesFactory;
import fuzs.puzzleslib.mixin.accessor.BooleanValueForgeAccessor;
import fuzs.puzzleslib.mixin.accessor.IntegerValueForgeAccessor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameRules;

import java.util.function.BiConsumer;

public final class ForgeGameRulesFactory implements GameRulesFactory {

    @Override
    public <T extends GameRules.Value<T>> GameRules.Key<T> register(String name, GameRules.Category category, GameRules.Type<T> type) {
        return GameRules.register(name, category, type);
    }

    @Override
    public GameRules.Type<GameRules.BooleanValue> createBooleanRule(boolean defaultValue, BiConsumer<MinecraftServer, GameRules.BooleanValue> callback) {
        return BooleanValueForgeAccessor.puzzleslib$callCreate(defaultValue, callback);
    }

    @Override
    public GameRules.Type<GameRules.IntegerValue> createIntRule(int defaultValue, int minimumValue, int maximumValue, BiConsumer<MinecraftServer, GameRules.IntegerValue> callback) {
        return IntegerValueForgeAccessor.puzzleslib$callCreate(defaultValue, callback);
    }
}
