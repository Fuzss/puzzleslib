package fuzs.puzzleslib.api.item.v2;

import fuzs.puzzleslib.api.init.v3.registry.LookupHelper;
import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableFloat;

import java.util.List;

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
     * @return the enchanting power, usually zero for blocks other than bookshelves
     */
    public static float getEnchantPowerBonus(BlockState blockState, Level level, BlockPos blockPos) {
        return ProxyImpl.get().getEnchantPowerBonus(blockState, level, blockPos);
    }

    /**
     * Check if the given enchanted be applied to an item stack via enchanting (in an enchanting table).
     *
     * @param enchantment the enchantment to check
     * @param itemStack   the item stack receiving the enchantment
     * @return is the application allowed
     */
    public static boolean canApplyAtEnchantingTable(Holder<Enchantment> enchantment, ItemStack itemStack) {
        return ProxyImpl.get().canApplyAtEnchantingTable(enchantment, itemStack);
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
     * @param itemStack     the item stack used as the enchantment holder
     * @param entity        the entity used as the enchantment holder
     * @param componentType the enchantment effect component type
     * @return the enchantment value effect bonus
     *
     * @see EnchantmentHelper#runIterationOnItem(ItemStack, EnchantmentHelper.EnchantmentVisitor)
     * @see Enchantment#modifyUnfilteredValue(DataComponentType, RandomSource, int, MutableFloat)
     */
    public static float getUnfilteredValueEffectBonus(ItemStack itemStack, Entity entity, DataComponentType<EnchantmentValueEffect> componentType) {
        MutableFloat mutableFloat = new MutableFloat(0.0F);
        EnchantmentHelper.runIterationOnItem(itemStack, (Holder<Enchantment> holder, int enchantmentLevel) -> {
            holder.value().modifyUnfilteredValue(componentType, entity.getRandom(), enchantmentLevel, mutableFloat);
        });
        return Math.max(0.0F, mutableFloat.floatValue());
    }

    /**
     * @param livingEntity  the entity used as the enchantment holder
     * @param componentType the enchantment effect component type
     * @return the enchantment value effect bonus
     *
     * @see EnchantmentHelper#runIterationOnEquipment(LivingEntity, EnchantmentHelper.EnchantmentInSlotVisitor)
     * @see Enchantment#modifyUnfilteredValue(DataComponentType, RandomSource, int, MutableFloat)
     */
    public static float getUnfilteredValueEffectBonus(LivingEntity livingEntity, DataComponentType<EnchantmentValueEffect> componentType) {
        MutableFloat mutableFloat = new MutableFloat(0.0F);
        EnchantmentHelper.runIterationOnEquipment(livingEntity,
                (Holder<Enchantment> holder, int enchantmentLevel, EnchantedItemInUse enchantedItemInUse) -> {
                    Enchantment enchantment = holder.value();
                    enchantment.modifyUnfilteredValue(componentType,
                            livingEntity.getRandom(),
                            enchantmentLevel,
                            mutableFloat);
                });
        return Math.max(0.0F, mutableFloat.floatValue());
    }

    /**
     * @param serverLevel   the server level for creating the loot context
     * @param itemStack     the item stack used as the enchantment holder
     * @param componentType the enchantment effect component type
     * @return the enchantment value effect bonus
     *
     * @see EnchantmentHelper#runIterationOnItem(ItemStack, EnchantmentHelper.EnchantmentVisitor)
     * @see Enchantment#modifyItemFilteredCount(DataComponentType, ServerLevel, int, ItemStack, MutableFloat)
     */
    public static float getItemFilteredValueEffectBonus(ServerLevel serverLevel, ItemStack itemStack, DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> componentType) {
        MutableFloat mutableFloat = new MutableFloat(0.0F);
        EnchantmentHelper.runIterationOnItem(itemStack, (Holder<Enchantment> holder, int enchantmentLevel) -> {
            holder.value()
                    .modifyItemFilteredCount(componentType, serverLevel, enchantmentLevel, itemStack, mutableFloat);
        });
        return Math.max(0.0F, mutableFloat.floatValue());
    }

    /**
     * @param serverLevel   the server level for creating the loot context
     * @param livingEntity  the entity used as the enchantment holder
     * @param componentType the enchantment effect component type
     * @return the enchantment value effect bonus
     *
     * @see EnchantmentHelper#runIterationOnEquipment(LivingEntity, EnchantmentHelper.EnchantmentInSlotVisitor)
     * @see Enchantment#modifyItemFilteredCount(DataComponentType, ServerLevel, int, ItemStack, MutableFloat)
     */
    public static float getItemFilteredValueEffectBonus(ServerLevel serverLevel, LivingEntity livingEntity, DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> componentType) {
        MutableFloat mutableFloat = new MutableFloat(0.0F);
        EnchantmentHelper.runIterationOnEquipment(livingEntity,
                (Holder<Enchantment> holder, int enchantmentLevel, EnchantedItemInUse enchantedItemInUse) -> {
                    holder.value()
                            .modifyItemFilteredCount(componentType,
                                    serverLevel,
                                    enchantmentLevel,
                                    enchantedItemInUse.itemStack(),
                                    mutableFloat);
                });
        return Math.max(0.0F, mutableFloat.floatValue());
    }

    /**
     * @param serverLevel   the server level for creating the loot context
     * @param itemStack     the item stack used as the enchantment holder
     * @param entity        the entity used as the enchantment holder
     * @param componentType the enchantment effect component type
     * @return the enchantment value effect bonus
     *
     * @see EnchantmentHelper#runIterationOnItem(ItemStack, EnchantmentHelper.EnchantmentVisitor)
     * @see Enchantment#modifyEntityFilteredValue(DataComponentType, ServerLevel, int, ItemStack, Entity,
     *         MutableFloat)
     */
    public static float getEntityFilteredValueEffectBonus(ServerLevel serverLevel, ItemStack itemStack, Entity entity, DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> componentType) {
        MutableFloat mutableFloat = new MutableFloat(0.0F);
        EnchantmentHelper.runIterationOnItem(itemStack, (Holder<Enchantment> holder, int enchantmentLevel) -> {
            holder.value()
                    .modifyEntityFilteredValue(componentType,
                            serverLevel,
                            enchantmentLevel,
                            itemStack,
                            entity,
                            mutableFloat);
        });
        return Math.max(0.0F, mutableFloat.floatValue());
    }

    /**
     * @param serverLevel   the server level for creating the loot context
     * @param livingEntity  the entity used as the enchantment holder
     * @param componentType the enchantment effect component type
     * @return the enchantment value effect bonus
     *
     * @see EnchantmentHelper#runIterationOnEquipment(LivingEntity, EnchantmentHelper.EnchantmentInSlotVisitor)
     * @see Enchantment#modifyEntityFilteredValue(DataComponentType, ServerLevel, int, ItemStack, Entity,
     *         MutableFloat)
     */
    public static float getEntityFilteredValueEffectBonus(ServerLevel serverLevel, LivingEntity livingEntity, DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> componentType) {
        MutableFloat mutableFloat = new MutableFloat(0.0F);
        EnchantmentHelper.runIterationOnEquipment(livingEntity,
                (Holder<Enchantment> holder, int enchantmentLevel, EnchantedItemInUse enchantedItemInUse) -> {
                    holder.value()
                            .modifyEntityFilteredValue(componentType,
                                    serverLevel,
                                    enchantmentLevel,
                                    enchantedItemInUse.itemStack(),
                                    livingEntity,
                                    mutableFloat);
                });
        return Math.max(0.0F, mutableFloat.floatValue());
    }

    /**
     * @param serverLevel   the server level for creating the loot context
     * @param itemStack     the item stack used as the enchantment holder
     * @param entity        the entity used as the enchantment holder
     * @param damageSource  the damage source
     * @param componentType the enchantment effect component type
     * @return the enchantment value effect bonus
     *
     * @see EnchantmentHelper#runIterationOnItem(ItemStack, EnchantmentHelper.EnchantmentVisitor)
     * @see Enchantment#modifyDamageFilteredValue(DataComponentType, ServerLevel, int, ItemStack, Entity,
     *         DamageSource, MutableFloat)
     */
    public static float getDamageFilteredValueEffectBonus(ServerLevel serverLevel, ItemStack itemStack, Entity entity, DamageSource damageSource, DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> componentType) {
        MutableFloat mutableFloat = new MutableFloat(0.0F);
        EnchantmentHelper.runIterationOnItem(itemStack, (Holder<Enchantment> holder, int enchantmentLevel) -> {
            holder.value()
                    .modifyDamageFilteredValue(componentType,
                            serverLevel,
                            enchantmentLevel,
                            itemStack,
                            entity,
                            damageSource,
                            mutableFloat);
        });
        return Math.max(0.0F, mutableFloat.floatValue());
    }

    /**
     * @param serverLevel   the server level for creating the loot context
     * @param livingEntity  the entity used as the enchantment holder
     * @param damageSource  the damage source
     * @param componentType the enchantment effect component type
     * @return the enchantment value effect bonus
     *
     * @see EnchantmentHelper#runIterationOnEquipment(LivingEntity, EnchantmentHelper.EnchantmentInSlotVisitor)
     * @see Enchantment#modifyDamageFilteredValue(DataComponentType, ServerLevel, int, ItemStack, Entity,
     *         DamageSource, MutableFloat)
     */
    public static float getDamageFilteredValueEffectBonus(ServerLevel serverLevel, LivingEntity livingEntity, DamageSource damageSource, DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> componentType) {
        MutableFloat mutableFloat = new MutableFloat(0.0F);
        EnchantmentHelper.runIterationOnEquipment(livingEntity,
                (Holder<Enchantment> holder, int enchantmentLevel, EnchantedItemInUse enchantedItemInUse) -> {
                    holder.value()
                            .modifyDamageFilteredValue(componentType,
                                    serverLevel,
                                    enchantmentLevel,
                                    enchantedItemInUse.itemStack(),
                                    livingEntity,
                                    damageSource,
                                    mutableFloat);
                });
        return Math.max(0.0F, mutableFloat.floatValue());
    }
}
