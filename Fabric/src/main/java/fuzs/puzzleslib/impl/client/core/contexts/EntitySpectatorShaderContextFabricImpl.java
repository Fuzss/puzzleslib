package fuzs.puzzleslib.impl.client.core.contexts;

import fuzs.puzzleslib.api.client.core.v1.contexts.EntitySpectatorShaderContext;
import fuzs.puzzleslib.api.client.events.v2.EntitySpectatorShaderRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

import java.util.Objects;

public final class EntitySpectatorShaderContextFabricImpl implements EntitySpectatorShaderContext {

    @Override
    public void registerSpectatorShader(ResourceLocation shaderLocation, EntityType<?> object, EntityType<?>... objects) {
        Objects.requireNonNull(shaderLocation, "shader location is null");
        Objects.requireNonNull(object, "entity type is null");
        EntitySpectatorShaderRegistry.INSTANCE.register(object, shaderLocation);
        Objects.requireNonNull(objects, "entity types is null");
        for (EntityType<?> entityType : objects) {
            Objects.requireNonNull(entityType, "entity type is null");
            EntitySpectatorShaderRegistry.INSTANCE.register(entityType, shaderLocation);
        }
    }
}
