package fuzs.puzzleslib.fabric.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.EntitySpectatorShadersContext;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jspecify.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class EntitySpectatorShadersContextFabricImpl implements EntitySpectatorShadersContext {
    private static final Map<EntityType<?>, Identifier> ENTITY_SPECTATOR_SHADERS = new LinkedHashMap<>();

    @Override
    public void registerSpectatorShader(EntityType<?> entityType, Identifier identifier) {
        Objects.requireNonNull(entityType, "entity type is null");
        Objects.requireNonNull(identifier, "shader location is null");
        ENTITY_SPECTATOR_SHADERS.put(entityType, identifier);
    }

    public static Optional<Identifier> getEntityShader(@Nullable Entity entity) {
        if (entity != null && ENTITY_SPECTATOR_SHADERS.containsKey(entity.getType())) {
            return Optional.of(ENTITY_SPECTATOR_SHADERS.get(entity.getType()));
        } else {
            return Optional.empty();
        }
    }
}
