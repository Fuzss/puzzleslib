package fuzs.puzzleslib.api.data.v2;

import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
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

    protected static String getHasName(ItemLike item, ItemLike... items) {
        return "has_" + Stream.concat(Stream.of(item), Stream.of(items)).map(RecipeProvider::getItemName).collect(Collectors.joining("_and_"));
    }

    protected static InventoryChangeTrigger.TriggerInstance has(ItemLike item, ItemLike... items) {
        return inventoryTrigger(ItemPredicate.Builder.item().of(Stream.concat(Stream.of(item), Stream.of(items)).toArray(ItemLike[]::new)).build());
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        Set<ResourceLocation> set = Sets.newHashSet();
        List<CompletableFuture<?>> list = new ArrayList<>();
        this.buildRecipes((finishedRecipe) -> {
            ResourceLocation id = new ResourceLocation(this.modId, finishedRecipe.getId().getPath());
            if (!set.add(id)) {
                throw new IllegalStateException("Duplicate recipe " + id);
            } else {
                list.add(DataProvider.saveStable(output, finishedRecipe.serializeRecipe(), this.recipePathProvider.json(id)));
                JsonElement jsonElement = finishedRecipe.serializeAdvancement();
                if (jsonElement != null) {
                    jsonElement = searchAndReplaceValue(jsonElement, finishedRecipe.getId(), id);
                    ResourceLocation advancementId = new ResourceLocation(this.modId, finishedRecipe.getAdvancementId().getPath());
                    list.add(DataProvider.saveStable(output, jsonElement, this.advancementPathProvider.json(advancementId)));
                }
            }
        });
        return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
    }

    @Override
    public final void buildRecipes(Consumer<FinishedRecipe> exporter) {
        this.addRecipes(exporter);
    }

    public abstract void addRecipes(Consumer<FinishedRecipe> exporter);
}
