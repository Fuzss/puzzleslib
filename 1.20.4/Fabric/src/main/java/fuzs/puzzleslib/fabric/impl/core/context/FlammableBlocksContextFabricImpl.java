package fuzs.puzzleslib.fabric.impl.core.context;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.core.v1.context.FlammableBlocksContext;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.world.level.block.Block;

import java.util.Objects;

public final class FlammableBlocksContextFabricImpl implements FlammableBlocksContext {

    @Override
    public void registerFlammable(int encouragement, int flammability, Block... blocks) {
        Preconditions.checkArgument(encouragement > 0, "encouragement is negative");
        Preconditions.checkArgument(flammability > 0, "flammability is negative");
        Objects.requireNonNull(blocks, "blocks is null");
        Preconditions.checkPositionIndex(1, blocks.length, "blocks is empty");
        for (Block block : blocks) {
            Objects.requireNonNull(block, "block is null");
            // flammability == burn, encouragement == spread
            FlammableBlockRegistry.getDefaultInstance().add(block, flammability, encouragement);
        }
    }
}
