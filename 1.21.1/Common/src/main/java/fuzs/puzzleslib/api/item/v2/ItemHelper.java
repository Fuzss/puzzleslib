package fuzs.puzzleslib.api.item.v2;

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
     * @param itemStack the item stack to be hurt
     * @param amount    the amount to damage the stack by
     * @param level     the level
     * @param player    the player using the stack
     * @param onBreak   what happens when the stack breaks, usually calls
     *                  {@link LivingEntity#onEquippedItemBroken(Item, EquipmentSlot)}
     */
    public static void hurtAndBreak(ItemStack itemStack, int amount, ServerLevel level, @Nullable ServerPlayer player, Consumer<Item> onBreak) {
        itemStack.hurtAndBreak(amount, level, player, onBreak);
    }

    /**
     * Called for damaging an item stack, potentially breaking it when it runs out of durability.
     *
     * @param itemStack the item stack to be hurt
     * @param amount    the amount to damage the stack by
     * @param entity    the entity using the stack
     * @param slot      the slot the stack is present in
     */
    public static void hurtAndBreak(ItemStack itemStack, int amount, LivingEntity entity, EquipmentSlot slot) {
        itemStack.hurtAndBreak(amount, entity, slot);
    }

    /**
     * Called for damaging an item stack, potentially breaking it when it runs out of durability.
     *
     * @param itemStack       the item stack to be hurt
     * @param amount          the amount to damage the stack by
     * @param entity          the entity using the stack
     * @param interactionHand the hand using the stack
     */
    public static void hurtAndBreak(ItemStack itemStack, int amount, LivingEntity entity, InteractionHand interactionHand) {
        hurtAndBreak(itemStack, amount, entity, LivingEntity.getSlotForHand(interactionHand));
    }
}
