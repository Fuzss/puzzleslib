package fuzs.puzzleslib.impl.core;

import fuzs.puzzleslib.api.core.v1.CommonAbstractions;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

public final class ForgeAbstractions implements CommonAbstractions {

    @Override
    public void openMenu(ServerPlayer player, MenuProvider menuProvider, BiConsumer<ServerPlayer, FriendlyByteBuf> screenOpeningDataWriter) {
        NetworkHooks.openGui(player, menuProvider, buf -> screenOpeningDataWriter.accept(player, buf));
    }

    @Override
    public boolean isBossMob(EntityType<?> type) {
        // this should ideally check Entity::canChangeDimensions, but we only have access to entity type
        // in newer versions this is replaced with the Forge tag
        return type == EntityType.ENDER_DRAGON || type == EntityType.WITHER;
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
}
