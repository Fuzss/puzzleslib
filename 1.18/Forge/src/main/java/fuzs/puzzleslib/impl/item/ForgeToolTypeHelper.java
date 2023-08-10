package fuzs.puzzleslib.impl.item;

import fuzs.puzzleslib.api.item.v2.ToolTypeHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.ToolActions;

public final class ForgeToolTypeHelper implements ToolTypeHelper {

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
        return ToolTypeHelper.super.isShield(stack) || stack.canPerformAction(ToolActions.SHIELD_BLOCK);
    }
}
