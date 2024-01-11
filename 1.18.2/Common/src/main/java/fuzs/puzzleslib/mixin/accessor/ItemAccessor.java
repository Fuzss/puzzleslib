package fuzs.puzzleslib.mixin.accessor;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * This mixin class doesn't seem to work when included in the <code>puzzleslib.common.mixins.json</code> file,
 * so moving it to mod loader specific subprojects seems to be a workaround for now.
 */
@Mixin(Item.class)
public interface ItemAccessor {

    @Accessor("category")
    @Mutable
    void puzzleslib$setCategory(@Nullable CreativeModeTab category);
}
