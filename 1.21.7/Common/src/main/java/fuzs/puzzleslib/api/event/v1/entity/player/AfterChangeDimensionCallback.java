package fuzs.puzzleslib.api.event.v1.entity.player;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

@FunctionalInterface
public interface AfterChangeDimensionCallback {
    EventInvoker<AfterChangeDimensionCallback> EVENT = EventInvoker.lookup(AfterChangeDimensionCallback.class);

    /**
     * Called after a player has been moved to different level.
     *
     * @param serverPlayer  the player
     * @param originalLevel the original level the player was in
     * @param newLevel      the new level the player was moved to
     */
    void onAfterChangeDimension(ServerPlayer serverPlayer, ServerLevel originalLevel, ServerLevel newLevel);
}
