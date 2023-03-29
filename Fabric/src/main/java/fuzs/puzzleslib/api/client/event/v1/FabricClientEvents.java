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
}
