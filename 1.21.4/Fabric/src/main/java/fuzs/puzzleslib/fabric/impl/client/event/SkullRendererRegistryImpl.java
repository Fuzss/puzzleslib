package fuzs.puzzleslib.fabric.impl.client.event;

import fuzs.puzzleslib.fabric.api.client.event.v1.registry.SkullRendererRegistry;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.world.level.block.SkullBlock;
import org.jetbrains.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public final class SkullRendererRegistryImpl implements SkullRendererRegistry {
    private static final Map<SkullBlock.Type, Function<EntityModelSet, SkullModelBase>> SKULL_MODEL_FACTORIES = new IdentityHashMap<>();

    @Override
    public void register(SkullBlock.Type skullBlockType, Function<EntityModelSet, SkullModelBase> skullModelFactory) {
        Objects.requireNonNull(skullBlockType, "skull block type is null");
        Objects.requireNonNull(skullModelFactory, "skull model factory is null");
        SKULL_MODEL_FACTORIES.put(skullBlockType, skullModelFactory);
    }

    @Nullable
    public static SkullModelBase createSkullModel(SkullBlock.Type skullBlockType, EntityModelSet entityModelSet) {
        Function<EntityModelSet, SkullModelBase> skullModelFactory = SKULL_MODEL_FACTORIES.get(skullBlockType);
        return skullModelFactory != null ? skullModelFactory.apply(entityModelSet) : null;
    }
}
