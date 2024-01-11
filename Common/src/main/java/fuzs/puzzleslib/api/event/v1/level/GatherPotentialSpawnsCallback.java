package fuzs.puzzleslib.api.event.v1.level;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.List;

@FunctionalInterface
public interface GatherPotentialSpawnsCallback {
    EventInvoker<GatherPotentialSpawnsCallback> EVENT = EventInvoker.lookup(GatherPotentialSpawnsCallback.class);

    /**
     * Fires when building a list of all possible entities that can spawn at the specified location.
     *
     * @param level            the current level instance
     * @param structureManager the structure manager, used for applying {@link Structure#spawnOverrides()}
     * @param generator        the chunk generator for calling {@link ChunkGenerator#getMobsAt(Holder, StructureManager, MobCategory, BlockPos)}
     * @param category         the mob category to retrieve potential spawns for
     * @param pos              the block position the spawn attempt is made at
     * @param mobsAt           the vanilla list of mobs available for the given position
     */
    void onGatherPotentialSpawns(ServerLevel level, StructureManager structureManager, ChunkGenerator generator, MobCategory category, BlockPos pos, List<MobSpawnSettings.SpawnerData> mobsAt);
}
