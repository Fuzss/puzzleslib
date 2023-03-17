package fuzs.puzzleslib.impl.client.core.contexts;

import fuzs.puzzleslib.api.client.core.v1.contexts.EntitySpectatorShaderContext;
import fuzs.puzzleslib.api.client.events.v2.EntitySpectatorShaderRegistry;
import fuzs.puzzleslib.api.core.v1.contexts.MultiRegistrationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

public final class EntitySpectatorShaderContextFabricImpl implements EntitySpectatorShaderContext, MultiRegistrationContext<EntityType<?>, ResourceLocation> {

    @Override
    public void registerSpectatorShader(ResourceLocation shaderLocation, EntityType<?> object, EntityType<?>... objects) {
        this.register(shaderLocation, object, objects);
    }

    @Override
    public void register(EntityType<?> object, ResourceLocation type) {
        EntitySpectatorShaderRegistry.INSTANCE.register(object, type);
    }
}
