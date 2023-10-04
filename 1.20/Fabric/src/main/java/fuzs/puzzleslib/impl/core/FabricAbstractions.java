package fuzs.puzzleslib.impl.core;

import com.google.common.collect.Sets;
import fuzs.puzzleslib.api.core.v1.CommonAbstractions;
import fuzs.puzzleslib.api.event.v1.core.EventPhase;
import fuzs.puzzleslib.api.event.v1.server.ServerLifecycleEvents;
import fuzs.puzzleslib.impl.event.SpawnTypeMob;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalEntityTypeTags;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.BiConsumer;

public final class FabricAbstractions implements CommonAbstractions, EventHandlerProvider {
    private final Set<String> hiddenPacks = Sets.newConcurrentHashSet();
    private MinecraftServer gameServer;

    @Override
    public void registerHandlers() {
        // registers for game server starting and stopping, so we can keep an instance of the server here so that
        // {@link FabricNetworkHandler} can be implemented much more similarly to Forge
        ServerLifecycleEvents.STARTING.register(EventPhase.FIRST, server -> this.gameServer = server);
        ServerLifecycleEvents.STOPPED.register(EventPhase.LAST, server -> this.gameServer = null);
    }

    @Override
    public MinecraftServer getGameServer() {
        return this.gameServer;
    }

    @Override
    public void openMenu(ServerPlayer player, MenuProvider menuProvider, BiConsumer<ServerPlayer, FriendlyByteBuf> screenOpeningDataWriter) {
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

    @Override
    public boolean isBossMob(EntityType<?> type) {
        return type.is(ConventionalEntityTypeTags.BOSSES);
    }

    @Override
    public float getEnchantPowerBonus(BlockState state, Level level, BlockPos pos) {
        return state.is(BlockTags.ENCHANTMENT_POWER_PROVIDER) ? 1.0F : 0.0F;
    }

    @Override
    public boolean canEquip(ItemStack stack, EquipmentSlot slot, Entity entity) {
        return slot == Mob.getEquipmentSlotForItem(stack);
    }

    @Override
    public int getMobLootingLevel(Entity entity, @Nullable Entity killerEntity, @Nullable DamageSource damageSource) {
        return killerEntity instanceof LivingEntity livingEntity ? EnchantmentHelper.getMobLooting(livingEntity) : 0;
    }

    @Override
    public boolean getMobGriefingRule(Level level, @Nullable Entity entity) {
        return level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
    }

    @Override
    public void onPlayerDestroyItem(Player player, ItemStack itemStack, @Nullable InteractionHand interactionHand) {

    }

    @Override
    public @Nullable MobSpawnType getMobSpawnType(Mob mob) {
        return ((SpawnTypeMob) mob).puzzleslib$getSpawnType();
    }

    @Override
    public Pack.Info createPackInfo(ResourceLocation id, Component description, int packVersion, FeatureFlagSet features, boolean hidden) {
        if (hidden) this.hiddenPacks.add(id.toString());
        return new Pack.Info(description, packVersion, features);
    }

    public boolean notHidden(String id) {
        return !this.hiddenPacks.contains(id);
    }

    @Override
    public boolean canApplyAtEnchantingTable(Enchantment enchantment, ItemStack itemStack) {
        return enchantment.category.canEnchant(itemStack.getItem());
    }

    @Override
    public boolean isAllowedOnBooks(Enchantment enchantment) {
        return true;
    }
}
