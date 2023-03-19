package fuzs.puzzleslib.impl.core.context;

import fuzs.puzzleslib.api.core.v1.context.FlammableBlocksContext;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.world.level.block.Block;

import java.util.Objects;

public final class FlammableBlocksContextFabricImpl implements FlammableBlocksContext {

    @Override
    public void registerFlammable(int encouragement, int flammability, Block object, Block... objects) {
        if (encouragement <= 0) throw new IllegalArgumentException("encouragement must be greater than 0");
        if (flammability <= 0) throw new IllegalArgumentException("flammability must be greater than 0");
        Objects.requireNonNull(object, "block is null");
        // flammability == burn, encouragement == spread
        FlammableBlockRegistry.getDefaultInstance().add(object, flammability, encouragement);
        Objects.requireNonNull(objects, "blocks is null");
        for (Block block : objects) {
            Objects.requireNonNull(block, "block is null");
            // flammability == burn, encouragement == spread
            FlammableBlockRegistry.getDefaultInstance().add(block, flammability, encouragement);
        }
    }
}
