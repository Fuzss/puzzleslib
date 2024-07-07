package fuzs.puzzleslib.neoforge.impl.item;

import fuzs.puzzleslib.api.item.v2.ToolTypeHelper;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.Tags;

public final class NeoForgeToolTypeHelper implements ToolTypeHelper {

    @Override
    public boolean isSword(ItemStack itemStack) {
        return ToolTypeHelper.super.isSword(itemStack) || itemStack.canPerformAction(ItemAbilities.SWORD_SWEEP);
    }

    @Override
    public boolean isAxe(ItemStack itemStack) {
        return ToolTypeHelper.super.isAxe(itemStack) || itemStack.canPerformAction(ItemAbilities.AXE_DIG);
    }

    @Override
    public boolean isHoe(ItemStack itemStack) {
        return ToolTypeHelper.super.isHoe(itemStack) || itemStack.canPerformAction(ItemAbilities.HOE_DIG);
    }

    @Override
    public boolean isPickaxe(ItemStack itemStack) {
        return ToolTypeHelper.super.isPickaxe(itemStack) || itemStack.canPerformAction(ItemAbilities.PICKAXE_DIG);
    }

    @Override
    public boolean isShovel(ItemStack itemStack) {
        return ToolTypeHelper.super.isShovel(itemStack) || itemStack.canPerformAction(ItemAbilities.SHOVEL_DIG);
    }

    @Override
    public boolean isShears(ItemStack itemStack) {
        return ToolTypeHelper.super.isShears(itemStack) || itemStack.is(Tags.Items.TOOLS_SHEAR) || itemStack.canPerformAction(
                ItemAbilities.SHEARS_DIG);
    }

    @Override
    public boolean isShield(ItemStack itemStack) {
        return ToolTypeHelper.super.isShield(itemStack) || itemStack.is(Tags.Items.TOOLS_SHIELD) || itemStack.canPerformAction(ItemAbilities.SHIELD_BLOCK);
    }

    @Override
    public boolean isBow(ItemStack itemStack) {
        return ToolTypeHelper.super.isBow(itemStack) || itemStack.is(Tags.Items.TOOLS_BOW);
    }

    @Override
    public boolean isCrossbow(ItemStack itemStack) {
        return ToolTypeHelper.super.isCrossbow(itemStack) || itemStack.is(Tags.Items.TOOLS_CROSSBOW);
    }

    @Override
    public boolean isFishingRod(ItemStack itemStack) {
        return ToolTypeHelper.super.isFishingRod(itemStack) || itemStack.is(Tags.Items.TOOLS_FISHING_ROD) || itemStack.canPerformAction(ItemAbilities.FISHING_ROD_CAST);
    }

    @Override
    public boolean isTridentLike(ItemStack itemStack) {
        return ToolTypeHelper.super.isTridentLike(itemStack) || itemStack.is(Tags.Items.TOOLS_SPEAR) || itemStack.canPerformAction(ItemAbilities.TRIDENT_THROW);
    }

    @Override
    public boolean isBrush(ItemStack itemStack) {
        return ToolTypeHelper.super.isBrush(itemStack) || itemStack.is(Tags.Items.TOOLS_BRUSH) || itemStack.canPerformAction(ItemAbilities.BRUSH_BRUSH);
    }

    @Override
    public boolean isMace(ItemStack itemStack) {
        return ToolTypeHelper.super.isMace(itemStack) || itemStack.is(Tags.Items.TOOLS_MACE);
    }

    @Override
    public boolean isTool(ItemStack itemStack) {
        return ToolTypeHelper.super.isTool(itemStack) || itemStack.is(Tags.Items.TOOLS);
    }

    @Override
    public boolean isArmor(ItemStack itemStack) {
        return ToolTypeHelper.super.isArmor(itemStack) || itemStack.is(Tags.Items.ARMORS);
    }
}
