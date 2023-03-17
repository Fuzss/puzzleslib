package fuzs.puzzleslib.impl.client.events;

import com.google.common.collect.Maps;
import fuzs.puzzleslib.api.client.events.v2.EntitySpectatorShaderRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class EntitySpectatorShaderRegistryImpl implements EntitySpectatorShaderRegistry {
    private static final Map<EntityType<?>, ResourceLocation> SHADER_LOCATIONS = Maps.newHashMap();

    @Override
    public void register(EntityType<?> entityType, ResourceLocation shaderLocation) {
        Objects.requireNonNull(entityType, "entity type is null");
        Objects.requireNonNull(shaderLocation, "shader location is null");
        SHADER_LOCATIONS.put(entityType, shaderLocation);
    }

    public static Optional<ResourceLocation> getEntityShader(@Nullable Entity entity) {
        if (entity != null && SHADER_LOCATIONS.containsKey(entity.getType())) {
            return Optional.of(SHADER_LOCATIONS.get(entity.getType()));
        }
        return Optional.empty();
    }
}
