package fuzs.puzzleslib.api.item.v2;

import fuzs.puzzleslib.api.init.v3.registry.LookupHelper;
import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.Nullable;

/**
 * A helper class containing enchanting related methods.
 */
public final class EnchantingHelper {

    private EnchantingHelper() {
        // NO-OP
    }

    /**
     * Looks up an enchantment holder in the provided registry access.
     *
     * @param entity      the registry access for retrieving the registry
     * @param resourceKey the value key
     * @return the holder from the registry
     */
    public static Holder<Enchantment> lookup(Entity entity, ResourceKey<Enchantment> resourceKey) {
        return LookupHelper.lookup(entity, Registries.ENCHANTMENT, resourceKey);
    }

    /**
     * Looks up an enchantment holder in the provided registry access.
     *
     * @param levelReader the registry access for retrieving the registry
     * @param resourceKey the value key
     * @return the holder from the registry
     */
    public static Holder<Enchantment> lookup(LevelReader levelReader, ResourceKey<Enchantment> resourceKey) {
        return LookupHelper.lookup(levelReader, Registries.ENCHANTMENT, resourceKey);
    }

    /**
     * Looks up an enchantment holder in the provided registry access.
     *
     * @param registries  the registry access for retrieving the registry
     * @param resourceKey the value key
     * @return the holder from the registry
     */
    public static Holder<Enchantment> lookup(HolderLookup.Provider registries, ResourceKey<Enchantment> resourceKey) {
        return LookupHelper.lookup(registries, Registries.ENCHANTMENT, resourceKey);
    }

    /**
     * @see EnchantmentHelper#getItemEnchantmentLevel(Holder, ItemStack)
     */
    public static int getItemEnchantmentLevel(HolderLookup.Provider registries, ResourceKey<Enchantment> enchantment, ItemStack itemStack) {
        Holder<Enchantment> holder = lookup(registries, enchantment);
        return EnchantmentHelper.getItemEnchantmentLevel(holder, itemStack);
    }

    /**
     * @see EnchantmentHelper#getEnchantmentLevel(Holder, LivingEntity)
     */
    public static int getEnchantmentLevel(ResourceKey<Enchantment> enchantment, LivingEntity livingEntity) {
        Holder<Enchantment> holder = lookup(livingEntity, enchantment);
        return EnchantmentHelper.getEnchantmentLevel(holder, livingEntity);
    }

    /**
     * @see EnchantmentHelper#has(ItemStack, DataComponentType)
     */
    public static boolean has(LivingEntity livingEntity, DataComponentType<?> componentType) {
        MutableBoolean mutableBoolean = new MutableBoolean(false);
        EnchantmentHelper.runIterationOnEquipment(livingEntity,
                (Holder<Enchantment> holder, int enchantmentLevel, EnchantedItemInUse enchantedItemInUse) -> {
                    if (holder.value().effects().has(componentType)) {
                        mutableBoolean.setTrue();
                    }
                });
        return mutableBoolean.booleanValue();
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
