package fuzs.puzzleslib.core;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

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
    public StairBlock stairBlock(Supplier<BlockState> blockState, BlockBehaviour.Properties properties) {
        return new StairBlock(blockState.get(), properties);
    }

    @Override
    public DamageSource damageSource(String messageId) {
        return new DamageSource(messageId);
    }

    @Override
    public CreativeModeTab creativeTab(String modId, String tabId, Supplier<ItemStack> stackSupplier, boolean cacheIcon, @Nullable BiConsumer<List<ItemStack>, CreativeModeTab> stacksForDisplay) {
        FabricItemGroupBuilder builder = FabricItemGroupBuilder.create(new ResourceLocation(modId, tabId)).icon(stackSupplier);
        if (stacksForDisplay != null) builder.appendItems(stacksForDisplay);
        return builder.build();
    }
}
