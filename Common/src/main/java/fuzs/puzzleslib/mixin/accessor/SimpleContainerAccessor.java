package fuzs.puzzleslib.mixin.accessor;

import net.minecraft.core.NonNullList;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SimpleContainer.class)
public interface SimpleContainerAccessor {

    @Accessor("size")
    void puzzleslib$setSize(int size);

    @Accessor("items")
    void puzzleslib$setItems(NonNullList<ItemStack> items);
}
