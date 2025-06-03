package fuzs.puzzleslib.api.item.v2;

import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * A helper class containing enchanting related methods.
 */
public final class EnchantingHelper {

    private EnchantingHelper() {
        // NO-OP
    }

    /**
     * Returns the enchanting power provided by a block. An enchanting power of 15 is required for level 30 enchants,
     * each bookshelf block provides exactly one enchanting power.
     *
     * @param blockState the block state at the given position
     * @param level      the level
     * @param blockPos   the block position in the level
     * @return enchanting power, usually zero for blocks other than bookshelves
     */
    public static float getEnchantPowerBonus(BlockState blockState, Level level, BlockPos blockPos) {
        return ProxyImpl.get().getEnchantPowerBonus(blockState, level, blockPos);
    }

    /**
     * Can the given enchanted be applied to an item stack via enchanting (in an enchanting table).
     *
     * @param enchantment the enchantment to check
     * @param itemStack   the item stack trying to receive the enchantment
     * @return is the application allowed
     */
    public static boolean canApplyAtEnchantingTable(Holder<Enchantment> enchantment, ItemStack itemStack) {
        return ProxyImpl.get().canApplyAtEnchantingTable(enchantment, itemStack);
    }

    /**
     * Tests if an enchanted book can be put onto an item stack.
     *
     * @param inputItemStack the item stack to enchant
     * @param bookItemStack  the book stack to enchant the item with
     * @return is combining both stacks allowed
     */
    public static boolean isBookEnchantable(ItemStack inputItemStack, ItemStack bookItemStack) {
        return ProxyImpl.get().isBookEnchantable(inputItemStack, bookItemStack);
    }

    /**
     * Called before an entity drops loot for determining the level of
     * {@link net.minecraft.world.item.enchantment.Enchantments#LOOTING} to apply when generating drops.
     *
     * @param target       the entity that has been killed
     * @param attacker     another entity responsible for killing the entity
     * @param damageSource the damage source the entity has been killed by
     * @return the level of looting to apply when generating drops
     */
    public static int getMobLootingLevel(Entity target, @Nullable Entity attacker, @Nullable DamageSource damageSource) {
        return ProxyImpl.get().getMobLootingLevel(target, attacker, damageSource);
    }
}
