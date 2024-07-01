package fuzs.puzzleslib.neoforge.impl.core;

import fuzs.puzzleslib.api.core.v1.CommonAbstractions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.function.BiConsumer;

public final class NeoForgeAbstractions implements CommonAbstractions {

    @Override
    public MinecraftServer getMinecraftServer() {
        return ServerLifecycleHooks.getCurrentServer();
    }

    @Override
    public void openMenu(ServerPlayer player, MenuProvider menuProvider, BiConsumer<ServerPlayer, FriendlyByteBuf> screenOpeningDataWriter) {
        player.openMenu(menuProvider, buf -> screenOpeningDataWriter.accept(player, buf));
    }

    @Override
    public boolean isBossMob(EntityType<?> type) {
        return type.is(Tags.EntityTypes.BOSSES);
    }

    @Override
    public float getEnchantPowerBonus(BlockState state, Level level, BlockPos pos) {
        return state.getEnchantPowerBonus(level, pos);
    }

    @Override
    public boolean canEquip(ItemStack stack, EquipmentSlot slot, LivingEntity entity) {
        return stack.canEquip(slot, entity);
    }

    @Override
    public int getMobLootingLevel(Entity entity, @Nullable Entity killerEntity, @Nullable DamageSource damageSource) {
        return CommonHooks.getLootingLevel(entity, killerEntity, damageSource);
    }

    @Override
    public boolean getMobGriefingRule(Level level, @Nullable Entity entity) {
        return EventHooks.canEntityGrief(level, entity);
    }

    @Override
    public void onPlayerDestroyItem(Player player, ItemStack itemStack, @Nullable InteractionHand interactionHand) {
        EventHooks.onPlayerDestroyItem(player, itemStack, interactionHand);
    }

    @Override
    public @Nullable MobSpawnType getMobSpawnType(Mob mob) {
        return mob.getSpawnType();
    }

    @Override
    public Pack.Metadata createPackInfo(ResourceLocation id, Component description, PackCompatibility packCompatibility, FeatureFlagSet features, boolean hidden) {
        return new Pack.Metadata(description, packCompatibility, features, Collections.emptyList(), hidden);
    }

    @Override
    public boolean canApplyAtEnchantingTable(Holder<Enchantment> enchantment, ItemStack itemStack) {
        return itemStack.isPrimaryItemFor(enchantment);
    }

    @Override
    public boolean isBookEnchantable(ItemStack inputStack, ItemStack bookStack) {
        return inputStack.isBookEnchantable(bookStack);
    }

    @Override
    public boolean onExplosionStart(Level level, Explosion explosion) {
        return EventHooks.onExplosionStart(level, explosion);
    }
}
