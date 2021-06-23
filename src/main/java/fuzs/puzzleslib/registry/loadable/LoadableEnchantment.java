package fuzs.puzzleslib.registry.loadable;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

/**
 * a basic enchantment extension which can be unloaded depending on a configuration state
 */
@SuppressWarnings("NullableProblems")
public abstract class LoadableEnchantment extends Enchantment {

    /**
     * vanilla constructor for passing through values
     * @param rarityIn rarity
     * @param typeIn type of tools this can be put on
     * @param slots slot this can activate in
     */
    public LoadableEnchantment(Rarity rarityIn, EnchantmentType typeIn, EquipmentSlotType[] slots) {

        super(rarityIn, typeIn, slots);
    }

    /**
     * @return is this enchantment enabled
     */
    protected abstract boolean isEnabled();

    @Override
    public boolean canEnchant(ItemStack stack) {

        return this.isEnabled() && super.canEnchant(stack);
    }

    @Override
    public boolean isTradeable() {

        return this.isEnabled() && super.isTradeable();
    }

    @Override
    public boolean isDiscoverable() {

        return this.isEnabled() && super.isDiscoverable();
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {

        return this.isEnabled() && super.canApplyAtEnchantingTable(stack);
    }

    @Override
    public boolean isAllowedOnBooks() {

        return this.isEnabled() && super.isAllowedOnBooks();
    }

}
