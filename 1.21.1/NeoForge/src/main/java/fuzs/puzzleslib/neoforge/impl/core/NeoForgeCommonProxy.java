package fuzs.puzzleslib.neoforge.impl.core;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.data.v2.tags.AbstractTagAppender;
import fuzs.puzzleslib.api.init.v3.GameRulesFactory;
import fuzs.puzzleslib.api.init.v3.registry.RegistryFactory;
import fuzs.puzzleslib.api.item.v2.ToolTypeHelper;
import fuzs.puzzleslib.api.item.v2.crafting.CombinedIngredients;
import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import fuzs.puzzleslib.api.network.v3.ServerboundMessage;
import fuzs.puzzleslib.impl.attachment.DataAttachmentRegistryImpl;
import fuzs.puzzleslib.impl.core.ModContext;
import fuzs.puzzleslib.impl.core.context.ModConstructorImpl;
import fuzs.puzzleslib.impl.network.codec.CustomPacketPayloadAdapter;
import fuzs.puzzleslib.neoforge.impl.attachment.NeoForgeDataAttachmentRegistryImpl;
import fuzs.puzzleslib.neoforge.impl.data.NeoForgeTagAppender;
import fuzs.puzzleslib.neoforge.impl.event.ForwardingLootPoolBuilder;
import fuzs.puzzleslib.neoforge.impl.event.ForwardingLootTableBuilder;
import fuzs.puzzleslib.neoforge.impl.event.NeoForgeEventInvokerRegistryImpl;
import fuzs.puzzleslib.neoforge.impl.init.MenuTypeWithData;
import fuzs.puzzleslib.neoforge.impl.init.NeoForgeGameRulesFactory;
import fuzs.puzzleslib.neoforge.impl.init.NeoForgeRegistryFactoryV3;
import fuzs.puzzleslib.neoforge.impl.init.NeoForgeRegistryFactoryV4;
import fuzs.puzzleslib.neoforge.impl.item.NeoForgeToolTypeHelper;
import fuzs.puzzleslib.neoforge.impl.item.crafting.NeoForgeCombinedIngredients;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientCommonPacketListener;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.tags.TagBuilder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.neoforge.common.extensions.ICommonPacketListener;
import net.neoforged.neoforge.entity.PartEntity;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class NeoForgeCommonProxy implements NeoForgeProxy {

    @Override
    public MinecraftServer getMinecraftServer() {
        return ServerLifecycleHooks.getCurrentServer();
    }

    @Override
    public <T> void openMenu(Player player, MenuProvider menuProvider, T data) {
        player.openMenu(new MenuProvider() {
            @Override
            public void writeClientSideData(AbstractContainerMenu containerMenu, RegistryFriendlyByteBuf buf) {
                MenuTypeWithData.encodeMenuData(containerMenu, buf, data);
            }

            @Override
            public Component getDisplayName() {
                return menuProvider.getDisplayName();
            }

            @Nullable
            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
                return menuProvider.createMenu(containerId, inventory, player);
            }
        });
    }

    @Override
    public void openMenu(ServerPlayer serverPlayer, MenuProvider menuProvider, BiConsumer<ServerPlayer, RegistryFriendlyByteBuf> dataWriter) {
        serverPlayer.openMenu(menuProvider, (RegistryFriendlyByteBuf buf) -> {
            dataWriter.accept(serverPlayer, buf);
        });
    }

    @Override
    public Pack.Metadata createPackInfo(ResourceLocation resourceLocation, Component descriptionComponent, PackCompatibility packCompatibility, FeatureFlagSet featureFlagSet, boolean hidden) {
        return new Pack.Metadata(descriptionComponent,
                packCompatibility,
                featureFlagSet,
                Collections.emptyList(),
                hidden);
    }

    @Override
    public Style getRarityStyle(Rarity rarity) {
        return rarity.getStyleModifier().apply(Style.EMPTY);
    }

    @Override
    public void onPlayerDestroyItem(Player player, ItemStack originalItemStack, @Nullable InteractionHand interactionHand) {
        EventHooks.onPlayerDestroyItem(player, originalItemStack, interactionHand);
    }

    @Override
    public void forEachPool(LootTable.Builder lootTable, Consumer<? super LootPool.Builder> lootPoolConsumer) {
        if (lootTable instanceof ForwardingLootTableBuilder) {
            for (LootPool lootPool : lootTable.build().pools) {
                lootPoolConsumer.accept(new ForwardingLootPoolBuilder(lootPool));
            }
        } else {
            throw new UnsupportedOperationException("Must be ForwardingLootTableBuilder");
        }
    }

    @Override
    public float getEnchantPowerBonus(BlockState blockState, Level level, BlockPos blockPos) {
        return blockState.getEnchantPowerBonus(level, blockPos);
    }

    @Override
    public boolean canApplyAtEnchantingTable(Holder<Enchantment> enchantment, ItemStack itemStack) {
        return itemStack.isPrimaryItemFor(enchantment);
    }

    @Override
    public boolean onExplosionStart(Level level, Explosion explosion) {
        return EventHooks.onExplosionStart(level, explosion);
    }

    @MustBeInvokedByOverriders
    @Override
    public void registerAllLoadingHandlers() {
        NeoForgeEventInvokerRegistryImpl.registerLoadingHandlers();
    }

    @MustBeInvokedByOverriders
    @Override
    public void registerAllEventHandlers() {
        NeoForgeEventInvokerRegistryImpl.freezeModBusEvents();
        NeoForgeEventInvokerRegistryImpl.registerEventHandlers();
    }

    @Override
    public boolean hasChannel(PacketListener packetListener, CustomPacketPayload.Type<?> type) {
        return packetListener instanceof ICommonPacketListener commonPacketListener && commonPacketListener.hasChannel(
                type);
    }

    @Override
    public Connection getConnection(PacketListener packetListener) {
        return ((ICommonPacketListener) packetListener).getConnection();
    }

    @Override
    public Packet<ClientCommonPacketListener> toClientboundPacket(CustomPacketPayload payload) {
        return payload.toVanillaClientbound();
    }

    @Override
    public Packet<ServerCommonPacketListener> toServerboundPacket(CustomPacketPayload payload) {
        return payload.toVanillaServerbound();
    }

    @Override
    public void finishConfigurationTask(ServerConfigurationPacketListener packetListener, ConfigurationTask.Type type) {
        packetListener.finishCurrentTask(type);
    }

    @Override
    public ModConstructorImpl<ModConstructor> getModConstructorImpl() {
        return new NeoForgeModConstructor();
    }

    @Override
    public ModContext getModContext(String modId) {
        return new NeoForgeModContext(modId);
    }

    @Override
    public RegistryFactory getRegistryFactoryV3() {
        return new NeoForgeRegistryFactoryV3();
    }

    @Override
    public fuzs.puzzleslib.api.init.v4.registry.RegistryFactory getRegistryFactoryV4() {
        return new NeoForgeRegistryFactoryV4();
    }

    @Override
    public GameRulesFactory getGameRulesFactory() {
        return new NeoForgeGameRulesFactory();
    }

    @Override
    public ToolTypeHelper getToolTypeHelper() {
        return new NeoForgeToolTypeHelper();
    }

    @Override
    public CombinedIngredients getCombinedIngredients() {
        return new NeoForgeCombinedIngredients();
    }

    @Override
    public <T> AbstractTagAppender<T> getTagAppender(TagBuilder tagBuilder, @Nullable Function<T, ResourceKey<T>> keyExtractor) {
        return new NeoForgeTagAppender<>(tagBuilder, keyExtractor);
    }

    @Override
    public DataAttachmentRegistryImpl getDataAttachmentRegistry() {
        return new NeoForgeDataAttachmentRegistryImpl();
    }

    @Override
    public boolean canEquip(ItemStack itemStack, EquipmentSlot equipmentSlot, LivingEntity livingEntity) {
        return itemStack.canEquip(equipmentSlot, livingEntity);
    }

    @Override
    public @Nullable MobSpawnType getMobSpawnReason(Mob mob) {
        return mob.getSpawnType();
    }

    @Override
    public boolean isMobGriefingAllowed(ServerLevel serverLevel, @Nullable Entity entity) {
        return EventHooks.canEntityGrief(serverLevel, entity);
    }

    @Override
    public Entity getPartEntityParent(Entity entity) {
        return entity instanceof PartEntity<?> partEntity ? partEntity.getParent() : entity;
    }

    @Override
    public boolean isFakePlayer(ServerPlayer serverPlayer) {
        return serverPlayer.isFakePlayer();
    }

    @Override
    public boolean isPiglinCurrency(ItemStack itemStack) {
        return itemStack.isPiglinCurrency();
    }

    @Override
    public <M1, M2> CompletableFuture<Void> registerClientReceiver(CustomPacketPayloadAdapter<M1> payload, IPayloadContext context, Function<M1, ClientboundMessage<M2>> adapter) {
        return CompletableFuture.allOf();
    }

    @Override
    public <M1, M2> CompletableFuture<Void> registerServerReceiver(CustomPacketPayloadAdapter<M1> payload, IPayloadContext context, Function<M1, ServerboundMessage<M2>> adapter) {
        return context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            ServerboundMessage<M2> message = adapter.apply(payload.unwrap());
            message.getHandler()
                    .handle(message.unwrap(), player.server, player.connection, player, player.serverLevel());
        });
    }
}
