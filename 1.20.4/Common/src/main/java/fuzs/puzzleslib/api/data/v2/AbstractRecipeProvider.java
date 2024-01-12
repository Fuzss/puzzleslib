package fuzs.puzzleslib.api.data.v2;

import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.JsonOps;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import net.minecraft.Util;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractRecipeProvider extends RecipeProvider {
    private final String modId;

    public AbstractRecipeProvider(DataProviderContext context) {
        this(context.getModId(), context.getPackOutput());
    }

    public AbstractRecipeProvider(String modId, PackOutput packOutput) {
        super(packOutput);
        this.modId = modId;
    }

    @Nullable
    private static <T> JsonElement searchAndReplaceValue(@Nullable JsonElement jsonElement, T searchFor, T replaceWith) {
        Objects.requireNonNull(searchFor, "search for is null");
        Objects.requireNonNull(replaceWith, "replace with is null");
        if (jsonElement != null && !jsonElement.isJsonNull()) {
            if (jsonElement.isJsonPrimitive()) {
                JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
                if (jsonPrimitive.isNumber()) {
                    if (searchFor.equals(jsonPrimitive.getAsNumber())) {
                        return new JsonPrimitive((Number) replaceWith);
                    }
                } else if (jsonPrimitive.isBoolean()) {
                    if (searchFor.equals(jsonPrimitive.getAsBoolean())) {
                        return new JsonPrimitive((Boolean) replaceWith);
                    }
                } else if (jsonPrimitive.isString()) {
                    if (searchFor.toString().equals(jsonPrimitive.getAsString())) {
                        return new JsonPrimitive(replaceWith.toString());
                    }
                }
                return jsonElement;
            } else if (jsonElement.isJsonArray()) {
                JsonArray jsonArray = jsonElement.getAsJsonArray();
                for (int i = 0; i < jsonArray.size(); i++) {
                    jsonArray.set(i, searchAndReplaceValue(jsonArray.get(i), searchFor, replaceWith));
                }
            } else if (jsonElement.isJsonObject()) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                    entry.setValue(searchAndReplaceValue(entry.getValue(), searchFor, replaceWith));
                }
            }
        }
        return jsonElement;
    }

    public static String getHasName(ItemLike item, ItemLike... items) {
        return "has_" + Stream.concat(Stream.of(item), Stream.of(items)).map(RecipeProvider::getItemName).collect(Collectors.joining("_and_"));
    }

    public static Criterion<InventoryChangeTrigger.TriggerInstance> has(ItemLike item, ItemLike... items) {
        return inventoryTrigger(ItemPredicate.Builder.item().of(Stream.concat(Stream.of(item), Stream.of(items)).toArray(ItemLike[]::new)).build());
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        Set<ResourceLocation> set = Sets.newHashSet();
        List<CompletableFuture<?>> list = new ArrayList<>();
        this.buildRecipes(new RecipeOutput() {

            @Override
            public void accept(ResourceLocation location, Recipe<?> recipe, @Nullable AdvancementHolder advancement) {
                final ResourceLocation oldLocation = location;
                location = new ResourceLocation(AbstractRecipeProvider.this.modId, location.getPath());
                if (!set.add(location)) {
                    throw new IllegalStateException("Duplicate recipe " + location);
                } else {
                    list.add(DataProvider.saveStable(output, Recipe.CODEC, recipe, AbstractRecipeProvider.this.recipePathProvider.json(location)));
                    if (advancement != null) {
                        JsonElement jsonElement = Util.getOrThrow(Advancement.CODEC.encodeStart(JsonOps.INSTANCE, advancement.value()), IllegalStateException::new);
                        jsonElement = searchAndReplaceValue(jsonElement, oldLocation, location);
                        ResourceLocation advancementLocation = new ResourceLocation(AbstractRecipeProvider.this.modId, advancement.id().getPath());
                        list.add(DataProvider.saveStable(output, jsonElement, AbstractRecipeProvider.this.advancementPathProvider.json(advancementLocation)));
                    }
                }
            }

            @SuppressWarnings("removal")
            @Override
            public Advancement.Builder advancement() {
                return Advancement.Builder.recipeAdvancement().parent(RecipeBuilder.ROOT_RECIPE_ADVANCEMENT);
            }
        });
        return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
    }

    @Override
    public final void buildRecipes(RecipeOutput exporter) {
        this.addRecipes(exporter);
    }

    public abstract void addRecipes(RecipeOutput exporter);
}
