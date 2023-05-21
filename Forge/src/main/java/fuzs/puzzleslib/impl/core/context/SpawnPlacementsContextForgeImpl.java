package fuzs.puzzleslib.impl.core.context;

import fuzs.puzzleslib.api.core.v1.context.SpawnPlacementsContext;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;

public record SpawnPlacementsContextForgeImpl(SpawnPlacementRegisterEvent evt) implements SpawnPlacementsContext {

    @Override
    public <T extends Mob> void registerSpawnPlacement(EntityType<T> entityType, SpawnPlacements.Type location, Heightmap.Types heightmap, SpawnPlacements.SpawnPredicate<T> spawnPredicate) {
        this.evt.register(entityType, location, heightmap, spawnPredicate, SpawnPlacementRegisterEvent.Operation.REPLACE);
    }
}
