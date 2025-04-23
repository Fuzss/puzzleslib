package fuzs.puzzleslib.fabric.impl.client.core.context;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.client.core.v1.context.EntitySpectatorShadersContext;
import fuzs.puzzleslib.fabric.api.client.event.v1.registry.EntitySpectatorShaderRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

import java.util.Objects;

public final class EntitySpectatorShadersContextFabricImpl implements EntitySpectatorShadersContext {

    @Override
    public void registerSpectatorShader(ResourceLocation resourceLocation, EntityType<?>... entityTypes) {
        Objects.requireNonNull(resourceLocation, "shader location is null");
        Objects.requireNonNull(entityTypes, "entity types is null");
        Preconditions.checkState(entityTypes.length > 0, "entity types is empty");
        for (EntityType<?> entityType : entityTypes) {
            Objects.requireNonNull(entityType, "entity type is null");
            EntitySpectatorShaderRegistry.INSTANCE.register(entityType, resourceLocation);
        }
    }
}
