package fuzs.puzzleslib.api.client.event.v1.gui;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.List;

@FunctionalInterface
public interface GatherEffectScreenTooltipCallback {
    EventInvoker<GatherEffectScreenTooltipCallback> EVENT = EventInvoker.lookup(GatherEffectScreenTooltipCallback.class);

    /**
     * Called when tooltip lines are gathered for an effect widget in the player inventory when it is hovered by the
     * cursor.
     * <p>
     * Only runs for small effects widgets.
     *
     * @param screen       the effect rendering inventory screen instance
     * @param mobEffect    the effect instance the tooltip is drawn for
     * @param tooltipLines the current tooltip components
     */
    void onGatherEffectScreenTooltip(AbstractContainerScreen<?> screen, MobEffectInstance mobEffect, List<Component> tooltipLines);
}
