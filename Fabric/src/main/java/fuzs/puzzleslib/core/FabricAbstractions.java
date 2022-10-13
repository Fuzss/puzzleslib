package fuzs.puzzleslib.core;

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
}
