package fuzs.puzzleslib.neoforge.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.SkullRenderersContext;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.object.skull.SkullModelBase;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.SkullBlock;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

import java.util.Objects;
import java.util.function.Function;

public record SkullRenderersContextNeoForgeImpl(EntityRenderersEvent.CreateSkullModels event) implements SkullRenderersContext {

    @Override
    public void registerSkullRenderer(SkullBlock.Type skullBlockType, Identifier textureLocation, Function<EntityModelSet, SkullModelBase> skullModelFactory) {
        Objects.requireNonNull(skullBlockType, "skull block type is null");
        Objects.requireNonNull(textureLocation, "texture location is null");
        Objects.requireNonNull(skullModelFactory, "skull model factory is null");
        this.event.registerSkullModel(skullBlockType, skullModelFactory, textureLocation);
    }
}
