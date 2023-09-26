package fuzs.puzzleslib.impl.core;

import fuzs.puzzleslib.api.core.v1.CommonAbstractions;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

public final class ForgeAbstractions implements CommonAbstractions {

    @Override
    public MinecraftServer getGameServer() {
        return ServerLifecycleHooks.getCurrentServer();
    }

    @Override
    public void openMenu(ServerPlayer player, MenuProvider menuProvider, BiConsumer<ServerPlayer, FriendlyByteBuf> screenOpeningDataWriter) {
        NetworkHooks.openScreen(player, menuProvider, buf -> screenOpeningDataWriter.accept(player, buf));
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
    public boolean canEquip(ItemStack stack, EquipmentSlot slot, Entity entity) {
        return stack.canEquip(slot, entity);
    }

    @Override
    public int getMobLootingLevel(Entity entity, @Nullable Entity killerEntity, @Nullable DamageSource damageSource) {
        return ForgeHooks.getLootingLevel(entity, killerEntity, damageSource);
    }

    @Override
    public boolean getMobGriefingRule(Level level, @Nullable Entity entity) {
        return ForgeEventFactory.getMobGriefingEvent(level, entity);
    }

    @Override
    public void onPlayerDestroyItem(Player player, ItemStack itemStack, @Nullable InteractionHand interactionHand) {
        ForgeEventFactory.onPlayerDestroyItem(player, itemStack, interactionHand);
    }

    @Override
    public @Nullable MobSpawnType getMobSpawnType(Mob mob) {
        return mob.getSpawnType();
    }

    @Override
    public Pack.Info createPackInfo(Component description, int packVersion, FeatureFlagSet features, boolean hidden) {
        return new Pack.Info(description, packVersion, packVersion, features, hidden);
    }
}
