package fuzs.puzzleslib.core;

import fuzs.puzzleslib.impl.creativetab.FabricCreativeModeTabBuilder;
import fuzs.puzzleslib.util.CreativeModeTabBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

public final class FabricAbstractions implements CommonAbstractions {

    @Override
    public void openMenu(ServerPlayer player, MenuProvider menuProvider, BiConsumer<ServerPlayer, FriendlyByteBuf> screenOpeningDataWriter) {
        // this is done to fix an early class loading issue on Quilt due to net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory
        // TODO remove wrapping Runnable in 1.19.3 together with deprecated fuzs.puzzleslib.core.CoreServices class, this is otherwise handled by creating an SPI instance only on demand in the main interface
        new Runnable() {

            @Override
            public void run() {
                player.openMenu(new ExtendedScreenHandlerFactory() {

                    @Override
                    public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
                        screenOpeningDataWriter.accept(player, buf);
                    }

                    @Override
                    public Component getDisplayName() {
                        return menuProvider.getDisplayName();
                    }

                    @Nullable
                    @Override
                    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
                        return menuProvider.createMenu(i, inventory, player);
                    }
                });
            }
        }.run();
    }

    @Override
    public CreativeModeTabBuilder creativeModeTabBuilder(String modId, String tabId) {
        return new FabricCreativeModeTabBuilder(modId, tabId);
    }
}
