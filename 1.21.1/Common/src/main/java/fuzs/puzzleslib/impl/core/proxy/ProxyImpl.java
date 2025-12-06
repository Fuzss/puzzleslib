package fuzs.puzzleslib.impl.core.proxy;

import fuzs.puzzleslib.api.core.v1.Proxy;
import fuzs.puzzleslib.api.event.v1.LoadCompleteCallback;
import fuzs.puzzleslib.impl.core.ModContext;
import fuzs.puzzleslib.impl.event.core.EventInvokerImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface ProxyImpl extends SidedProxy, FactoriesProxy, NetworkingProxy, EntityProxy {

    static ProxyImpl get() {
        return (ProxyImpl) Proxy.INSTANCE;
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

    @Deprecated
    void openMenu(ServerPlayer serverPlayer, MenuProvider menuProvider, BiConsumer<ServerPlayer, RegistryFriendlyByteBuf> dataWriter);

    Pack.Metadata createPackInfo(ResourceLocation resourceLocation, Component descriptionComponent, PackCompatibility packCompatibility, FeatureFlagSet featureFlagSet, boolean hidden);

    Style getRarityStyle(Rarity rarity);

    void onPlayerDestroyItem(Player player, ItemStack originalItemStack, @Nullable InteractionHand interactionHand);

    void forEachPool(LootTable.Builder lootTable, Consumer<? super LootPool.Builder> lootPoolConsumer);

    float getEnchantPowerBonus(BlockState blockState, Level level, BlockPos blockPos);

    boolean canApplyAtEnchantingTable(Holder<Enchantment> enchantment, ItemStack itemStack);

    @Deprecated
    boolean onExplosionStart(Level level, Explosion explosion);
}
