package fuzs.puzzleslib.fabric.impl.item;

import fuzs.puzzleslib.api.item.v2.ToolTypeHelper;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.world.item.ItemStack;

public final class FabricToolTypeHelper implements ToolTypeHelper {

    @Override
    public boolean isShears(ItemStack itemStack) {
        return ToolTypeHelper.super.isShears(itemStack) || itemStack.is(ConventionalItemTags.SHEAR_TOOLS);
    }

    @Override
    public boolean isShield(ItemStack itemStack) {
        return ToolTypeHelper.super.isShield(itemStack) || itemStack.is(ConventionalItemTags.SHIELD_TOOLS);
    }

    @Override
    public boolean isBow(ItemStack itemStack) {
        return ToolTypeHelper.super.isBow(itemStack) || itemStack.is(ConventionalItemTags.BOW_TOOLS);
    }

    @Override
    public boolean isCrossbow(ItemStack itemStack) {
        return ToolTypeHelper.super.isCrossbow(itemStack) || itemStack.is(ConventionalItemTags.CROSSBOW_TOOLS);
    }

    @Override
    public boolean isFishingRod(ItemStack itemStack) {
        return ToolTypeHelper.super.isFishingRod(itemStack) || itemStack.is(ConventionalItemTags.FISHING_ROD_TOOLS);
    }

    @Override
    public boolean isTridentLike(ItemStack itemStack) {
        return ToolTypeHelper.super.isTridentLike(itemStack) || itemStack.is(ConventionalItemTags.SPEAR_TOOLS);
    }

    @Override
    public boolean isBrush(ItemStack itemStack) {
        return ToolTypeHelper.super.isBrush(itemStack) || itemStack.is(ConventionalItemTags.BRUSH_TOOLS);
    }

    @Override
    public boolean isArmor(ItemStack itemStack) {
        return ToolTypeHelper.super.isArmor(itemStack) || itemStack.is(ConventionalItemTags.ARMORS);
    }

    @Override
    public boolean isMeleeWeapon(ItemStack itemStack) {
        return ToolTypeHelper.super.isMeleeWeapon(itemStack) || itemStack.is(ConventionalItemTags.MELEE_WEAPON_TOOLS);
    }

    @Override
    public boolean isRangedWeapon(ItemStack itemStack) {
        return ToolTypeHelper.super.isRangedWeapon(itemStack) || itemStack.is(ConventionalItemTags.RANGED_WEAPON_TOOLS);
    }

    @Override
    public boolean isMiningTool(ItemStack itemStack) {
        return ToolTypeHelper.super.isMiningTool(itemStack) || itemStack.is(ConventionalItemTags.MINING_TOOL_TOOLS);
    }

    @Override
    public boolean isTool(ItemStack itemStack) {
        return ToolTypeHelper.super.isTool(itemStack) || itemStack.is(ConventionalItemTags.TOOLS);
    }
}
