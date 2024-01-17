package fuzs.puzzleslib.fabric.impl.client.core.context;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.client.core.v1.context.EntitySpectatorShaderContext;
import fuzs.puzzleslib.fabric.api.client.event.v1.registry.EntitySpectatorShaderRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

import java.util.Objects;

public final class EntitySpectatorShaderContextFabricImpl implements EntitySpectatorShaderContext {

    @Override
    public void registerSpectatorShader(ResourceLocation shaderLocation, EntityType<?>... entityTypes) {
        Objects.requireNonNull(shaderLocation, "shader location is null");
        Objects.requireNonNull(entityTypes, "entity types is null");
        Preconditions.checkPositionIndex(1, entityTypes.length, "entity types is empty");
        for (EntityType<?> entityType : entityTypes) {
            Objects.requireNonNull(entityType, "entity type is null");
            EntitySpectatorShaderRegistry.INSTANCE.register(entityType, shaderLocation);
        }
    }
}
