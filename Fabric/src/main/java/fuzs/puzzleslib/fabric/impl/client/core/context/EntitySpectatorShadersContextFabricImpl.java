package fuzs.puzzleslib.fabric.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.EntitySpectatorShadersContext;
import fuzs.puzzleslib.fabric.api.client.event.v1.registry.EntitySpectatorShaderRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

import java.util.Objects;

public final class EntitySpectatorShadersContextFabricImpl implements EntitySpectatorShadersContext {

    public void registerSpectatorShader(EntityType<?> entityType, ResourceLocation resourceLocation) {
        Objects.requireNonNull(entityType, "entity type is null");
        Objects.requireNonNull(resourceLocation, "resource location is null");
        EntitySpectatorShaderRegistry.INSTANCE.register(entityType, resourceLocation);
    }
}
