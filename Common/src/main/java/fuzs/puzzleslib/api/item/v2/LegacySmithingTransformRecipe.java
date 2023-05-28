package fuzs.puzzleslib.api.item.v2;

import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;

import java.util.Objects;

/**
 * A simplified version on {@link SmithingTransformRecipe} that allows for upgrading gear without the need for a smithing template, just like the old smithing used to work.
 * <p>Intended for simply netherite upgrades in mods that shouldn't necessarily be as expensive to consume a full netherite upgrade smithing template.
 */
public class LegacySmithingTransformRecipe extends SmithingTransformRecipe {
    /**
     * The recipe serializer id that is used during registration.
     */
    public static final String RECIPE_SERIALIZER_ID = "legacy_smithing_transform";

    private final Ingredient base;
    private final Ingredient addition;
    private final ItemStack result;

    /**
     * @param id       the recipe id, the namespace is also used for retrieving the corresponding serializer as every mod using this recipe type must register their own serializer
     * @param base     the base item for the smithing upgrade, like diamond armor and tools in vanilla
     * @param addition the upgrade item, usually a netherite ingot in vanilla
     * @param result   the result item from the smithing upgrade, like netherite armor and tools in vanilla
     */
    public LegacySmithingTransformRecipe(ResourceLocation id, Ingredient base, Ingredient addition, ItemStack result) {
        super(id, Ingredient.of(), base, addition, result);
        this.base = base;
        this.addition = addition;
        this.result = result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return getModSerializer(this.getId().getNamespace());
    }

    /**
     * Finds the mod-specific {@link RecipeSerializer} in the registry.
     * <p>{@link fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags#LEGACY_SMITHING} must be enabled so the serializer is registered.
     *
     * @param modId the mod id to find the serializer for
     * @return the serializer
     */
    public static RecipeSerializer<?> getModSerializer(String modId) {
        RecipeSerializer<?> recipeSerializer = BuiltInRegistries.RECIPE_SERIALIZER.get(new ResourceLocation(modId, RECIPE_SERIALIZER_ID));
        Objects.requireNonNull(recipeSerializer, "legacy smithing transform recipe serializer for %s is null".formatted(modId));
        return recipeSerializer;
    }

    /**
     * The custom serializer used for <code>legacy_smithing_transform</code>, simply does not serialize any template item, as it is set to empty which would fail during serialization.
     */
    public static class Serializer implements RecipeSerializer<LegacySmithingTransformRecipe> {

        @Override
        public LegacySmithingTransformRecipe fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
            Ingredient base = Ingredient.fromJson(GsonHelper.getAsJsonObject(jsonObject, "base"));
            Ingredient addition = Ingredient.fromJson(GsonHelper.getAsJsonObject(jsonObject, "addition"));
            ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(jsonObject, "result"));
            return new LegacySmithingTransformRecipe(resourceLocation, base, addition, result);
        }

        @Override
        public LegacySmithingTransformRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf friendlyByteBuf) {
            Ingredient base = Ingredient.fromNetwork(friendlyByteBuf);
            Ingredient addition = Ingredient.fromNetwork(friendlyByteBuf);
            ItemStack result = friendlyByteBuf.readItem();
            return new LegacySmithingTransformRecipe(resourceLocation, base, addition, result);
        }

        @Override
        public void toNetwork(FriendlyByteBuf friendlyByteBuf, LegacySmithingTransformRecipe smithingTransformRecipe) {
            smithingTransformRecipe.base.toNetwork(friendlyByteBuf);
            smithingTransformRecipe.addition.toNetwork(friendlyByteBuf);
            friendlyByteBuf.writeItem(smithingTransformRecipe.result);
        }
    }
}
