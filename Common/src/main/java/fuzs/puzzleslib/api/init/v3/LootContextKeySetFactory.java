package fuzs.puzzleslib.api.init.v3;

import fuzs.puzzleslib.api.init.v3.registry.ContentRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.context.ContextKeySet;

import java.util.function.Consumer;

/**
 * Helper class for creating and registering a {@link ContextKeySet ContextKeySets}.
 */
@Deprecated(forRemoval = true)
public final class LootContextKeySetFactory {

    private LootContextKeySetFactory() {
        // NO-OP
    }

    /**
     * Creates and registers a new {@link ContextKeySet}.
     *
     * @param resourceLocation the resource location for the registry
     * @param builderConsumer  the consumer for configuring the builder
     * @return the created context key set
     */
    public static ContextKeySet registerContextKeySet(ResourceLocation resourceLocation, Consumer<ContextKeySet.Builder> builderConsumer) {
        return ContentRegistrationHelper.registerContextKeySet(resourceLocation, builderConsumer);
    }
}
