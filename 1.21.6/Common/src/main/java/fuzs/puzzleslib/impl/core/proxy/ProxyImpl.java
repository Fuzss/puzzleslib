package fuzs.puzzleslib.impl.core.proxy;

import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.core.v1.ServiceProviderHelper;
import fuzs.puzzleslib.impl.client.core.proxy.ClientProxyImpl;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.ServerExplosion;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface ProxyImpl extends SidedProxy, FactoriesProxy, NetworkingProxy, EnchantingProxy, EntityProxy {
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

    MinecraftServer getMinecraftServer();

    <T> void openMenu(Player player, MenuProvider menuProvider, T data);

    Pack.Metadata createPackInfo(ResourceLocation resourceLocation, Component descriptionComponent, PackCompatibility packCompatibility, FeatureFlagSet featureFlagSet, boolean hidden);

    boolean onExplosionStart(ServerLevel serverLevel, ServerExplosion explosion);

    Style getRarityStyle(Rarity rarity);

    void forEachPool(LootTable.Builder lootTable, Consumer<? super LootPool.Builder> lootPoolConsumer);

    void onPlayerDestroyItem(Player player, ItemStack originalItemStack, @Nullable InteractionHand interactionHand);
}
