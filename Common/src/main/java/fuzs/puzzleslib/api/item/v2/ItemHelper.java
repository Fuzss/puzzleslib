package fuzs.puzzleslib.api.item.v2;

import fuzs.puzzleslib.api.core.v1.CommonAbstractions;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * A simple utility for abstracting common {@link ItemStack} functionality.
 */
public final class ItemHelper {

    private ItemHelper() {
        // NO-OP
    }

    /**
     * Called for damaging an item stack, potentially breaking it when it runs out of durability.
     *
     * @param itemStack       the item stack to be hurt
     * @param amount          the amount to damage the stack by
     * @param livingEntity    the entity using the stack
     * @param interactionHand the hand using the stack
     */
    public static void hurtAndBreak(ItemStack itemStack, int amount, LivingEntity livingEntity, InteractionHand interactionHand) {
        hurtAndBreak(itemStack, amount, livingEntity, LivingEntity.getSlotForHand(interactionHand));
    }

    /**
     * Called for damaging an item stack, potentially breaking it when it runs out of durability.
     *
     * @param itemStack     the item stack to be hurt
     * @param amount        the amount to damage the stack by
     * @param livingEntity  the entity using the stack
     * @param equipmentSlot the slot the stack is present in
     */
    public static void hurtAndBreak(ItemStack itemStack, int amount, LivingEntity livingEntity, EquipmentSlot equipmentSlot) {
        if (livingEntity.level() instanceof ServerLevel serverLevel) {
            ServerPlayer serverPlayer = livingEntity instanceof ServerPlayer ? (ServerPlayer) livingEntity : null;
            hurtAndBreak(itemStack, amount, serverLevel, serverPlayer, (Item item) -> {
                livingEntity.onEquippedItemBroken(item, equipmentSlot);
            });
        }
    }

    /**
     * Called for damaging an item stack, potentially breaking it when it runs out of durability.
     *
     * @param itemStack    the item stack to be hurt
     * @param amount       the amount to damage the stack by
     * @param serverLevel  the level
     * @param serverPlayer the player using the stack
     * @param onBreak      what happens when the stack breaks, usually calls
     *                     {@link LivingEntity#onEquippedItemBroken(Item, EquipmentSlot)}
     */
    public static void hurtAndBreak(ItemStack itemStack, int amount, ServerLevel serverLevel, @Nullable ServerPlayer serverPlayer, Consumer<Item> onBreak) {
        ItemStack originalItemStack = copyItemStackIfNecessary(itemStack, serverPlayer);
        itemStack.hurtAndBreak(amount, serverLevel, serverPlayer, (Item item) -> {
            onBreak.accept(item);
            if (serverPlayer != null) {
                CommonAbstractions.INSTANCE.onPlayerDestroyItem(serverPlayer, originalItemStack, null);
            }
        });
    }

    private static ItemStack copyItemStackIfNecessary(ItemStack itemStack, @Nullable ServerPlayer serverPlayer) {
        // the NeoForge item destroy event uses a copy of the original item stack, so make sure we keep this here
        if (serverPlayer != null && ModLoaderEnvironment.INSTANCE.getModLoader().isForgeLike()) {
            return itemStack.copy();
        } else {
            return itemStack;
        }
    }
}
