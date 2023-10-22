package fuzs.puzzleslib.mixin.accessor;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockItem.class)
public interface BlockItemAccessor {

    @Accessor("block")
    @Mutable
    void diagonalwalls$setBlock(Block block);
}
