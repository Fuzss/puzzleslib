package fuzs.puzzleslib.api.client.event.v1;

import fuzs.puzzleslib.api.event.v1.core.FabricEventFactory;
import net.fabricmc.fabric.api.event.Event;

/**
 * Events originally found on Forge in the <code>net.minecraftforge.client.event</code> package.
 */
public final class FabricClientEvents {
    /**
     * Fires before the name tag of an entity is tried to be rendered, in addition to preventing the name tag from rendering, rendering can also be forced.
     */
    public static final Event<RenderNameTagCallback> RENDER_NAME_TAG = FabricEventFactory.createResult(RenderNameTagCallback.class);
    /**
     * Called after the screen background is drawn (like menu texture).
     */
    public static final Event<ContainerScreenEvents.Background> CONTAINER_SCREEN_BACKGROUND = FabricEventFactory.create(ContainerScreenEvents.Background.class);
    /**
     * Called after the screen foreground is drawn (like text labels).
     */
    public static final Event<ContainerScreenEvents.Foreground> CONTAINER_SCREEN_FOREGROUND = FabricEventFactory.create(ContainerScreenEvents.Foreground.class);
}
