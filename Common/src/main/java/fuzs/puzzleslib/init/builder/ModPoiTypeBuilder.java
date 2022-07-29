package fuzs.puzzleslib.init.builder;

import com.google.common.collect.ImmutableSet;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * we need our own builder instead of passing parameters directly and then handling the builder part in mod loader specific code
 * as we cannot reference arguments directly (the blocks) on Forge,
 * we need to use a {@link java.util.function.Supplier} together with this builder instead
 *
 * @param ticketCount    max amounts of tickets
 * @param searchDistance distance to search for this poi type
 * @param blocks         blocks states valid for this poi type
 */
public record ModPoiTypeBuilder(int ticketCount, int searchDistance, Iterable<BlockState> blocks) {

    /**
     * @param ticketCount    max amounts of tickets
     * @param searchDistance distance to search for this poi type
     * @param blocks         blocks valid for this poi type
     */
    private ModPoiTypeBuilder(int ticketCount, int searchDistance, Block... blocks) {
        this(ticketCount, searchDistance, Stream.of(blocks)
                .mapMulti((Block block, Consumer<BlockState> mapper) -> block.getStateDefinition().getPossibleStates().forEach(mapper))
                .collect(ImmutableSet.toImmutableSet()));
    }

    /**
     * @param ticketCount    max amounts of tickets
     * @param searchDistance distance to search for this poi type
     * @param blocks         blocks states valid for this poi type
     * @return   builder instance
     */
    public static ModPoiTypeBuilder of(int ticketCount, int searchDistance, Iterable<BlockState> blocks) {
        return new ModPoiTypeBuilder(ticketCount, searchDistance, blocks);
    }

    /**
     * @param ticketCount    max amounts of tickets
     * @param searchDistance distance to search for this poi type
     * @param blocks         blocks valid for this poi type
     * @return   builder instance
     */
    public static ModPoiTypeBuilder of(int ticketCount, int searchDistance, Block... blocks) {
        return new ModPoiTypeBuilder(ticketCount, searchDistance, blocks);
    }
}
