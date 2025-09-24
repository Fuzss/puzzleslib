package fuzs.puzzleslib.fabric.api.client.event.v1;

import fuzs.puzzleslib.fabric.impl.client.event.ExtraScreenExtensions;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

import java.util.Objects;

@Deprecated
@FunctionalInterface
public interface AfterBackgroundCallback {

    static Event<AfterBackgroundCallback> afterBackground(Screen screen) {
        Objects.requireNonNull(screen, "screen is null");
        return ((ExtraScreenExtensions) screen).puzzleslib$getAfterBackgroundEvent();
    }

    /**
     * Runs after a screen background is rendered in {@link Screen#renderBackground(GuiGraphics, int, int, float)}.
     *
     * @param screen      the currently displayed screen
     * @param guiGraphics the gui graphics component
     * @param mouseX      the x-position of the mouse
     * @param mouseY      the y-position of the mouse
     * @param partialTick the partial tick time
     */
    void onAfterBackground(Screen screen, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick);
}
