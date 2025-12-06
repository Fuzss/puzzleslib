package fuzs.puzzleslib.fabric.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.EntitySpectatorShadersContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class EntitySpectatorShadersContextFabricImpl implements EntitySpectatorShadersContext {
    private static final Map<EntityType<?>, ResourceLocation> ENTITY_SPECTATOR_SHADERS = new LinkedHashMap<>();

    @Override
    public void registerSpectatorShader(EntityType<?> entityType, ResourceLocation resourceLocation) {
        Objects.requireNonNull(entityType, "entity type is null");
        Objects.requireNonNull(resourceLocation, "shader location is null");
        ENTITY_SPECTATOR_SHADERS.put(entityType, resourceLocation);
    }

    public static Optional<ResourceLocation> getEntityShader(@Nullable Entity entity) {
        if (entity != null && ENTITY_SPECTATOR_SHADERS.containsKey(entity.getType())) {
            return Optional.of(ENTITY_SPECTATOR_SHADERS.get(entity.getType()));
        } else {
            return Optional.empty();
        }
    }
}
