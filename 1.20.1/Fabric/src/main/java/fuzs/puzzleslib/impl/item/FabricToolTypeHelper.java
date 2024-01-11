package fuzs.puzzleslib.impl.item;

import fuzs.puzzleslib.api.item.v2.ToolTypeHelper;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.world.item.ItemStack;

public final class FabricToolTypeHelper implements ToolTypeHelper {

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
    public boolean isTridentLike(ItemStack stack) {
        return ToolTypeHelper.super.isTridentLike(stack) || stack.is(ConventionalItemTags.SPEARS);
    }
}
