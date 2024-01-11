package fuzs.puzzleslib.fabric.impl.client.screen;

import fuzs.puzzleslib.api.client.screen.v2.ScreenHelper;
import fuzs.puzzleslib.fabric.mixin.client.accessor.AbstractContainerScreenFabricAccessor;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
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
public final class FabricScreenHelper implements ScreenHelper {

    @Override
    public Minecraft getMinecraft(Screen screen) {
        return Screens.getClient(screen);
    }

    @Override
    public Font getFont(Screen screen) {
        return Screens.getTextRenderer(screen);
    }

    @Override
    public int getImageWidth(AbstractContainerScreen<?> screen) {
        Objects.requireNonNull(screen, "screen is null");
        return ((AbstractContainerScreenFabricAccessor) screen).puzzleslib$getXSize();
    }

    @Override
    public int getImageHeight(AbstractContainerScreen<?> screen) {
        Objects.requireNonNull(screen, "screen is null");
        return ((AbstractContainerScreenFabricAccessor) screen).puzzleslib$getYSize();
    }

    @Override
    public int getLeftPos(AbstractContainerScreen<?> screen) {
        Objects.requireNonNull(screen, "screen is null");
        return ((AbstractContainerScreenFabricAccessor) screen).puzzleslib$getGuiLeft();
    }

    @Override
    public int getTopPos(AbstractContainerScreen<?> screen) {
        Objects.requireNonNull(screen, "screen is null");
        return ((AbstractContainerScreenFabricAccessor) screen).puzzleslib$getGuiTop();
    }

    @Override
    public @Nullable Slot getHoveredSlot(AbstractContainerScreen<?> screen) {
        Objects.requireNonNull(screen, "screen is null");
        return ((AbstractContainerScreenFabricAccessor) screen).puzzleslib$getSlotUnderMouse();
    }
}
