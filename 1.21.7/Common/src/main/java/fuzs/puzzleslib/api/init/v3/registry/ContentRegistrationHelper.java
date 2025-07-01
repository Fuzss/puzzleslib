package fuzs.puzzleslib.api.init.v3.registry;

import fuzs.puzzleslib.api.event.v1.CommonSetupCallback;
import fuzs.puzzleslib.impl.item.CustomTransmuteRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.SkullBlock;

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
}
