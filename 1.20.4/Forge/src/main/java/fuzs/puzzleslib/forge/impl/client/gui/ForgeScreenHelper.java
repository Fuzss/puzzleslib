package fuzs.puzzleslib.forge.impl.client.gui;

import fuzs.puzzleslib.api.client.gui.v2.screen.ScreenHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * a helper class for accessing encapsulated fields on a screen
 * on Forge those are all exposed, but Fabric requires special accessors
 *
 * <p>for adding buttons, those are handled very different on both mod loaders:
 * on Forge add buttons during init event with appropriate helper methods
 * on Fabric adding is done via custom ButtonList
 */
public final class ForgeScreenHelper implements ScreenHelper {

    @Override
    public Minecraft getMinecraft(Screen screen) {
        Objects.requireNonNull(screen, "screen is null");
        return screen.getMinecraft();
    }

    @Override
    public Font getFont(Screen screen) {
        Objects.requireNonNull(screen, "screen is null");
        return this.getMinecraft(screen).font;
    }

    @Override
    public int getImageWidth(AbstractContainerScreen<?> screen) {
        Objects.requireNonNull(screen, "screen is null");
        return screen.getXSize();
    }

    @Override
    public int getImageHeight(AbstractContainerScreen<?> screen) {
        Objects.requireNonNull(screen, "screen is null");
        return screen.getYSize();
    }

    @Override
    public int getLeftPos(AbstractContainerScreen<?> screen) {
        Objects.requireNonNull(screen, "screen is null");
        return screen.getGuiLeft();
    }

    @Override
    public int getTopPos(AbstractContainerScreen<?> screen) {
        Objects.requireNonNull(screen, "screen is null");
        return screen.getGuiTop();
    }

    @Override
    public @Nullable Slot getHoveredSlot(AbstractContainerScreen<?> screen) {
        Objects.requireNonNull(screen, "screen is null");
        return screen.getSlotUnderMouse();
    }
}
