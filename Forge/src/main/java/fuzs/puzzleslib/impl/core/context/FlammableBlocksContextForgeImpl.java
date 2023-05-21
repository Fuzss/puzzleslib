package fuzs.puzzleslib.impl.core.context;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.core.v1.context.FlammableBlocksContext;
import fuzs.puzzleslib.mixin.accessor.FireBlockForgeAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Objects;

public final class FlammableBlocksContextForgeImpl implements FlammableBlocksContext {

    @Override
    public void registerFlammable(int encouragement, int flammability, Block... blocks) {
        Preconditions.checkArgument(encouragement > 0, "encouragement is negative");
        Preconditions.checkArgument(flammability > 0, "flammability is negative");
        Objects.requireNonNull(blocks, "blocks is null");
        Preconditions.checkPositionIndex(0, blocks.length, "blocks is empty");
        for (Block block : blocks) {
            Objects.requireNonNull(block, "block is null");
            ((FireBlockForgeAccessor) Blocks.FIRE).puzzleslib$setFlammable(block, encouragement, flammability);
        }
    }
}
