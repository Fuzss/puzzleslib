package fuzs.puzzleslib.api.item.v2;

import com.google.common.base.Suppliers;
import com.google.common.collect.Maps;
import fuzs.puzzleslib.impl.item.TierImpl;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * A class containing factory methods for default {@link Tier} and {@link ArmorMaterial} implementations.
 */
@Deprecated
public final class ItemEquipmentFactories {
    private static final ArmorItem.Type[] ARMOR_TYPES = {
            ArmorItem.Type.BOOTS,
            ArmorItem.Type.LEGGINGS,
            ArmorItem.Type.CHESTPLATE,
            ArmorItem.Type.HELMET,
            ArmorItem.Type.BODY
    };

    private ItemEquipmentFactories() {
        // NO-OP
    }

    /**
     * Creates a new {@link Tier}.
     *
     * @param miningLevel       the mining level of the tier, wood / gold is 0, stone is 1, iron is 2, diamond is 3,
     *                          netherite is 4
     * @param itemDurability    tool durability for this tier
     * @param miningSpeed       speed for mining blocks, wood is 2, gold is 12, stone is 4, iron is 6, diamond is 8,
     *                          netherite is 9
     * @param attackDamageBonus attack damage bonus for added on top of base item damage; this mostly depends on the
     *                          specific item, as the base value is different for each one since Minecraft 1.9
     * @param enchantability    enchantment value, wood is 15, gold is 22, stone is 5, iron is 14, diamond is 14,
     *                          netherite is 15
     * @param repairIngredient  the repair material used in an anvil for restoring item durability
     * @return the new {@link Tier}
     */
    public static Tier registerTier(int miningLevel, int itemDurability, float miningSpeed, float attackDamageBonus, int enchantability, Supplier<Ingredient> repairIngredient) {
        return registerTier(getVanillaMiningLevelBlockTag(miningLevel),
                itemDurability,
                miningSpeed,
                attackDamageBonus,
                enchantability,
                repairIngredient
        );
    }

    /**
     * Converts the legacy mining level value to corresponding block tag.
     * <p>
     * Supported values are as follows:
     * <ul>
     *     <li>0: Wood / Gold</li>
     *     <li>1: Stone</li>
     *     <li>2: Iron</li>
     *     <li>3: Diamond</li>
     *     <li>4: Netherite</li>
     * </ul>
     *
     * @param miningLevel legacy mining level value
     * @return incorrect for tool block tag
     */
    public static TagKey<Block> getVanillaMiningLevelBlockTag(int miningLevel) {
        return switch (miningLevel) {
            case 0 -> BlockTags.INCORRECT_FOR_WOODEN_TOOL;
            case 1 -> BlockTags.INCORRECT_FOR_STONE_TOOL;
            case 2 -> BlockTags.INCORRECT_FOR_IRON_TOOL;
            case 3 -> BlockTags.INCORRECT_FOR_DIAMOND_TOOL;
            case 4 -> BlockTags.INCORRECT_FOR_NETHERITE_TOOL;
            default -> throw new IllegalArgumentException("Unsupported mining level: " + miningLevel);
        };
    }

    /**
     * Creates a new {@link Tier}.
     *
     * @param incorrectBlocksForDrops the mining level of the tier as a block tag
     * @param itemDurability          tool durability for this tier
     * @param miningSpeed             speed for mining blocks, wood is 2, gold is 12, stone is 4, iron is 6, diamond is
     *                                8, netherite is 9
     * @param attackDamageBonus       attack damage bonus for added on top of base item damage; this mostly depends on
     *                                the specific item, as the base value is different for each one since Minecraft
     *                                1.9
     * @param enchantability          enchantment value, wood is 15, gold is 22, stone is 5, iron is 14, diamond is 14,
     *                                netherite is 15
     * @param repairIngredient        the repair material used in an anvil for restoring item durability
     * @return the new {@link Tier}
     */
    public static Tier registerTier(TagKey<Block> incorrectBlocksForDrops, int itemDurability, float miningSpeed, float attackDamageBonus, int enchantability, Supplier<Ingredient> repairIngredient) {
        return new TierImpl(incorrectBlocksForDrops,
                itemDurability,
                miningSpeed,
                attackDamageBonus,
                enchantability,
                Suppliers.memoize(repairIngredient::get)
        );
    }

    /**
     * Converts the legacy protection amount values array to an armor type defense values map.
     *
     * @param protectionAmounts protection value for each slot type; order is boots, leggings, chest plate, helmet,
     *                          body
     * @return armor type defense values map
     */
    public static Map<ArmorItem.Type, Integer> toArmorTypeMap(int... protectionAmounts) {
        return toArmorTypeMapWithFallback(0, protectionAmounts);
    }

    /**
     * Converts the legacy protection amount values array to an armor type defense values map.
     *
     * @param protectionAmountFallback fallback value used when armor type index is not present in proved array
     * @param protectionAmounts        protection value for each slot type; order is boots, leggings, chest plate,
     *                                 helmet, body
     * @return armor type defense values map
     */
    public static Map<ArmorItem.Type, Integer> toArmorTypeMapWithFallback(int protectionAmountFallback, int... protectionAmounts) {
        Map<ArmorItem.Type, Integer> map = new EnumMap<>(ArmorItem.Type.class);
        for (int i = 0; i < ARMOR_TYPES.length; i++) {
            map.put(ARMOR_TYPES[i], i < protectionAmounts.length ? protectionAmounts[i] : protectionAmountFallback);
        }

        return Maps.immutableEnumMap(map);
    }
}
