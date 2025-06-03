package fuzs.puzzleslib.neoforge.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.SkullRenderersContext;
import fuzs.puzzleslib.neoforge.impl.core.context.AbstractNeoForgeContext;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SkullBlock;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

import java.util.Objects;
import java.util.function.Function;

public final class SkullRenderersContextNeoForgeImpl extends AbstractNeoForgeContext implements SkullRenderersContext {

    @Override
    public void registerSkullRenderer(SkullBlock.Type skullBlockType, ResourceLocation textureLocation, Function<EntityModelSet, SkullModelBase> skullModelFactory) {
        Objects.requireNonNull(skullBlockType, "skull block type is null");
        Objects.requireNonNull(textureLocation, "texture location is null");
        Objects.requireNonNull(skullModelFactory, "skull model factory is null");
        this.registerForEvent(FMLClientSetupEvent.class, (FMLClientSetupEvent evt) -> {
            evt.enqueueWork(() -> {
                SkullBlockRenderer.SKIN_BY_TYPE.put(skullBlockType, textureLocation);
            });
        });
        this.registerForEvent(EntityRenderersEvent.CreateSkullModels.class,
                (EntityRenderersEvent.CreateSkullModels evt) -> {
                    evt.registerSkullModel(skullBlockType, skullModelFactory);
                });
    }
}
