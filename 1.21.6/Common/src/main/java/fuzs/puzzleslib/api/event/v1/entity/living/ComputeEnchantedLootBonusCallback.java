package fuzs.puzzleslib.api.event.v1.entity.living;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.data.MutableInt;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface ComputeEnchantedLootBonusCallback {
    EventInvoker<ComputeEnchantedLootBonusCallback> EVENT = EventInvoker.lookup(ComputeEnchantedLootBonusCallback.class);

    /**
     * Called just before a {@link LivingEntity} drops all its loot for determining the level of a loot bonus
     * enchantment such as {@link net.minecraft.world.item.enchantment.Enchantments#LOOTING} that should be applied to
     * the drops.
     * <p>
     * Specifically the event allows for controlling the enchantment level when applying the:
     * <ul>
     *     <li>loot item function <code>minecraft:enchanted_count_increase</code></li>
     *     <li>loot item condition <code>minecraft:random_chance_with_enchanted_bonus</code></li>
     *     <li>enchantment effect component <code>minecraft:equipment_drops</code></li>
     * </ul>
     *
     * @param livingEntity     the entity that is about to drop all its loot
     * @param damageSource     the damage source that caused the entity to drop its loot
     * @param enchantment      the enchantment
     * @param enchantmentLevel the current enchantment level
     */
    void onComputeEnchantedLootBonus(LivingEntity livingEntity, @Nullable DamageSource damageSource, Holder<Enchantment> enchantment, MutableInt enchantmentLevel);
}
