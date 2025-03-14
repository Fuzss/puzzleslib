package fuzs.puzzleslib.neoforge.impl.core.context;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.core.v1.context.FlammableBlocksContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;

import java.util.Objects;

public final class FlammableBlocksContextNeoForgeImpl implements FlammableBlocksContext {

    @Override
    public void registerFlammable(int encouragement, int flammability, Block... blocks) {
        Preconditions.checkArgument(encouragement > 0, "encouragement is negative");
        Preconditions.checkArgument(flammability > 0, "flammability is negative");
        Objects.requireNonNull(blocks, "blocks is null");
        Preconditions.checkState(blocks.length > 0, "blocks is empty");
        for (Block block : blocks) {
            Objects.requireNonNull(block, "block is null");
            ((FireBlock) Blocks.FIRE).setFlammable(block, encouragement, flammability);
        }
    }
}
