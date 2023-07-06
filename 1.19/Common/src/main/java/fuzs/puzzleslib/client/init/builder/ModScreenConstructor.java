package fuzs.puzzleslib.client.init.builder;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public interface ModScreenConstructor<T extends AbstractContainerMenu, U extends Screen & MenuAccess<T>> {

    U create(T abstractContainerMenu, Inventory inventory, Component component);
}
