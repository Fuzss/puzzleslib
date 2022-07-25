package fuzs.puzzleslib.registry;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameRules;

import java.util.function.BiConsumer;

/**
 * implementation on Fabric using methods conveniently provided by Fabric Api
 */
public class FabricGameRuleFactory implements CommonGameRuleFactory {

    @Override
    public <T extends GameRules.Value<T>> GameRules.Key<T> register(String name, GameRules.Category category, GameRules.Type<T> type) {
        return GameRuleRegistry.register(name, category, type);
    }

    @Override
    public GameRules.Type<GameRules.BooleanValue> createBooleanRule(boolean defaultValue) {
        return GameRuleFactory.createBooleanRule(defaultValue);
    }

    @Override
    public GameRules.Type<GameRules.BooleanValue> createBooleanRule(boolean defaultValue, BiConsumer<MinecraftServer, GameRules.BooleanValue> changedCallback) {
        return GameRuleFactory.createBooleanRule(defaultValue, changedCallback);
    }

    @Override
    public GameRules.Type<GameRules.IntegerValue> createIntRule(int defaultValue) {
        return GameRuleFactory.createIntRule(defaultValue);
    }

    @Override
    public GameRules.Type<GameRules.IntegerValue> createIntRule(int defaultValue, BiConsumer<MinecraftServer, GameRules.IntegerValue> changedCallback) {
        return GameRuleFactory.createIntRule(defaultValue, changedCallback);
    }
}
