package fuzs.puzzleslib.api.data.v2;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class AbstractRecipeProvider extends RecipeProvider implements DataProvider {
    static final RegistryAccess EMPTY_REGISTRY_ACCESS = new RegistryAccess.ImmutableRegistryAccess(List.of(new MappedRegistry<>(
            Registries.ITEM,
            Lifecycle.stable())));
    static final RecipeOutput EMPTY_RECIPE_OUTPUT = new RecipeOutput() {
        @Override
        public void accept(ResourceKey<Recipe<?>> resourceKey, Recipe<?> recipe, @Nullable AdvancementHolder advancementHolder) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Advancement.Builder advancement() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void includeRootAdvancement() {
            throw new UnsupportedOperationException();
        }
    };

    private final PackOutput packOutput;
    private final CompletableFuture<HolderLookup.Provider> registries;
    protected final String modId;

    public AbstractRecipeProvider(DataProviderContext context) {
        this(context.getModId(), context.getPackOutput(), context.getRegistries());
    }

    public AbstractRecipeProvider(String modId, PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
        super(EMPTY_REGISTRY_ACCESS, EMPTY_RECIPE_OUTPUT);
        this.packOutput = packOutput;
        this.registries = registries;
        this.modId = modId;
    }

    @Nullable
    protected static <T> JsonElement searchAndReplaceValue(@Nullable JsonElement jsonElement, T searchFor, T replaceWith) {
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

    public static String getItemName(Ingredient ingredient) {
        return getItemName(ingredient.items().stream().map(Holder::value).toArray(ItemLike[]::new));
    }

    public static String getItemName(ItemLike... items) {
        Preconditions.checkState(items.length > 0, "items is empty");
        return Arrays.stream(items).map(RecipeProvider::getItemName).collect(Collectors.joining("_or_"));
    }

    public static String getConversionRecipeName(ItemLike result, Ingredient ingredient) {
        return getConversionRecipeName(result, ingredient.items().stream().map(Holder::value).toArray(ItemLike[]::new));
    }

    public static String getConversionRecipeName(ItemLike result, ItemLike... items) {
        Preconditions.checkState(items.length > 0, "items is empty");
        return getItemName(result) + "_from_" + getItemName(items);
    }

    public static String getHasName(Ingredient ingredient) {
        return getHasName(ingredient.items().stream().map(Holder::value).toArray(ItemLike[]::new));
    }

    public static String getHasName(ItemLike... items) {
        Preconditions.checkState(items.length > 0, "items is empty");
        return "has_" + getItemName(items);
    }

    public Criterion<InventoryChangeTrigger.TriggerInstance> has(Ingredient ingredient) {
        return this.has(ingredient.items().stream().map(Holder::value).toArray(ItemLike[]::new));
    }

    public Criterion<InventoryChangeTrigger.TriggerInstance> has(ItemLike... items) {
        Preconditions.checkState(items.length > 0, "items is empty");
        return inventoryTrigger(ItemPredicate.Builder.item().of(this.items, items).build());
    }

    @Deprecated(forRemoval = true)
    public void stonecutterResultFromBase(RecipeOutput recipeOutput, RecipeCategory category, ItemLike result, Ingredient material) {
        this.stonecutterResultFromBase(category, result, material);
    }

    public void stonecutterResultFromBase(RecipeCategory category, ItemLike result, Ingredient material) {
        this.stonecutterResultFromBase(category, result, material, 1);
    }

    @Deprecated(forRemoval = true)
    public void stonecutterResultFromBase(RecipeOutput recipeOutput, RecipeCategory category, ItemLike result, Ingredient material, int count) {
        this.stonecutterResultFromBase(category, result, material, count);
    }

    public void stonecutterResultFromBase(RecipeCategory category, ItemLike result, Ingredient material, int count) {
        SingleItemRecipeBuilder.stonecutting(material, category, result, count)
                .unlockedBy(getHasName(material), this.has(material))
                .save(this.output, getConversionRecipeName(result, material) + "_stonecutting");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        // this must not be CompletableFuture::thenApply, as not all futures are guaranteed to complete in time,
        // leading to some files silently failing to generate at all
        return this.registries.thenCompose((HolderLookup.Provider registries) -> {
            List<CompletableFuture<?>> completableFutures = new ArrayList<>();
            RecipeOutput recipeOutput = new RecipeOutputImpl(output, registries, completableFutures::add);
            this.injectRegistries(registries, recipeOutput);
            this.addRecipes(recipeOutput);
            return CompletableFuture.allOf(completableFutures.toArray(CompletableFuture[]::new));
        }).thenRun(() -> {
            this.injectRegistries(EMPTY_REGISTRY_ACCESS, EMPTY_RECIPE_OUTPUT);
        });
    }

    private void injectRegistries(HolderLookup.Provider registries, RecipeOutput recipeOutput) {
        super.registries = registries;
        super.items = registries.lookupOrThrow(Registries.ITEM);
        super.output = recipeOutput;
    }

    public final HolderLookup.Provider registries() {
        Preconditions.checkState(super.registries != EMPTY_REGISTRY_ACCESS, "registry access is empty");
        return super.registries;
    }

    public final HolderGetter<Item> items() {
        Preconditions.checkState(super.registries != EMPTY_REGISTRY_ACCESS, "registry access is empty");
        return super.items;
    }

    @Override
    public void buildRecipes() {
        throw new UnsupportedOperationException();
    }

    public abstract void addRecipes(RecipeOutput recipeOutput);

    @Override
    public String getName() {
        return "Recipes";
    }

    private class RecipeOutputImpl implements RecipeOutput {
        private final CachedOutput output;
        private final PackOutput.PathProvider recipePathProvider;
        private final PackOutput.PathProvider advancementPathProvider;
        private final HolderLookup.Provider registries;
        private final Consumer<CompletableFuture<?>> consumer;
        private final Set<ResourceKey<Recipe<?>>> recipes = new HashSet<>();

        public RecipeOutputImpl(CachedOutput output, HolderLookup.Provider registries, Consumer<CompletableFuture<?>> consumer) {
            this.output = output;
            this.recipePathProvider = AbstractRecipeProvider.this.packOutput.createRegistryElementsPathProvider(
                    Registries.RECIPE);
            this.advancementPathProvider = AbstractRecipeProvider.this.packOutput.createRegistryElementsPathProvider(
                    Registries.ADVANCEMENT);
            this.registries = registries;
            this.consumer = consumer;
        }

        @Override
        public void accept(ResourceKey<Recipe<?>> resourceKey, Recipe<?> recipe, @Nullable AdvancementHolder advancementHolder) {
            final ResourceKey<Recipe<?>> originalResourceKey = resourceKey;
            // relocate all recipes to the mod id, so they do not depend on the item namespace which would
            // place e.g. new recipes for vanilla items in 'minecraft' which is not desired
            ResourceLocation resourceLocation = ResourceLocationHelper.fromNamespaceAndPath(AbstractRecipeProvider.this.modId,
                    resourceKey.location().getPath());
            resourceKey = ResourceKey.create(Registries.RECIPE, resourceLocation);
            if (!this.recipes.add(resourceKey)) {
                throw new IllegalStateException("Duplicate recipe " + resourceKey);
            } else {
                this.consumer.accept(DataProvider.saveStable(this.output,
                        this.registries,
                        Recipe.CODEC,
                        recipe,
                        this.recipePathProvider.json(resourceKey.location())));
                if (advancementHolder != null) {
                    RegistryOps<JsonElement> registryOps = this.registries.createSerializationContext(JsonOps.INSTANCE);
                    JsonElement jsonElement = Advancement.CODEC.encodeStart(registryOps, advancementHolder.value())
                            .getOrThrow();
                    jsonElement = searchAndReplaceValue(jsonElement, originalResourceKey, resourceKey);
                    ResourceLocation advancementLocation = ResourceLocationHelper.fromNamespaceAndPath(
                            AbstractRecipeProvider.this.modId,
                            advancementHolder.id().getPath());
                    this.consumer.accept(DataProvider.saveStable(this.output,
                            jsonElement,
                            this.advancementPathProvider.json(advancementLocation)));
                }
            }
        }

        @SuppressWarnings("removal")
        @Override
        public Advancement.Builder advancement() {
            return Advancement.Builder.recipeAdvancement().parent(RecipeBuilder.ROOT_RECIPE_ADVANCEMENT);
        }

        @Override
        public void includeRootAdvancement() {
            throw new UnsupportedOperationException();
        }
    }
}