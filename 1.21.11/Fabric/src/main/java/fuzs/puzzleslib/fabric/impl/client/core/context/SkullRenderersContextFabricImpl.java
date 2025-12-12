package fuzs.puzzleslib.fabric.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.SkullRenderersContext;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.object.skull.SkullModelBase;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.SkullBlock;
import org.jspecify.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public final class SkullRenderersContextFabricImpl implements SkullRenderersContext {
    private static final Map<SkullBlock.Type, Function<EntityModelSet, SkullModelBase>> SKULL_MODEL_FACTORIES = new IdentityHashMap<>();

    @Override
    public void registerSkullRenderer(SkullBlock.Type skullBlockType, Identifier textureLocation, Function<EntityModelSet, SkullModelBase> skullModelFactory) {
        Objects.requireNonNull(skullBlockType, "skull block type is null");
        Objects.requireNonNull(textureLocation, "texture location is null");
        Objects.requireNonNull(skullModelFactory, "skull model factory is null");
        SkullBlockRenderer.SKIN_BY_TYPE.put(skullBlockType, textureLocation);
        SKULL_MODEL_FACTORIES.put(skullBlockType, skullModelFactory);
    }

    @Nullable public static SkullModelBase createSkullModel(SkullBlock.Type skullBlockType, EntityModelSet entityModelSet) {
        Function<EntityModelSet, SkullModelBase> skullModelFactory = SKULL_MODEL_FACTORIES.get(skullBlockType);
        return skullModelFactory != null ? skullModelFactory.apply(entityModelSet) : null;
    }
}
