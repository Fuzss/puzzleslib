package fuzs.puzzleslib.api.client.core.v1.context;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

/**
 * Register a client-side screen factory to be constructed when opening an {@link AbstractContainerMenu}.
 */
@FunctionalInterface
public interface MenuScreensContext {

    /**
     * Register a factory for creating a {@link net.minecraft.client.gui.screens.inventory.AbstractContainerScreen} for
     * a corresponding {@link MenuType}.
     *
     * @param menuType the menu type
     * @param factory  the screen factory
     * @param <M>      the type of menu
     * @param <S>      the type of screen
     */
    <M extends AbstractContainerMenu, S extends Screen & MenuAccess<M>> void registerMenuScreen(MenuType<? extends M> menuType, MenuScreens.ScreenConstructor<M, S> factory);
}
