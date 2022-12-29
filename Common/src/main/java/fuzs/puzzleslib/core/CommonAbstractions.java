package fuzs.puzzleslib.core;

import fuzs.puzzleslib.util.CreativeModeTabBuilder;
import fuzs.puzzleslib.util.PuzzlesUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * useful methods for gameplay related things that require mod loader specific abstractions
 */
public interface CommonAbstractions {
    /**
     * instance of the common abstractions SPI
     */
    CommonAbstractions INSTANCE = PuzzlesUtil.loadServiceProvider(CommonAbstractions.class);

    /**
     * opens a menu on both client and server
     *
     * @param player       player to open menu for
     * @param menuProvider menu factory
     */
    default void openMenu(ServerPlayer player, MenuProvider menuProvider) {
        this.openMenu(player, menuProvider, (ServerPlayer serverPlayer, FriendlyByteBuf buf) -> {
        });
    }

    /**
     * opens a menu on both client and server while also providing additional data
     *
     * @param player                  player to open menu for
     * @param menuProvider            menu factory
     * @param screenOpeningDataWriter additional data added via {@link FriendlyByteBuf}
     */
    void openMenu(ServerPlayer player, MenuProvider menuProvider, BiConsumer<ServerPlayer, FriendlyByteBuf> screenOpeningDataWriter);

    /**
     * simple access to new {@link StairBlock} instance, as vanilla constructor is protected, both mod loaders use an access transformer for this
     *
     * @param blockState parent default block state as supplier for Forge
     * @param properties properties from parent
     * @return the new stair block
     *
     * @deprecated removed in favor of access widener
     */
    @Deprecated(forRemoval = true)
    default StairBlock stairBlock(Supplier<BlockState> blockState, BlockBehaviour.Properties properties) {
        return new StairBlock(blockState.get(), properties);
    }

    /**
     * simple access to new {@link DamageSource} instance, as vanilla constructor is protected, both mod loaders use an access transformer for this
     *
     * @param messageId id for the translation key
     * @return the new damage source
     *
     * @deprecated removed in favor of access widener
     */
    @Deprecated(forRemoval = true)
    default DamageSource damageSource(String messageId) {
        return new DamageSource(messageId);
    }

    /**
     * creates a new creative mode tab, handles adding to the creative screen
     * use this when one tab is enough for the mod, <code>tabId</code> defaults to "main"
     *
     * @param modId         the mod this tab is used by
     * @param stackSupplier the display stack
     * @return the creative mode tab
     *
     * @deprecated renamed to {@link #creativeModeTab(String, Supplier)}
     */
    @Deprecated(forRemoval = true)
    default CreativeModeTab creativeTab(String modId, Supplier<ItemStack> stackSupplier) {
        return this.creativeModeTab(modId, stackSupplier);
    }

    /**
     * creates a new creative mode tab, handles adding to the creative screen
     *
     * @param modId         the mod this tab is used by
     * @param tabId         the key for this tab, useful when the mod has multiple
     * @param stackSupplier the display stack
     * @return the creative mode tab
     *
     * @deprecated functionality moved to {@link #creativeModeTabBuilder(String, String)}
     */
    @Deprecated(forRemoval = true)
    default CreativeModeTab creativeTab(String modId, String tabId, Supplier<ItemStack> stackSupplier) {
        return this.creativeModeTabBuilder(modId, tabId).setIcon(stackSupplier).build();
    }

    /**
     * creates a new creative mode tab, handles adding to the creative screen
     *
     * @param modId            the mod this tab is used by
     * @param tabId            the key for this tab, useful when the mod has multiple
     * @param stackSupplier    the display stack
     * @param cacheIcon        should the result from <code>stackSupplier</code> be cached, enabled by default, allows for a cycling icon when disabled; not supported on Fabric
     * @param stacksForDisplay manually add stacks to show in tab, allows for sorting
     * @return the creative mode tab
     *
     * @deprecated functionality moved to {@link #creativeModeTabBuilder(String, String)}
     */
    @Deprecated(forRemoval = true)
    default CreativeModeTab creativeTab(String modId, String tabId, Supplier<ItemStack> stackSupplier, boolean cacheIcon, @Nullable BiConsumer<List<ItemStack>, CreativeModeTab> stacksForDisplay) {
        CreativeModeTabBuilder tabBuilder = this.creativeModeTabBuilder(modId, tabId).setIcon(stackSupplier);
        if (!cacheIcon) tabBuilder.disableIconCache();
        if (stacksForDisplay != null) tabBuilder.appendItems(stacksForDisplay);
        return tabBuilder.build();
    }

    /**
     * creates a new creative mode tab, handles adding to the creative screen
     * use this when one tab is enough for the mod, <code>tabId</code> defaults to "main"
     *
     * @param modId             the mod this tab is used by
     * @param stackSupplier     the display stack
     * @return                  the creative mode tab
     */
    default CreativeModeTab creativeModeTab(String modId, Supplier<ItemStack> stackSupplier) {
        return this.creativeModeTabBuilder(modId).setIcon(stackSupplier).build();
    }

    /**
     * creates a builder for a new creative mode tab, the implementation handles adding to the creative screen
     * <p>use this when one tab is enough for the mod, <code>tabId</code> defaults to "main"
     *
     * @param modId the mod this tab is used by
     * @return builder instance
     */
    default CreativeModeTabBuilder creativeModeTabBuilder(String modId) {
        return this.creativeModeTabBuilder(modId, "main");
    }

    /**
     * creates a builder for a new creative mode tab, the implementation handles adding to the creative screen
     *
     * @param modId the mod this tab is used by
     * @param tabId the key for this tab, useful when the mod has multiple
     * @return builder instance
     */
    CreativeModeTabBuilder creativeModeTabBuilder(String modId, String tabId);
}
