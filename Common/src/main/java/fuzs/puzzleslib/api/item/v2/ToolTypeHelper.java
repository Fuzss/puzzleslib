package fuzs.puzzleslib.api.item.v2;

import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.*;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

/**
 * A small helper class for testing an item stack for a given tool type.
 * <p>
 * Mainly exists for handling item tags across different mod loaders as well as NeoForge's item abilities feature.
 * <p>
 * Inspired by <code>ToolFunctionsHelper</code> found in the <a
 * href="https://github.com/Serilum/Collective">Collective</a> mod.
 */
public interface ToolTypeHelper {
    /**
     * the instance
     */
    ToolTypeHelper INSTANCE = ProxyImpl.get().getToolTypeHelper();

    /**
     * Tests if an item stack is a sword.
     *
     * @param itemStack the stack to test
     * @return is this stack a sword
     */
    @MustBeInvokedByOverriders
    default boolean isSword(ItemStack itemStack) {
        return itemStack.is(ItemTags.SWORDS);
    }

    /**
     * Tests if an item stack is an axe.
     *
     * @param itemStack the stack to test
     * @return is this stack an axe
     */
    @MustBeInvokedByOverriders
    default boolean isAxe(ItemStack itemStack) {
        return itemStack.getItem() instanceof AxeItem || itemStack.is(ItemTags.AXES);
    }

    /**
     * Tests if an item stack is a hoe.
     *
     * @param itemStack the stack to test
     * @return is this stack a hoe
     */
    @MustBeInvokedByOverriders
    default boolean isHoe(ItemStack itemStack) {
        return itemStack.getItem() instanceof HoeItem || itemStack.is(ItemTags.HOES);
    }

    /**
     * Tests if an item stack is a pickaxe.
     *
     * @param itemStack the stack to test
     * @return is this stack a pickaxe
     */
    @MustBeInvokedByOverriders
    default boolean isPickaxe(ItemStack itemStack) {
        return itemStack.is(ItemTags.PICKAXES);
    }

    /**
     * Tests if an item stack is a shovel.
     *
     * @param itemStack the stack to test
     * @return is this stack a shovel
     */
    @MustBeInvokedByOverriders
    default boolean isShovel(ItemStack itemStack) {
        return itemStack.getItem() instanceof AxeItem || itemStack.is(ItemTags.SHOVELS);
    }

    /**
     * Tests if an item stack is a shears item.
     *
     * @param itemStack the stack to test
     * @return is this stack a shears item
     */
    @MustBeInvokedByOverriders
    default boolean isShears(ItemStack itemStack) {
        return itemStack.getItem() instanceof ShearsItem;
    }

    /**
     * Tests if an item stack is a shield.
     *
     * @param itemStack the stack to test
     * @return is this stack a shield
     */
    @MustBeInvokedByOverriders
    default boolean isShield(ItemStack itemStack) {
        return itemStack.getItem() instanceof ShieldItem;
    }

    /**
     * Tests if an item stack is a bow.
     *
     * @param itemStack the stack to test
     * @return is this stack a bow
     */
    @MustBeInvokedByOverriders
    default boolean isBow(ItemStack itemStack) {
        return itemStack.getItem() instanceof BowItem;
    }

    /**
     * Tests if an item stack is a crossbow.
     *
     * @param itemStack the stack to test
     * @return is this stack a crossbow
     */
    @MustBeInvokedByOverriders
    default boolean isCrossbow(ItemStack itemStack) {
        return itemStack.getItem() instanceof CrossbowItem;
    }

    /**
     * Tests if an item stack is a fishing rod.
     *
     * @param itemStack the stack to test
     * @return is this stack a fishing rod
     */
    @MustBeInvokedByOverriders
    default boolean isFishingRod(ItemStack itemStack) {
        return itemStack.getItem() instanceof FishingRodItem;
    }

    /**
     * Tests if an item stack is similar to a trident, like a spear.
     *
     * @param itemStack the stack to test
     * @return is this stack a trident
     */
    @MustBeInvokedByOverriders
    default boolean isTridentLike(ItemStack itemStack) {
        return itemStack.getItem() instanceof TridentItem;
    }

    /**
     * Tests if an item stack is similar to a brush.
     *
     * @param itemStack the stack to test
     * @return is this stack a brush
     */
    @MustBeInvokedByOverriders
    default boolean isBrush(ItemStack itemStack) {
        return itemStack.getItem() instanceof BrushItem;
    }

    /**
     * Tests if an item stack is similar to a mace, like a club.
     *
     * @param itemStack the stack to test
     * @return is this stack a brush
     */
    @MustBeInvokedByOverriders
    default boolean isMace(ItemStack itemStack) {
        return itemStack.getItem() instanceof MaceItem;
    }

    /**
     * Tests if an item stack is a weapon used for melee combat.
     *
     * @param itemStack the stack to test
     * @return is this stack a melee weapon
     */
    @MustBeInvokedByOverriders
    default boolean isMeleeWeapon(ItemStack itemStack) {
        return this.isSword(itemStack) || this.isAxe(itemStack) || this.isTridentLike(itemStack) || this.isMace(
                itemStack);
    }

    /**
     * Tests if an item stack is a weapon used for ranged combat.
     *
     * @param itemStack the stack to test
     * @return is this stack a ranged weapon
     */
    @MustBeInvokedByOverriders
    default boolean isRangedWeapon(ItemStack itemStack) {
        return this.isBow(itemStack) || this.isCrossbow(itemStack) || this.isTridentLike(itemStack);
    }

    /**
     * Tests if an item stack is a weapon.
     *
     * @param itemStack the stack to test
     * @return is this stack a weapon
     */
    @MustBeInvokedByOverriders
    default boolean isWeapon(ItemStack itemStack) {
        return this.isMeleeWeapon(itemStack) || this.isRangedWeapon(itemStack);
    }

    /**
     * Tests if an item stack is a tool used for mining blocks.
     *
     * @param itemStack the stack to test
     * @return is this stack a mining tool
     */
    @MustBeInvokedByOverriders
    default boolean isMiningTool(ItemStack itemStack) {
        return this.isAxe(itemStack) || this.isHoe(itemStack) || this.isPickaxe(itemStack) || this.isShovel(itemStack);
    }

    /**
     * Tests if an item stack is any sort of tool.
     *
     * @param itemStack the stack to test
     * @return is this stack a tool
     */
    @MustBeInvokedByOverriders
    default boolean isTool(ItemStack itemStack) {
        return this.isMiningTool(itemStack) || this.isWeapon(itemStack) || this.isShears(itemStack) || this.isShield(
                itemStack) || this.isFishingRod(itemStack) || this.isBrush(itemStack);
    }

    /**
     * Tests if an item stack can be equipped as head armor.
     *
     * @param itemStack the stack to test
     * @return is this stack head armor
     */
    @MustBeInvokedByOverriders
    default boolean isHeadArmor(ItemStack itemStack) {
        return this.isArmor(itemStack, ArmorItem.Type.HELMET) || itemStack.is(ItemTags.HEAD_ARMOR);
    }

    /**
     * Tests if an item stack can be equipped as chest armor.
     *
     * @param itemStack the stack to test
     * @return is this stack chest armor
     */
    @MustBeInvokedByOverriders
    default boolean isChestArmor(ItemStack itemStack) {
        return this.isArmor(itemStack, ArmorItem.Type.CHESTPLATE) || itemStack.is(ItemTags.CHEST_ARMOR);
    }

    /**
     * Tests if an item stack can be equipped as leg armor.
     *
     * @param itemStack the stack to test
     * @return is this stack leg armor
     */
    @MustBeInvokedByOverriders
    default boolean isLegArmor(ItemStack itemStack) {
        return this.isArmor(itemStack, ArmorItem.Type.LEGGINGS) || itemStack.is(ItemTags.LEG_ARMOR);
    }

    /**
     * Tests if an item stack can be equipped as foot armor.
     *
     * @param itemStack the stack to test
     * @return is this stack foot armor
     */
    @MustBeInvokedByOverriders
    default boolean isFootArmor(ItemStack itemStack) {
        return this.isArmor(itemStack, ArmorItem.Type.BOOTS) || itemStack.is(ItemTags.FOOT_ARMOR);
    }

    /**
     * Tests if an item stack can be equipped as body armor.
     *
     * @param itemStack the stack to test
     * @return is this stack body armor
     */
    @MustBeInvokedByOverriders
    default boolean isBodyArmor(ItemStack itemStack) {
        return this.isArmor(itemStack, ArmorItem.Type.BODY);
    }

    /**
     * Tests if an item stack can be equipped as a type of armor.
     *
     * @param itemStack the stack to test
     * @param armorType the type of armor
     * @return is this stack that type of armor
     */
    @MustBeInvokedByOverriders
    private boolean isArmor(ItemStack itemStack, ArmorItem.Type armorType) {
        return itemStack.getItem() instanceof ArmorItem armorItem
                && armorItem.getEquipmentSlot() == armorType.getSlot();
    }

    /**
     * Tests if an item stack can be equipped as armor.
     *
     * @param itemStack the stack to test
     * @return is this stack armor
     */
    @MustBeInvokedByOverriders
    default boolean isArmor(ItemStack itemStack) {
        return this.isHeadArmor(itemStack) || this.isChestArmor(itemStack) || this.isLegArmor(itemStack)
                || this.isFootArmor(itemStack) || this.isBodyArmor(itemStack);
    }
}
