package fuzs.puzzleslib.api.client.event.v1.renderer;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResultHolder;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface ExtractBlockOutlineCallback {
    EventInvoker<ExtractBlockOutlineCallback> EVENT = EventInvoker.lookup(ExtractBlockOutlineCallback.class);

    /**
     * Called when the block highlight outline for the current hit result is about to be submitted.
     *
     * @param clientLevel      the current client level
     * @param blockPos         the block position from the hit result
     * @param blockState       the block state from the hit result
     * @param hitResult        the hit result to render the outline for
     * @param collisionContext the collision context for the camera entity
     * @return <ul>
     *         <li>{@link EventResultHolder#interrupt(Object)} to pass a custom {@link VoxelShape}; or potentially {@link null} to prevent any outline from rendering</li>
     *         <li>{@link EventResultHolder#pass()} to allow vanilla outline rendering to happen without changes</li>
     *         </ul>
     */
    EventResultHolder<@Nullable VoxelShape> onExtractBlockOutline(ClientLevel clientLevel, BlockPos blockPos, BlockState blockState, BlockHitResult hitResult, CollisionContext collisionContext);
}
