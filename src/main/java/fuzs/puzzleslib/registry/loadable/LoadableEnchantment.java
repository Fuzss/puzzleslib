package fuzs.puzzleslib.registry.loadable;

import fuzs.puzzleslib.registry.v2.registryentry.ModEnchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;

/**
 * @deprecated renamed to {@link ModEnchantment}
 */
@Deprecated
public abstract class LoadableEnchantment extends ModEnchantment {

    public LoadableEnchantment(Rarity rarityIn, EnchantmentType typeIn, EquipmentSlotType[] slots) {

        super(rarityIn, typeIn, slots);
    }

}
