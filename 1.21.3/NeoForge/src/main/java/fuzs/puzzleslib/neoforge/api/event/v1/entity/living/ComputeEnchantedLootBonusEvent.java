package fuzs.puzzleslib.neoforge.api.event.v1.entity.living;

import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Called just before a {@link LivingEntity} drops all its loot for determining the level of a loot bonus enchantment
 * such as {@link net.minecraft.world.item.enchantment.Enchantments#LOOTING} that should be applied to the drops.
 * <p>
 * Specifically the event allows for controlling the enchantment level when applying the:
 * <ul>
 *     <li>loot item function <code>minecraft:enchanted_count_increase</code></li>
 *     <li>loot item condition <code>minecraft:random_chance_with_enchanted_bonus</code></li>
 *     <li>enchantment effect component <code>minecraft:equipment_drops</code></li>
 * </ul>
 * <p>
 * This event is fired on the {@link NeoForge#EVENT_BUS}.
 */
public class ComputeEnchantedLootBonusEvent extends LivingEvent {
    @Nullable
    private final DamageSource damageSource;
    private final Holder<Enchantment> enchantment;
    private int enchantmentLevel;

    @ApiStatus.Internal
    public ComputeEnchantedLootBonusEvent(LivingEntity entity, @Nullable DamageSource damageSource, Holder<Enchantment> enchantment, int enchantmentLevel) {
        super(entity);
        this.damageSource = damageSource;
        this.enchantment = enchantment;
        this.enchantmentLevel = enchantmentLevel;
    }

    @Nullable
    public DamageSource getDamageSource() {
        return this.damageSource;
    }

    public Holder<Enchantment> getEnchantment() {
        return this.enchantment;
    }

    public int getEnchantmentLevel() {
        return this.enchantmentLevel;
    }

    public void setEnchantmentLevel(int enchantmentLevel) {
        this.enchantmentLevel = enchantmentLevel;
    }

    @ApiStatus.Internal
    public static int onComputeEnchantedLootBonus(Holder<Enchantment> enchantment, int enchantmentLevel, LootContext lootContext) {
        Entity entity = lootContext.getOptionalParameter(LootContextParams.THIS_ENTITY);
        if (!(entity instanceof LivingEntity livingEntity)) return enchantmentLevel;
        DamageSource damageSource = lootContext.getOptionalParameter(LootContextParams.DAMAGE_SOURCE);
        return onComputeEnchantedLootBonus(enchantment, enchantmentLevel, livingEntity, damageSource);
    }

    @ApiStatus.Internal
    public static int onComputeEnchantedLootBonus(Holder<Enchantment> enchantment, int enchantmentLevel, LivingEntity livingEntity, @Nullable DamageSource damageSource) {
        return NeoForge.EVENT_BUS.post(
                        new ComputeEnchantedLootBonusEvent(livingEntity, damageSource, enchantment, enchantmentLevel))
                .getEnchantmentLevel();
    }
}
