package fuzs.puzzleslib.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.EntitySpectatorShaderContext;
import fuzs.puzzleslib.api.client.event.v1.EntitySpectatorShaderRegistry;
import fuzs.puzzleslib.api.core.v1.context.MultiRegistrationContext;
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
