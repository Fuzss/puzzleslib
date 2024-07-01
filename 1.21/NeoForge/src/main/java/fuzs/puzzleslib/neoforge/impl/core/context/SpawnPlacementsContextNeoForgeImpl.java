package fuzs.puzzleslib.neoforge.impl.core.context;

import fuzs.puzzleslib.api.core.v1.context.SpawnPlacementsContext;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.event.entity.SpawnPlacementRegisterEvent;

import java.util.Objects;

public record SpawnPlacementsContextNeoForgeImpl(SpawnPlacementRegisterEvent evt) implements SpawnPlacementsContext {

    @Override
    public <T extends Mob> void registerSpawnPlacement(EntityType<T> entityType, SpawnPlacementType location, Heightmap.Types heightmap, SpawnPlacements.SpawnPredicate<T> spawnPredicate) {
        Objects.requireNonNull(entityType, "entity type is null");
        Objects.requireNonNull(location, "location is null");
        Objects.requireNonNull(heightmap, "heightmap is null");
        Objects.requireNonNull(spawnPredicate, "spawnPredicate is null");
        this.evt.register(entityType, location, heightmap, spawnPredicate, SpawnPlacementRegisterEvent.Operation.REPLACE);
    }
}
