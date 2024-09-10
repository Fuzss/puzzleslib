package fuzs.puzzleslib.api.client.event.v1.entity.player;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;

@FunctionalInterface
public interface MovementInputUpdateCallback {
    EventInvoker<MovementInputUpdateCallback> EVENT = EventInvoker.lookup(MovementInputUpdateCallback.class);

    /**
     * Called after {@link Input#tick(boolean, float)} has run for the {@link LocalPlayer}.
     *
     * @param player the local player instance
     * @param input  the input instance for that player
     */
    void onMovementInputUpdate(LocalPlayer player, Input input);
}
