package fuzs.puzzleslib.api.init.v3;

import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.event.v1.CommonSetupCallback;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.function.Consumer;

/**
 * Helper class for creating and registering a {@link ContextKeySet ContextKeySets}.
 */
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
        ContextKeySet.Builder builder = new ContextKeySet.Builder();
        builderConsumer.accept(builder);
        ContextKeySet contextKeySet = builder.build();
        if (ModLoaderEnvironment.INSTANCE.isDataGeneration()) {
            // run this immediately, as common setup does not run during data generation, but we need this for generating loot tables
            // this can only ever run in a development environment where no other mods conflicting here will be present
            registerContextKeySet(resourceLocation, contextKeySet);
        } else {
            // delay this, as the underlying registry map is not concurrent, possibly leading to issues with other mods on NeoForge
            CommonSetupCallback.EVENT.register(() -> {
                registerContextKeySet(resourceLocation, contextKeySet);
            });
        }
        return contextKeySet;
    }

    static void registerContextKeySet(ResourceLocation resourceLocation, ContextKeySet contextKeySet) {
        if (LootContextParamSets.REGISTRY.put(resourceLocation, contextKeySet) != null) {
            throw new IllegalStateException("Loot context key set " + resourceLocation + " is already registered");
        }
    }
}
