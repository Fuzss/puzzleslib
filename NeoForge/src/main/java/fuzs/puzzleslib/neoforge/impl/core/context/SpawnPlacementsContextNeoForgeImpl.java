package fuzs.puzzleslib.neoforge.impl.core.context;

import fuzs.puzzleslib.api.core.v1.context.SpawnPlacementsContext;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

import java.util.Objects;

public record SpawnPlacementsContextNeoForgeImpl(RegisterSpawnPlacementsEvent event) implements SpawnPlacementsContext {

    @Override
    public <T extends Mob> void registerSpawnPlacement(EntityType<T> entityType, SpawnPlacementType spawnPlacementType, Heightmap.Types heightmapType, SpawnPlacements.SpawnPredicate<T> spawnPredicate) {
        Objects.requireNonNull(entityType, "entity type is null");
        Objects.requireNonNull(spawnPlacementType, "location is null");
        Objects.requireNonNull(heightmapType, "heightmap is null");
        Objects.requireNonNull(spawnPredicate, "spawnPredicate is null");
        this.event.register(entityType, spawnPlacementType, heightmapType,
                spawnPredicate,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }
}
