package fuzs.puzzleslib.fabric.impl.client.event;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import fuzs.puzzleslib.fabric.api.client.event.v1.SkullRendererRegistry;
import fuzs.puzzleslib.api.client.init.v1.SkullRenderersFactory;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.world.level.block.SkullBlock;

import java.util.Objects;
import java.util.Set;

public final class SkullRendererRegistryImpl implements SkullRendererRegistry {
    private static final Set<SkullRenderersFactory> FACTORIES = Sets.newHashSet();

    @Override
    public void register(SkullRenderersFactory factory) {
        Objects.requireNonNull(factory, "factory is null");
        FACTORIES.add(factory);
    }

    public static void addAll(EntityModelSet entityModelSet, ImmutableMap.Builder<SkullBlock.Type, SkullModelBase> builder) {
        for (SkullRenderersFactory factory : FACTORIES) {
            factory.createSkullRenderers(entityModelSet, builder::put);
        }
    }
}
