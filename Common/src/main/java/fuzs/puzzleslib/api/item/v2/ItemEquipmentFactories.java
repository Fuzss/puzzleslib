package fuzs.puzzleslib.api.item.v2;

import com.google.common.base.Suppliers;
import fuzs.puzzleslib.impl.item.ArmorMaterialImpl;
import fuzs.puzzleslib.impl.item.TierImpl;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Supplier;

/**
 * A class containing factory methods for default {@link Tier} and {@link ArmorMaterial} implementations.
 */
public final class ItemEquipmentFactories {

    private ItemEquipmentFactories() {

    }

    /**
     * Creates a new {@link Tier}.
     *
     * @param miningLevel       the mining level of the tier, wood / gold is 0, stone is 1, iron is 2, diamond is 3, netherite is 4
     * @param itemDurability    tool durability for this tier
     * @param miningSpeed       speed for mining blocks, wood is 2, gold is 12, stone is 4, iron is 6, diamond is 8, netherite is 9
     * @param attackDamageBonus attack damage bonus for added on top of base item damage; this mostly depends on the specific item, as the base value is different for each one since Minecraft 1.9
     * @param enchantability    enchantment value, wood is 15, gold is 22, stone is 5, iron is 14, diamond is 14, netherite is 15
     * @param repairIngredient  the repair material used in an anvil for restoring item durability
     * @return the new {@link Tier}
     */
    public static Tier registerTier(int miningLevel, int itemDurability, float miningSpeed, float attackDamageBonus, int enchantability, Supplier<Ingredient> repairIngredient) {
        return new TierImpl(miningLevel, itemDurability, miningSpeed, attackDamageBonus, enchantability, Suppliers.memoize(repairIngredient::get));
    }

    /**
     * Creates a new {@link ArmorMaterial}.
     *
     * @param name                 name of this material, used for the texture location
     * @param durabilityMultiplier multiplier for internal base durability per slot type
     * @param protectionAmounts    protection value for each slot type, order is boots, leggings, chest plate, helmet
     * @param enchantability       enchantment value, leather is 15, gold is 25, chain is 12, iron is 9, diamond is 10, turtle is 9, netherite is 15
     * @param equipSound           the sound played when putting a piece of armor into the dedicated equipment slot
     * @param toughness            armor toughness value for all slot types of this armor set
     * @param knockbackResistance  knockback resistance value for all slot types of this armor set
     * @param repairIngredient     the repair material used in an anvil for restoring item durability
     * @return the new {@link ArmorMaterial}
     */
    public static ArmorMaterial registerArmorMaterial(ResourceLocation name, int durabilityMultiplier, int[] protectionAmounts, int enchantability, Supplier<SoundEvent> equipSound, float toughness, float knockbackResistance, Supplier<Ingredient> repairIngredient) {
        return new ArmorMaterialImpl(name.toString(), durabilityMultiplier, protectionAmounts, enchantability, Suppliers.memoize(equipSound::get), toughness, knockbackResistance, Suppliers.memoize(repairIngredient::get));
    }
}
