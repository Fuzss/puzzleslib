package fuzs.puzzleslib.registry.loadable;

import fuzs.puzzleslib.registry.entry.ModEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

/**
 * @deprecated renamed to {@link ModEnchantment}
 */
@Deprecated
public abstract class LoadableEnchantment extends ModEnchantment {

    public LoadableEnchantment(Rarity rarityIn, EnchantmentType typeIn, EquipmentSlotType[] slots) {

        super(rarityIn, typeIn, slots);
    }

}
