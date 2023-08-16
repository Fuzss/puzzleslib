package fuzs.puzzleslib.impl.init;

import fuzs.puzzleslib.api.init.v2.GameRulesFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameRules;

import java.util.function.BiConsumer;

public final class FabricGameRulesFactory implements GameRulesFactory {

    @Override
    public <T extends GameRules.Value<T>> GameRules.Key<T> register(String name, GameRules.Category category, GameRules.Type<T> type) {
        return GameRuleRegistry.register(name, category, type);
    }

    @Override
    public GameRules.Type<GameRules.BooleanValue> createBooleanRule(boolean defaultValue, BiConsumer<MinecraftServer, GameRules.BooleanValue> callback) {
        return GameRuleFactory.createBooleanRule(defaultValue, callback);
    }

    @Override
    public GameRules.Type<GameRules.IntegerValue> createIntRule(int defaultValue, int minimumValue, int maximumValue, BiConsumer<MinecraftServer, GameRules.IntegerValue> callback) {
        return GameRuleFactory.createIntRule(defaultValue, minimumValue, maximumValue, callback);
    }
}
