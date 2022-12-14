package fuzs.puzzleslib.core;

import fuzs.puzzleslib.impl.util.ForgeCreativeModeTabBuilder;
import fuzs.puzzleslib.util.CreativeModeTabBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkHooks;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public final class ForgeAbstractions implements CommonAbstractions {

    @Override
    public void openMenu(ServerPlayer player, MenuProvider menuProvider, BiConsumer<ServerPlayer, FriendlyByteBuf> screenOpeningDataWriter) {
        NetworkHooks.openScreen(player, menuProvider, buf -> screenOpeningDataWriter.accept(player, buf));
    }

    @Override
    public StairBlock stairBlock(Supplier<BlockState> blockState, BlockBehaviour.Properties properties) {
        return new StairBlock(blockState, properties);
    }

    @Override
    public DamageSource damageSource(String messageId) {
        return new DamageSource(messageId);
    }

    @Override
    public CreativeModeTabBuilder creativeModeTabBuilder(String modId, String tabId) {
        return new ForgeCreativeModeTabBuilder(modId, tabId);
    }
}
