package fuzs.puzzleslib.common.api.client.gui.v2.screens.inventory;

import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

/**
 * A custom {@link AbstractContainerScreen} extension that fully supports adding
 * {@link net.minecraft.client.gui.components.AbstractWidget} by adding back the missing super calls to
 * {@link net.minecraft.client.gui.components.events.ContainerEventHandler}.
 */
public abstract class AbstractWidgetsContainerScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {

    public AbstractWidgetsContainerScreen(T menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    public AbstractWidgetsContainerScreen(T menu, Inventory inventory, Component title, int imageWidth, int imageHeight) {
        super(menu, inventory, title, imageWidth, imageHeight);
    }

    /**
     * @see net.minecraft.client.gui.components.events.ContainerEventHandler#mouseScrolled(double, double, double,
     *         double)
     */
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) {
            return true;
        }

        return this.getChildAt(mouseX, mouseY).filter((GuiEventListener listener) -> {
            return listener.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        }).isPresent();
    }
}
