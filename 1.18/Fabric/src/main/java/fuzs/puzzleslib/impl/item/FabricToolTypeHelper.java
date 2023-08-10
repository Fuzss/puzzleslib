package fuzs.puzzleslib.impl.item;

import fuzs.puzzleslib.api.item.v2.ToolTypeHelper;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.world.item.ItemStack;

public final class FabricToolTypeHelper implements ToolTypeHelper {

    @Override
    public boolean isSword(ItemStack stack) {
        return ToolTypeHelper.super.isSword(stack) || stack.is(ConventionalItemTags.SWORDS);
    }

    @Override
    public boolean isAxe(ItemStack stack) {
        return ToolTypeHelper.super.isAxe(stack) || stack.is(ConventionalItemTags.AXES);
    }

    @Override
    public boolean isHoe(ItemStack stack) {
        return ToolTypeHelper.super.isHoe(stack) || stack.is(ConventionalItemTags.HOES);
    }

    @Override
    public boolean isPickaxe(ItemStack stack) {
        return ToolTypeHelper.super.isPickaxe(stack) || stack.is(ConventionalItemTags.PICKAXES);
    }

    @Override
    public boolean isShovel(ItemStack stack) {
        return ToolTypeHelper.super.isShovel(stack) || stack.is(ConventionalItemTags.SHOVELS);
    }

    @Override
    public boolean isShears(ItemStack stack) {
        return ToolTypeHelper.super.isShears(stack) || stack.is(ConventionalItemTags.SHEARS);
    }

    @Override
    public boolean isShield(ItemStack stack) {
        return ToolTypeHelper.super.isShield(stack) || stack.is(ConventionalItemTags.SHIELDS);
    }

    @Override
    public boolean isBow(ItemStack stack) {
        return ToolTypeHelper.super.isBow(stack) || stack.is(ConventionalItemTags.BOWS);
    }

    @Override
    public boolean isTrident(ItemStack stack) {
        return ToolTypeHelper.super.isTrident(stack) || stack.is(ConventionalItemTags.SPEARS);
    }
}
