package fuzs.puzzleslib.core;

import fuzs.puzzleslib.impl.util.ForgeCreativeModeTabBuilder;
import fuzs.puzzleslib.util.CreativeModeTabBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraftforge.network.NetworkHooks;

import java.util.function.BiConsumer;

public final class ForgeAbstractions implements CommonAbstractions {

    @Override
    public void openMenu(ServerPlayer player, MenuProvider menuProvider, BiConsumer<ServerPlayer, FriendlyByteBuf> screenOpeningDataWriter) {
        NetworkHooks.openScreen(player, menuProvider, buf -> screenOpeningDataWriter.accept(player, buf));
    }

    @Override
    public CreativeModeTabBuilder creativeModeTabBuilder(String modId, String tabId) {
        return new ForgeCreativeModeTabBuilder(modId, tabId);
    }
}
