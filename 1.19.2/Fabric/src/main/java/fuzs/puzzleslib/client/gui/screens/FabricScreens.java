package fuzs.puzzleslib.client.gui.screens;

import fuzs.puzzleslib.mixin.client.accessor.AbstractContainerScreenFabricAccessor;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * a helper class for accessing encapsulated fields on a screen
 * on Forge those are all exposed, but Fabric requires special accessors
 *
 * <p>for adding buttons, those are handled very different on both mod loaders:
 * on Forge add buttons during init event with appropriate helper methods
 * on Fabric adding is done via custom ButtonList
 */
public final class FabricScreens implements CommonScreens {

    @Override
    public Minecraft getMinecraft(Screen screen) {
        return Screens.getClient(screen);
    }

    @Override
    public Font getFont(Screen screen) {
        return Screens.getTextRenderer(screen);
    }

    @Override
    public ItemRenderer getItemRenderer(Screen screen) {
        return Screens.getItemRenderer(screen);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Widget> getRenderableButtons(Screen screen) {
        return (List<Widget>) (List<?>) Screens.getButtons(screen);
    }

    @Override
    public int getImageWidth(AbstractContainerScreen<?> screen) {
        Objects.requireNonNull(screen, "Screen cannot be null");
        return ((AbstractContainerScreenFabricAccessor) screen).getXSize();
    }

    @Override
    public int getImageHeight(AbstractContainerScreen<?> screen) {
        Objects.requireNonNull(screen, "Screen cannot be null");
        return ((AbstractContainerScreenFabricAccessor) screen).getYSize();
    }

    @Override
    public int getLeftPos(AbstractContainerScreen<?> screen) {
        Objects.requireNonNull(screen, "Screen cannot be null");
        return ((AbstractContainerScreenFabricAccessor) screen).getGuiLeft();
    }

    @Override
    public int getTopPos(AbstractContainerScreen<?> screen) {
        Objects.requireNonNull(screen, "Screen cannot be null");
        return ((AbstractContainerScreenFabricAccessor) screen).getGuiTop();
    }

    @Override
    public @Nullable Slot getHoveredSlot(AbstractContainerScreen<?> screen) {
        Objects.requireNonNull(screen, "Screen cannot be null");
        return ((AbstractContainerScreenFabricAccessor) screen).getSlotUnderMouse();
    }
}
