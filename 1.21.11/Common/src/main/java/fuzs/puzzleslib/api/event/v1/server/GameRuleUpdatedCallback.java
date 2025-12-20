package fuzs.puzzleslib.api.event.v1.server;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.gamerules.GameRule;

import java.util.Objects;

@FunctionalInterface
public interface GameRuleUpdatedCallback<T> {

    @SuppressWarnings("unchecked")
    static <T> EventInvoker<GameRuleUpdatedCallback<T>> gameRuleUpdated(GameRule<T> gameRule) {
        Objects.requireNonNull(gameRule, "game rule is null");
        return (EventInvoker<GameRuleUpdatedCallback<T>>) (EventInvoker<?>) EventInvoker.lookup(GameRuleUpdatedCallback.class,
                gameRule);
    }

    /**
     * Called at the end of {@link MinecraftServer#onGameRuleChanged(GameRule, Object)}, and allows for custom handling
     * of updated game rule values.
     *
     * @param minecraftServer  the server
     * @param gameRule         the game rule
     * @param newGameRuleValue the new game rule value
     */
    void onGameRuleUpdated(MinecraftServer minecraftServer, GameRule<T> gameRule, T newGameRuleValue);
}
