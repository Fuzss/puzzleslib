package fuzs.puzzleslib.neoforge.impl.item;

import fuzs.puzzleslib.api.item.v2.ToolTypeHelper;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.ToolActions;

public final class NeoForgeToolTypeHelper implements ToolTypeHelper {

    @Override
    public boolean isSword(ItemStack stack) {
        return ToolTypeHelper.super.isSword(stack) || stack.canPerformAction(ToolActions.SWORD_SWEEP);
    }

    @Override
    public boolean isAxe(ItemStack stack) {
        return ToolTypeHelper.super.isAxe(stack) || stack.canPerformAction(ToolActions.AXE_DIG);
    }

    @Override
    public boolean isHoe(ItemStack stack) {
        return ToolTypeHelper.super.isHoe(stack) || stack.canPerformAction(ToolActions.HOE_DIG);
    }

    @Override
    public boolean isPickaxe(ItemStack stack) {
        return ToolTypeHelper.super.isPickaxe(stack) || stack.canPerformAction(ToolActions.PICKAXE_DIG);
    }

    @Override
    public boolean isShovel(ItemStack stack) {
        return ToolTypeHelper.super.isShovel(stack) || stack.canPerformAction(ToolActions.SHOVEL_DIG);
    }

    @Override
    public boolean isShears(ItemStack stack) {
        return ToolTypeHelper.super.isShears(stack) || stack.is(Tags.Items.SHEARS) || stack.canPerformAction(ToolActions.SHEARS_DIG);
    }

    @Override
    public boolean isShield(ItemStack stack) {
        return ToolTypeHelper.super.isShield(stack) || stack.is(Tags.Items.TOOLS_SHIELDS) || stack.canPerformAction(ToolActions.SHIELD_BLOCK);
    }

    @Override
    public boolean isBow(ItemStack stack) {
        return ToolTypeHelper.super.isBow(stack) || stack.is(Tags.Items.TOOLS_BOWS);
    }

    @Override
    public boolean isCrossbow(ItemStack stack) {
        return ToolTypeHelper.super.isCrossbow(stack) || stack.is(Tags.Items.TOOLS_CROSSBOWS);
    }

    @Override
    public boolean isFishingRod(ItemStack stack) {
        return ToolTypeHelper.super.isFishingRod(stack) || stack.is(Tags.Items.TOOLS_FISHING_RODS) || stack.canPerformAction(ToolActions.FISHING_ROD_CAST);
    }

    @Override
    public boolean isTridentLike(ItemStack stack) {
        return ToolTypeHelper.super.isTridentLike(stack) || stack.is(Tags.Items.TOOLS_TRIDENTS);
    }
}
