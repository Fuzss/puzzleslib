package fuzs.puzzleslib.neoforge.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.EntitySpectatorShadersContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.client.event.RegisterEntitySpectatorShadersEvent;

import java.util.Objects;

public record EntitySpectatorShadersContextNeoForgeImpl(RegisterEntitySpectatorShadersEvent evt) implements EntitySpectatorShadersContext {

    @Override
    public void registerSpectatorShader(EntityType<?> entityType, ResourceLocation resourceLocation) {
        Objects.requireNonNull(entityType, "entity type is null");
        Objects.requireNonNull(resourceLocation, "resource location is null");
        this.evt.register(entityType, resourceLocation);
    }
}
