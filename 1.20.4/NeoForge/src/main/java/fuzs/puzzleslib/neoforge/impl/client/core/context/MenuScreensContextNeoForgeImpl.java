package fuzs.puzzleslib.neoforge.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.MenuScreensContext;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

import java.util.Objects;

public record MenuScreensContextNeoForgeImpl(RegisterMenuScreensEvent evt) implements MenuScreensContext {

    @Override
    public <M extends AbstractContainerMenu, S extends Screen & MenuAccess<M>> void registerMenuScreen(MenuType<? extends M> menuType, MenuScreens.ScreenConstructor<M, S> factory) {
        Objects.requireNonNull(menuType, "menu type is null");
        Objects.requireNonNull(factory, "factory is null");
        this.evt.register(menuType, factory);
    }
}
