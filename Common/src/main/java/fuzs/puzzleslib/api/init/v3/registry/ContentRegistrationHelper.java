package fuzs.puzzleslib.api.init.v3.registry;

import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.event.v1.CommonSetupCallback;
import fuzs.puzzleslib.impl.item.CustomTransmuteRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Contains methods for registering various gameplay content.
 */
public final class ContentRegistrationHelper {

    private ContentRegistrationHelper() {
        // NO-OP
    }

    /**
     * Registers mod-specific recipe serializers for custom transmute recipes.
     *
     * @param registryManager the registry manager instance
     */
    public static void registerTransmuteRecipeSerializers(RegistryManager registryManager) {
        CustomTransmuteRecipe.registerSerializers((String string, Supplier<RecipeSerializer<?>> recipeSerializerSupplier) -> {
            registryManager.register(Registries.RECIPE_SERIALIZER, string, recipeSerializerSupplier);
        });
    }

    /**
     * Registers a new skull block type.
     *
     * @param resourceLocation the name used for the skull block type
     * @return the skull block type
     */
    public static SkullBlock.Type registerSkullBlockType(ResourceLocation resourceLocation) {
        String string = resourceLocation.toString();
        SkullBlock.Type skullBlockType = () -> string;
        CommonSetupCallback.EVENT.register(() -> {
            SkullBlock.Type.TYPES.put(skullBlockType.getSerializedName(), skullBlockType);
        });
        return skullBlockType;
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
            // run this immediately, as the common setup does not run during data generation, but we need this for generating loot tables
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

    private static void registerContextKeySet(ResourceLocation resourceLocation, ContextKeySet contextKeySet) {
        if (LootContextParamSets.REGISTRY.put(resourceLocation, contextKeySet) != null) {
            throw new IllegalStateException("Loot context key set " + resourceLocation + " is already registered");
        }
    }
}
