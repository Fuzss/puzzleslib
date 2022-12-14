package fuzs.puzzleslib.core;

import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.List;
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
    public CreativeModeTab creativeTab(String modId, String tabId, Supplier<ItemStack> stackSupplier, boolean cacheIcon, @Nullable BiConsumer<List<ItemStack>, CreativeModeTab> stacksForDisplay) {
        return new CreativeModeTab(modId + "." + tabId) {

            @Override
            public ItemStack getIconItem() {
                if (cacheIcon) {
                    return super.getIconItem();
                }
                return stackSupplier.get();
            }

            @Override
            public ItemStack makeIcon() {
                return stackSupplier.get();
            }

            @Override
            public void fillItemList(NonNullList<ItemStack> items) {
                if (stacksForDisplay == null) {
                    super.fillItemList(items);
                } else {
                    stacksForDisplay.accept(items, this);
                }
            }
        };
    }
}
