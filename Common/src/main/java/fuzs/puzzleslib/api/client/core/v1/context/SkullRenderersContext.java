package fuzs.puzzleslib.api.client.core.v1.context;

import fuzs.puzzleslib.api.client.init.v1.SkullRenderersFactory;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SkullBlock;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * register models for custom {@link net.minecraft.world.level.block.SkullBlock.Type} implementations
 */
@FunctionalInterface
public interface SkullRenderersContext {

    /**
     * add models for specific skull types
     *
     * @param factory factory for the model(s)
     */
    @Deprecated
    void registerSkullRenderer(SkullRenderersFactory factory);

    /**
     * @param skullBlockType    the skull block type
     * @param textureLocation   the texture location, usually for the corresponding entity
     * @param skullModelFactory the skull model factory
     */
    default void registerSkullRenderer(SkullBlock.Type skullBlockType, ResourceLocation textureLocation, Function<EntityModelSet, SkullModelBase> skullModelFactory) {
        Objects.requireNonNull(skullBlockType, "skull block type is null");
        Objects.requireNonNull(textureLocation, "texture location is null");
        Objects.requireNonNull(skullModelFactory, "skull model factory is null");
        // This is fine on NeoForge regarding a ConcurrentModificationException, as the event is fired sequentially.
        SkullBlockRenderer.SKIN_BY_TYPE.put(skullBlockType, textureLocation);
        this.registerSkullRenderer((EntityModelSet entityModelSet, BiConsumer<SkullBlock.Type, SkullModelBase> consumer) -> {
            consumer.accept(skullBlockType, skullModelFactory.apply(entityModelSet));
        });
    }
}
