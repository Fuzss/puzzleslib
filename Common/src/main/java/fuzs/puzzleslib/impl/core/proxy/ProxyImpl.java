package fuzs.puzzleslib.impl.core.proxy;

import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.core.v1.ServiceProviderHelper;
import fuzs.puzzleslib.api.event.v1.LoadCompleteCallback;
import fuzs.puzzleslib.impl.client.core.proxy.ClientProxyImpl;
import fuzs.puzzleslib.impl.core.ModContext;
import fuzs.puzzleslib.impl.event.core.EventInvokerImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.util.Util;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public interface ProxyImpl extends SidedProxy, FactoriesProxy, NetworkingProxy, EntityProxy {
    ProxyImpl INSTANCE = Util.make(() -> {
        if (ModLoaderEnvironment.INSTANCE.isClient()) {
            return ServiceProviderHelper.load(ClientProxyImpl.class);
        } else {
            return ServiceProviderHelper.load(ProxyImpl.class);
        }
    });

    static ProxyImpl get() {
        return ProxyImpl.INSTANCE;
    }

    @MustBeInvokedByOverriders
    default void registerEventHandlers() {
        LoadCompleteCallback.EVENT.register(() -> {
            ModContext.forEach(ModContext::runAfterConstruction);
            EventInvokerImpl.initialize();
        });
    }

    MinecraftServer getMinecraftServer();

    <T> void openMenu(Player player, MenuProvider menuProvider, T data);

    Pack.Metadata createPackInfo(Identifier identifier, Component descriptionComponent, PackCompatibility packCompatibility, FeatureFlagSet featureFlagSet, boolean isHidden);

    boolean isPackHidden(Pack pack);

    void setPackHidden(Pack pack, boolean isHidden);

    Style getRarityStyle(Rarity rarity);

    void onPlayerDestroyItem(Player player, ItemStack originalItemStack, @Nullable InteractionHand interactionHand);

    void forEachPool(LootTable.Builder lootTable, Consumer<? super LootPool.Builder> lootPoolConsumer);

    float getEnchantPowerBonus(BlockState blockState, Level level, BlockPos blockPos);

    boolean canApplyAtEnchantingTable(Holder<Enchantment> enchantment, ItemStack itemStack);
}
