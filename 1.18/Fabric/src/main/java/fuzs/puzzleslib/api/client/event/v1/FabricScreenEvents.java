package fuzs.puzzleslib.api.client.event.v1;

import fuzs.puzzleslib.api.event.v1.core.FabricEventFactory;
import net.fabricmc.fabric.api.event.Event;

/**
 * Events originally found on Forge in the <code>net.minecraftforge.client.event.ScreenEvent</code> package.
 */
public final class FabricScreenEvents {
    /**
     * Called for instance of {@link net.minecraft.client.gui.screens.inventory.AbstractContainerScreen}, after the screen background is drawn (like menu texture).
     */
    public static final Event<ContainerScreenEvents.Background> CONTAINER_SCREEN_BACKGROUND = FabricEventFactory.create(ContainerScreenEvents.Background.class);
    /**
     * Called for instance of {@link net.minecraft.client.gui.screens.inventory.AbstractContainerScreen}, after the screen foreground is drawn (like text labels).
     */
    public static final Event<ContainerScreenEvents.Foreground> CONTAINER_SCREEN_FOREGROUND = FabricEventFactory.create(ContainerScreenEvents.Foreground.class);
    /**
     * Called before mob effects are drawn next to the inventory menu, used to force a rendering mode, or to cancel the rendering completely.
     */
    public static final Event<InventoryMobEffectsCallback> INVENTORY_MOB_EFFECTS = FabricEventFactory.createResult(InventoryMobEffectsCallback.class);
    /**
     * Called just before a new screen is set to {@link net.minecraft.client.Minecraft#screen} in {@link net.minecraft.client.Minecraft#setScreen},
     * allows for exchanging the new screen with a different one, or can prevent a new screen from opening, effectively forcing the old screen to remain.
     */
    public static final Event<ScreenOpeningCallback> SCREEN_OPENING = FabricEventFactory.createResult(ScreenOpeningCallback.class);
    /**
     * Called just before a tooltip is drawn on a screen, allows for preventing the tooltip from drawing.
     */
    public static final Event<ScreenTooltipEvents.Render> RENDER_TOOLTIP = FabricEventFactory.createResult(ScreenTooltipEvents.Render.class);
}
