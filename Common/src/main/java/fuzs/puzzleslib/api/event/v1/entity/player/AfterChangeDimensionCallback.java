package fuzs.puzzleslib.api.event.v1.entity.player;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

@FunctionalInterface
public interface AfterChangeDimensionCallback {
    EventInvoker<AfterChangeDimensionCallback> EVENT = EventInvoker.lookup(AfterChangeDimensionCallback.class);

    /**
     * Called after a player has been moved to different world.
     *
     * @param player the player
     * @param from   the original world the player was in
     * @param to     the new world the player was moved to
     */
    void onAfterChangeDimension(ServerPlayer player, ServerLevel from, ServerLevel to);
}
