package fuzs.puzzleslib.impl.core.contexts;

import fuzs.puzzleslib.api.core.v1.contexts.FlammableBlocksContext;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.world.level.block.Block;

import java.util.Objects;

public final class FlammableBlocksContextFabricImpl implements FlammableBlocksContext {

    @Override
    public void registerFlammable(int encouragement, int flammability, Block... blocks) {
        Objects.requireNonNull(blocks, "blocks is null");
        for (Block block : blocks) {
            Objects.requireNonNull(block, "block is null");
            // flammability == burn, encouragement == spread
            FlammableBlockRegistry.getDefaultInstance().add(block, flammability, encouragement);
        }
    }
}
