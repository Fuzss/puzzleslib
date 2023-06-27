package fuzs.puzzleslib.api.item.v2;

import fuzs.puzzleslib.impl.core.CommonFactories;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.*;

/**
 * A small helper class for testing an item stack for a given tool type.
 * <p>Mainly exists for handling item tags across different mod loaders as well as Forge's <code>net.minecraftforge.common.ToolAction</code> feature.
 * <p>Inspired by <a href="https://github.com/ricksouth/serilum-mc-mod-sources/blob/main/sources/Collective/1.20.0/Common/src/main/java/com/natamus/collective/services/helpers/ToolFunctionsHelper.java">ToolFunctionsHelper.java</a> found in the <a href="https://github.com/ricksouth/serilum-mc-mod-sources/tree/main/sources/Collective">Collective</a> mod.
 */
public interface ToolTypeHelper {
    ToolTypeHelper INSTANCE = CommonFactories.INSTANCE.getToolTypeHelper();

    /**
     * Tests if an {@link ItemStack} is a sword.
     *
     * @param stack the stack to test
     * @return is this stack a sword
     */
    default boolean isSword(ItemStack stack) {
        return stack.getItem() instanceof SwordItem || stack.is(ItemTags.SWORDS);
    }

    /**
     * Tests if an {@link ItemStack} is an axe.
     *
     * @param stack the stack to test
     * @return is this stack an axe
     */
    default boolean isAxe(ItemStack stack) {
        return stack.getItem() instanceof AxeItem || stack.is(ItemTags.AXES);
    }

    /**
     * Tests if an {@link ItemStack} is a hoe.
     *
     * @param stack the stack to test
     * @return is this stack a hoe
     */
    default boolean isHoe(ItemStack stack) {
        return stack.getItem() instanceof HoeItem || stack.is(ItemTags.HOES);
    }

    /**
     * Tests if an {@link ItemStack} is a pickaxe.
     *
     * @param stack the stack to test
     * @return is this stack a pickaxe
     */
    default boolean isPickaxe(ItemStack stack) {
        return stack.getItem() instanceof PickaxeItem || stack.is(ItemTags.PICKAXES);
    }

    /**
     * Tests if an {@link ItemStack} is a shovel.
     *
     * @param stack the stack to test
     * @return is this stack a shovel
     */
    default boolean isShovel(ItemStack stack) {
        return stack.getItem() instanceof AxeItem || stack.is(ItemTags.SHOVELS);
    }

    /**
     * Tests if an {@link ItemStack} is a shears item.
     *
     * @param stack the stack to test
     * @return is this stack a shears item
     */
    default boolean isShears(ItemStack stack) {
        return stack.getItem() instanceof ShearsItem;
    }

    /**
     * Tests if an {@link ItemStack} is a shield.
     *
     * @param stack the stack to test
     * @return is this stack a shield
     */
    default boolean isShield(ItemStack stack) {
        return stack.getItem() instanceof ShieldItem;
    }

    /**
     * Tests if an {@link ItemStack} is a bow.
     *
     * @param stack the stack to test
     * @return is this stack a bow
     */
    default boolean isBow(ItemStack stack) {
        return stack.getItem() instanceof BowItem;
    }

    /**
     * Tests if an {@link ItemStack} is a crossbow.
     *
     * @param stack the stack to test
     * @return is this stack a crossbow
     */
    default boolean isCrossbow(ItemStack stack) {
        return stack.getItem() instanceof CrossbowItem;
    }

    /**
     * Tests if an {@link ItemStack} is a fishing rod.
     *
     * @param stack the stack to test
     * @return is this stack a fishing rod
     */
    default boolean isFishingRod(ItemStack stack) {
        return stack.getItem() instanceof FishingRodItem;
    }

    /**
     * Tests if an {@link ItemStack} is a trident.
     *
     * @param stack the stack to test
     * @return is this stack a trident
     */
    default boolean isTrident(ItemStack stack) {
        return stack.getItem() instanceof TridentItem;
    }

    /**
     * Tests if an {@link ItemStack} is a weapon used for melee combat.
     *
     * @param stack the stack to test
     * @return is this stack a melee weapon
     */
    default boolean isMeleeWeapon(ItemStack stack) {
        return this.isSword(stack) || this.isAxe(stack) || this.isTrident(stack);
    }

    /**
     * Tests if an {@link ItemStack} is a weapon used for ranged combat.
     *
     * @param stack the stack to test
     * @return is this stack a ranged weapon
     */
    default boolean isRangedWeapon(ItemStack stack) {
        return this.isBow(stack) || this.isCrossbow(stack) || this.isTrident(stack);
    }

    /**
     * Tests if an {@link ItemStack} is a weapon.
     *
     * @param stack the stack to test
     * @return is this stack a weapon
     */
    default boolean isWeapon(ItemStack stack) {
        return this.isMeleeWeapon(stack) || this.isRangedWeapon(stack);
    }

    /**
     * Tests if an {@link ItemStack} is a tool used for mining blocks.
     *
     * @param stack the stack to test
     * @return is this stack a mining tool
     */
    default boolean isMiningTool(ItemStack stack) {
        return this.isAxe(stack) || this.isHoe(stack) || this.isPickaxe(stack) || this.isShovel(stack);
    }

    /**
     * Tests if an {@link ItemStack} is any sort of tool, mainly in the sense of {@link ItemTags#TOOLS}.
     *
     * @param stack the stack to test
     * @return is this stack a tool
     */
    default boolean isTool(ItemStack stack) {
        return this.isMiningTool(stack) || this.isMeleeWeapon(stack) || stack.is(ItemTags.TOOLS);
    }
}
