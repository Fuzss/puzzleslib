package fuzs.puzzleslib.fabric.impl.core;

import com.google.common.collect.Sets;
import fuzs.puzzleslib.api.core.v1.CommonAbstractions;
import fuzs.puzzleslib.api.event.v1.core.EventPhase;
import fuzs.puzzleslib.api.event.v1.server.ServerLifecycleEvents;
import fuzs.puzzleslib.api.init.v3.registry.LookupHelper;
import fuzs.puzzleslib.fabric.api.event.v1.FabricLevelEvents;
import fuzs.puzzleslib.fabric.impl.event.FabricEventImplHelper;
import fuzs.puzzleslib.fabric.impl.event.SpawnTypeMob;
import fuzs.puzzleslib.impl.core.EventHandlerProvider;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.fabricmc.fabric.api.item.v1.EnchantingContext;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalEntityTypeTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerExplosion;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Set;
import java.util.function.BiConsumer;

public final class FabricAbstractions implements CommonAbstractions, EventHandlerProvider {
    private final Set<String> hiddenPacks = Sets.newConcurrentHashSet();
    private MinecraftServer minecraftServer;

    @Override
    public void registerEventHandlers() {
        // registers for game server starting and stopping, so we can keep an instance of the server here
        ServerLifecycleEvents.STARTING.register(EventPhase.FIRST, (MinecraftServer minecraftServer) -> {
            this.minecraftServer = minecraftServer;
        });
        ServerLifecycleEvents.STOPPED.register(EventPhase.LAST, (MinecraftServer minecraftServer) -> {
            this.minecraftServer = null;
        });
    }

    @Override
    public MinecraftServer getMinecraftServer() {
        return this.minecraftServer;
    }

    @Override
    public boolean hasChannel(ServerPlayer serverPlayer, CustomPacketPayload.Type<?> type) {
        return !(serverPlayer instanceof FakePlayer) && ServerPlayNetworking.canSend(serverPlayer, type);
    }

    @Override
    public void openMenu(ServerPlayer player, MenuProvider menuProvider, BiConsumer<ServerPlayer, RegistryFriendlyByteBuf> dataWriter) {
        player.openMenu(new ExtendedScreenHandlerFactory<RegistryFriendlyByteBuf>() {

            @Override
            public RegistryFriendlyByteBuf getScreenOpeningData(ServerPlayer player) {
                RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), player.registryAccess());
                dataWriter.accept(player, buf);
                return buf;
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

    @Override
    public Entity getPartEntityParent(Entity entity) {
        return entity instanceof EnderDragonPart enderDragonPart ? enderDragonPart.parentMob : entity;
    }

    @Override
    public boolean isBossMob(EntityType<?> type) {
        return type.is(ConventionalEntityTypeTags.BOSSES);
    }

    @Override
    public float getEnchantPowerBonus(BlockState state, Level level, BlockPos pos) {
        return state.is(BlockTags.ENCHANTMENT_POWER_PROVIDER) ? 1.0F : 0.0F;
    }

    @Override
    public boolean canEquip(ItemStack stack, EquipmentSlot slot, LivingEntity entity) {
        return slot == entity.getEquipmentSlotForItem(stack);
    }

    @Override
    public int getMobLootingLevel(Entity target, @Nullable Entity attacker, @Nullable DamageSource damageSource) {
        int enchantmentLevel = CommonAbstractions.super.getMobLootingLevel(target, attacker, damageSource);
        if (!(target instanceof LivingEntity livingEntity)) return enchantmentLevel;
        Holder<Enchantment> enchantment = LookupHelper.lookupEnchantment(target, Enchantments.LOOTING);
        return FabricEventImplHelper.onComputeEnchantedLootBonus(enchantment,
                enchantmentLevel,
                livingEntity,
                damageSource);
    }

    @Override
    public boolean getMobGriefingRule(ServerLevel serverLevel, @Nullable Entity entity) {
        return serverLevel.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
    }

    @Override
    public void onPlayerDestroyItem(Player player, ItemStack originalItemStack, @Nullable InteractionHand interactionHand) {
        // NO-OP
    }

    @Override
    public @Nullable EntitySpawnReason getMobSpawnType(Mob mob) {
        return ((SpawnTypeMob) mob).puzzleslib$getSpawnType();
    }

    @Override
    public Pack.Metadata createPackInfo(ResourceLocation id, Component description, PackCompatibility packCompatibility, FeatureFlagSet features, boolean hidden) {
        if (hidden) this.hiddenPacks.add(id.toString());
        return new Pack.Metadata(description, packCompatibility, features, Collections.emptyList());
    }

    public boolean notHidden(String id) {
        return !this.hiddenPacks.contains(id);
    }

    @Override
    public boolean canApplyAtEnchantingTable(Holder<Enchantment> enchantment, ItemStack itemStack) {
        return itemStack.canBeEnchantedWith(enchantment, EnchantingContext.PRIMARY);
    }

    @Override
    public boolean isBookEnchantable(ItemStack inputStack, ItemStack bookStack) {
        return true;
    }

    @Override
    public boolean onExplosionStart(ServerLevel serverLevel, ServerExplosion explosion) {
        return FabricLevelEvents.EXPLOSION_START.invoker().onExplosionStart(serverLevel, explosion).isInterrupt();
    }

    @Override
    public Style getRarityStyle(Rarity rarity) {
        return Style.EMPTY.applyFormat(rarity.color());
    }
}
