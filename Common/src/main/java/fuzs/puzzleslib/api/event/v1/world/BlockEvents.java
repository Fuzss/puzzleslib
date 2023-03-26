package fuzs.puzzleslib.api.event.v1.world;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class BlockEvents {
    public static final EventInvoker<FarmlandTrample> FARMLAND_TRAMPLE = EventInvoker.lookup(FarmlandTrample.class);

    private BlockEvents() {

    }

    @FunctionalInterface
    public interface FarmlandTrample {

        /**
         * Fired when an entity falls onto a block of farmland and in the process would trample on it, turning the block into dirt and destroying potential crops.
         *
         * @param level level farmland block is trampled in
         * @param pos farmland block position
         * @param state block state farmland will be converted to after trampling
         * @param fallDistance fall distance of the entity
         * @param entity the entity falling on the farmland block
         * @return {@link EventResult#INTERRUPT} to prevent trampling,
         *         {@link EventResult#PASS} to allow the trampling
         */
        EventResult onFarmlandTrample(Level level, BlockPos pos, BlockState state, float fallDistance, Entity entity);
    }
}
