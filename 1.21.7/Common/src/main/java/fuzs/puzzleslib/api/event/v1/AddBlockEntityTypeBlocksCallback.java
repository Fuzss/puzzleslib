package fuzs.puzzleslib.api.event.v1;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.BiConsumer;

@FunctionalInterface
public interface AddBlockEntityTypeBlocksCallback {
    EventInvoker<AddBlockEntityTypeBlocksCallback> EVENT = EventInvoker.lookup(AddBlockEntityTypeBlocksCallback.class);

    /**
     * Runs after block entity types have been registered, allows for manually adding additional supported blocks for
     * that a block entity type.
     *
     * @param consumer register an additional block for a block entity type
     */
    void onAddBlockEntityTypeBlocks(BiConsumer<BlockEntityType<?>, Block> consumer);
}
