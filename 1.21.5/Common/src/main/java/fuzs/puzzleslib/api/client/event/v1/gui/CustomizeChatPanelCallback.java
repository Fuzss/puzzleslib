package fuzs.puzzleslib.api.client.event.v1.gui;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.data.MutableInt;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

@FunctionalInterface
public interface CustomizeChatPanelCallback {
    EventInvoker<CustomizeChatPanelCallback> EVENT = EventInvoker.lookup(CustomizeChatPanelCallback.class);

    /**
     * Called before the chat panel is drawn, allows for changing x- and y-position.
     *
     * @param guiGraphics  the gui graphics component
     * @param deltaTracker the delta tracker
     * @param posX         the x-position of the chat panel overlay from the screen left
     * @param posY         the y-position of the chat panel overlay from the screen top
     */
    void onRenderChatPanel(GuiGraphics guiGraphics, DeltaTracker deltaTracker, MutableInt posX, MutableInt posY);
}
