package fuzs.puzzleslib.api.item.v2;

import fuzs.puzzleslib.mixin.accessor.ItemAccessor;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.Collection;

@FunctionalInterface
public interface DisplayItemsOutput {

    void accept(ItemStack stack);

    default void accept(ItemLike itemLike) {
        this.accept(new ItemStack(itemLike));
    }

    default void acceptAll(Collection<ItemStack> collection) {
        collection.forEach(this::accept);
    }

    static DisplayItemsOutput setItemCategory(CreativeModeTab tab) {
        return (ItemStack stack) -> {
            if (stack.getItem().getItemCategory() == null) {
                ((ItemAccessor) stack.getItem()).puzzleslib$setCategory(tab);
            }
        };
    }
}
