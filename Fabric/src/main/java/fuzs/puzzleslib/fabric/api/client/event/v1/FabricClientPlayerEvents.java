package fuzs.puzzleslib.fabric.api.client.event.v1;

import fuzs.puzzleslib.api.client.event.v1.entity.player.*;
import fuzs.puzzleslib.fabric.api.event.v1.core.FabricEventFactory;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;

public final class FabricClientPlayerEvents {
    /**
     * Called when computing the field of view modifier on the client, mostly depending on
     * {@link Attributes#MOVEMENT_SPEED}, but also changes for certain actions such as when drawing a bow.
     */
    public static final Event<ComputeFovModifierCallback> COMPUTE_FOV_MODIFIER = FabricEventFactory.create(
            ComputeFovModifierCallback.class);
    /**
     * Called when a player joins a server, the player is already initialized.
     */
    public static final Event<ClientPlayerNetworkEvents.Join> PLAYER_JOIN = FabricEventFactory.create(
            ClientPlayerNetworkEvents.Join.class);
    /**
     * Called when a player disconnects from the server, but also occurs before joining a new single player level or
     * server.
     */
    public static final Event<ClientPlayerNetworkEvents.Leave> PLAYER_LEAVE = FabricEventFactory.create(
            ClientPlayerNetworkEvents.Leave.class);
    /**
     * Called when the local player is replaced from respawning.
     */
    public static final Event<ClientPlayerCopyCallback> PLAYER_COPY = FabricEventFactory.create(ClientPlayerCopyCallback.class);
    /**
     * Called after the {@link ClientInput} has ticked for the {@link LocalPlayer}.
     */
    public static final Event<MovementInputUpdateCallback> MOVEMENT_INPUT_UPDATE = FabricEventFactory.create(
            MovementInputUpdateCallback.class);
    /**
     * Runs before the client performs a pick interaction based on {@link Minecraft#hitResult}, setting a new item stack
     * to the main hand if possible.
     */
    public static final Event<InteractionInputEvents.Pick> PICK_INTERACTION_INPUT = FabricEventFactory.createResult(
            InteractionInputEvents.Pick.class);

    private FabricClientPlayerEvents() {
        // NO-OP
    }
}
