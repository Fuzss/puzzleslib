package fuzs.puzzleslib.mixin.accessor;

import net.minecraft.core.NonNullList;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SimpleContainer.class)
public interface SimpleContainerAccessor {

    @Accessor("size")
    @Mutable
    void puzzleslib$setSize(int size);

    @Accessor("items")
    @Mutable
    void puzzleslib$setItems(NonNullList<ItemStack> items);
}
