package fuzs.puzzleslib.api.event.v1.entity.player;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.server.level.ServerPlayer;

@FunctionalInterface
public interface StopSleepInBedCallback {
    EventInvoker<StopSleepInBedCallback> EVENT = EventInvoker.lookup(StopSleepInBedCallback.class);

    /**
     * Called when the player stops sleeping in a bed in
     * {@link net.minecraft.world.entity.player.Player#stopSleepInBed(boolean, boolean)}.
     *
     * @param serverPlayer         the player
     * @param hasSleptThroughNight if the player has successfully slept until dawn, otherwise sleep has been interrupted
     *                             e.g., from leaving the bed
     */
    void onStopSleepInBed(ServerPlayer serverPlayer, boolean hasSleptThroughNight);
}
