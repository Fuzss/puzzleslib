package fuzs.puzzleslib.neoforge.impl.core;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.core.v1.context.PayloadTypesContext;
import fuzs.puzzleslib.api.data.v2.tags.AbstractTagAppender;
import fuzs.puzzleslib.api.init.v3.GameRulesFactory;
import fuzs.puzzleslib.api.init.v3.registry.LookupHelper;
import fuzs.puzzleslib.api.init.v3.registry.RegistryFactory;
import fuzs.puzzleslib.api.item.v2.ToolTypeHelper;
import fuzs.puzzleslib.api.item.v2.crafting.CombinedIngredients;
import fuzs.puzzleslib.impl.attachment.DataAttachmentRegistryImpl;
import fuzs.puzzleslib.impl.core.ModContext;
import fuzs.puzzleslib.impl.core.context.ModConstructorImpl;
import fuzs.puzzleslib.neoforge.api.event.v1.entity.living.ComputeEnchantedLootBonusEvent;
import fuzs.puzzleslib.neoforge.impl.attachment.NeoForgeDataAttachmentRegistryImpl;
import fuzs.puzzleslib.neoforge.impl.core.context.PayloadTypesContextNeoForgeImpl;
import fuzs.puzzleslib.neoforge.impl.data.NeoForgeTagAppender;
import fuzs.puzzleslib.neoforge.impl.event.ForwardingLootPoolBuilder;
import fuzs.puzzleslib.neoforge.impl.event.ForwardingLootTableBuilder;
import fuzs.puzzleslib.neoforge.impl.event.NeoForgeEventInvokerRegistryImpl;
import fuzs.puzzleslib.neoforge.impl.init.NeoForgeGameRulesFactory;
import fuzs.puzzleslib.neoforge.impl.init.NeoForgeRegistryFactory;
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
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerExplosion;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.neoforge.common.extensions.ICommonPacketListener;
import net.neoforged.neoforge.entity.PartEntity;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class NeoForgeCommonProxy implements NeoForgeProxy {

    @Override
    public MinecraftServer getMinecraftServer() {
        return ServerLifecycleHooks.getCurrentServer();
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
    public boolean onExplosionStart(ServerLevel serverLevel, ServerExplosion explosion) {
        return EventHooks.onExplosionStart(serverLevel, explosion);
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

    @MustBeInvokedByOverriders
    @Override
    public void registerLoadingHandlers() {
        NeoForgeEventInvokerRegistryImpl.registerLoadingHandlers();
    }

    @MustBeInvokedByOverriders
    @Override
    public void registerEventHandlers() {
        NeoForgeEventInvokerRegistryImpl.freezeModBusEvents();
        NeoForgeEventInvokerRegistryImpl.registerEventHandlers();
    }

    @Override
    public boolean hasChannel(PacketListener packetListener, CustomPacketPayload.Type<?> type) {
        return packetListener instanceof ICommonPacketListener commonPacketListener &&
                commonPacketListener.hasChannel(type);
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
    public PayloadTypesContext createPayloadTypesContext(String modId, RegisterPayloadHandlersEvent evt) {
        return new PayloadTypesContextNeoForgeImpl.ServerImpl(modId, evt);
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
    public boolean isBookEnchantable(ItemStack inputItemStack, ItemStack bookItemStack) {
        return inputItemStack.isBookEnchantable(bookItemStack);
    }

    @Override
    public int getMobLootingLevel(Entity target, @Nullable Entity attacker, @Nullable DamageSource damageSource) {
        int enchantmentLevel = NeoForgeProxy.super.getMobLootingLevel(target, attacker, damageSource);
        if (!(target instanceof LivingEntity livingEntity)) return enchantmentLevel;
        Holder<Enchantment> enchantment = LookupHelper.lookupEnchantment(target, Enchantments.LOOTING);
        return ComputeEnchantedLootBonusEvent.onComputeEnchantedLootBonus(enchantment,
                enchantmentLevel,
                livingEntity,
                damageSource);
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
    public RegistryFactory getRegistryFactory() {
        return new NeoForgeRegistryFactory();
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
    public @Nullable EntitySpawnReason getMobSpawnType(Mob mob) {
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
}
