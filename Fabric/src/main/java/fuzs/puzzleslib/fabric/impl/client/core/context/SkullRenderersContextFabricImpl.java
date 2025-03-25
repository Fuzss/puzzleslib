package fuzs.puzzleslib.fabric.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.SkullRenderersContext;
import fuzs.puzzleslib.fabric.api.client.event.v1.registry.SkullRendererRegistry;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SkullBlock;

import java.util.Objects;
import java.util.function.Function;

public final class SkullRenderersContextFabricImpl implements SkullRenderersContext {

    @Override
    public void registerSkullRenderer(SkullBlock.Type skullBlockType, ResourceLocation textureLocation, Function<EntityModelSet, SkullModelBase> skullModelFactory) {
        Objects.requireNonNull(skullBlockType, "skull block type is null");
        Objects.requireNonNull(textureLocation, "texture location is null");
        Objects.requireNonNull(skullModelFactory, "skull model factory is null");
        SkullBlockRenderer.SKIN_BY_TYPE.put(skullBlockType, textureLocation);
        SkullRendererRegistry.INSTANCE.register(skullBlockType, skullModelFactory);
    }
}
