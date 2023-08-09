package fuzs.puzzleslib.mixin.accessor;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Item.class)
public interface ItemAccessor {

    @Nullable
    @Accessor("category")
    void puzzleslib$setCategory(CreativeModeTab category);
}
