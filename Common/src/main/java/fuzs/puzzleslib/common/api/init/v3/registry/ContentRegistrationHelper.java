package fuzs.puzzleslib.common.api.init.v3.registry;

import fuzs.puzzleslib.common.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.common.api.event.v1.CommonSetupCallback;
import fuzs.puzzleslib.common.impl.core.proxy.ProxyImpl;
import fuzs.puzzleslib.common.impl.item.TransmuteShapedRecipe;
import fuzs.puzzleslib.common.impl.item.TransmuteShapelessRecipe;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.function.Consumer;

/**
 * Contains methods for registering various gameplay content.
 */
public final class ContentRegistrationHelper {
    /**
     * The shaped transmute recipe serializer id that is used during registration.
     */
    public static final String TRANSMUTE_SHAPED_RECIPE_SERIALIZER_ID = "crafting_transmute_shaped";
    /**
     * The shapeless transmute recipe serializer id that is used during registration.
     */
    public static final String TRANSMUTE_SHAPELESS_RECIPE_SERIALIZER_ID = "crafting_transmute_shapeless";

    private ContentRegistrationHelper() {
        // NO-OP
    }

    public static ResourceKey<RecipeSerializer<?>> getTransmuteShapedRecipeSerializer(String modId) {
        return ResourceKey.create(Registries.RECIPE_SERIALIZER,
                Identifier.fromNamespaceAndPath(modId, TRANSMUTE_SHAPED_RECIPE_SERIALIZER_ID));
    }

    public static ResourceKey<RecipeSerializer<?>> getTransmuteShapelessRecipeSerializer(String modId) {
        return ResourceKey.create(Registries.RECIPE_SERIALIZER,
                Identifier.fromNamespaceAndPath(modId, TRANSMUTE_SHAPELESS_RECIPE_SERIALIZER_ID));
    }

    /**
     * Registers mod-specific recipe serializers for custom transmute recipes.
     *
     * @param registryManager the registry manager instance
     */
    public static void registerTransmuteRecipeSerializers(RegistryManager registryManager) {
        Holder.Reference<RecipeSerializer<TransmuteShapedRecipe>> shapedSerializer = TransmuteRecipeFactory.register(
                registryManager,
                TRANSMUTE_SHAPED_RECIPE_SERIALIZER_ID,
                ShapedRecipe.SERIALIZER,
                TransmuteShapedRecipe::new);
        ProxyImpl.get().synchronizeRecipeSerializer(shapedSerializer);
        Holder.Reference<RecipeSerializer<TransmuteShapelessRecipe>> shapelessSerializer = TransmuteRecipeFactory.register(
                registryManager,
                TRANSMUTE_SHAPELESS_RECIPE_SERIALIZER_ID,
                ShapelessRecipe.SERIALIZER,
                TransmuteShapelessRecipe::new);
        ProxyImpl.get().synchronizeRecipeSerializer(shapelessSerializer);
    }

    /**
     * Registers a new skull block type.
     *
     * @param id the name used for the skull block type
     * @return the skull block type
     */
    public static SkullBlock.Type registerSkullBlockType(Identifier id) {
        String string = id.toString();
        SkullBlock.Type skullBlockType = () -> string;
        CommonSetupCallback.EVENT.register(() -> {
            SkullBlock.Type.TYPES.put(skullBlockType.getSerializedName(), skullBlockType);
        });
        return skullBlockType;
    }

    /**
     * Creates and registers a new {@link ContextKeySet}.
     *
     * @param id              the identifier for the registry
     * @param builderConsumer the consumer for configuring the builder
     * @return the created context key set
     */
    public static ContextKeySet registerContextKeySet(Identifier id, Consumer<ContextKeySet.Builder> builderConsumer) {
        ContextKeySet.Builder builder = new ContextKeySet.Builder();
        builderConsumer.accept(builder);
        ContextKeySet contextKeySet = builder.build();
        if (ModLoaderEnvironment.INSTANCE.isDataGeneration()) {
            // Run this immediately, as the common setup does not run during data generation, but we need this for generating loot tables.
            // This can only ever run in a development environment where no other mods conflicting here will be present.
            registerContextKeySet(id, contextKeySet);
        } else {
            // Delay this, as the underlying registry map is not concurrent, possibly leading to issues with other mods on NeoForge.
            CommonSetupCallback.EVENT.register(() -> {
                registerContextKeySet(id, contextKeySet);
            });
        }

        return contextKeySet;
    }

    private static void registerContextKeySet(Identifier id, ContextKeySet contextKeySet) {
        if (LootContextParamSets.REGISTRY.put(id, contextKeySet) != null) {
            throw new IllegalStateException("Loot context key set " + id + " is already registered");
        }
    }
}
