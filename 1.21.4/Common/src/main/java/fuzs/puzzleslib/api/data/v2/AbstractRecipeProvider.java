package fuzs.puzzleslib.api.data.v2;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
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
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Stream;

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
    private static final Map<BlockFamily.Variant, BiFunction<ItemLike, ItemLike, RecipeBuilder>> STONECUTTING_BUILDERS = ImmutableMap.<BlockFamily.Variant, BiFunction<ItemLike, ItemLike, RecipeBuilder>>builder()
            .put(BlockFamily.Variant.CHISELED,
                    (itemLike, itemLike2) -> SingleItemRecipeBuilder.stonecutting(Ingredient.of(itemLike2),
                            RecipeCategory.BUILDING_BLOCKS,
                            itemLike))
            .put(BlockFamily.Variant.CUT,
                    (itemLike, itemLike2) -> SingleItemRecipeBuilder.stonecutting(Ingredient.of(itemLike2),
                            RecipeCategory.BUILDING_BLOCKS,
                            itemLike))
            .put(BlockFamily.Variant.SLAB,
                    (itemLike, itemLike2) -> SingleItemRecipeBuilder.stonecutting(Ingredient.of(itemLike2),
                            RecipeCategory.BUILDING_BLOCKS,
                            itemLike,
                            2))
            .put(BlockFamily.Variant.STAIRS,
                    (itemLike, itemLike2) -> SingleItemRecipeBuilder.stonecutting(Ingredient.of(itemLike2),
                            RecipeCategory.BUILDING_BLOCKS,
                            itemLike))
            .put(BlockFamily.Variant.POLISHED,
                    (itemLike, itemLike2) -> SingleItemRecipeBuilder.stonecutting(Ingredient.of(itemLike2),
                            RecipeCategory.BUILDING_BLOCKS,
                            itemLike))
            .put(BlockFamily.Variant.WALL,
                    (itemLike, itemLike2) -> SingleItemRecipeBuilder.stonecutting(Ingredient.of(itemLike2),
                            RecipeCategory.DECORATIONS,
                            itemLike))
            .build();

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

    public void generateForBlockFamilies(Stream<BlockFamily> blockFamilies) {
        blockFamilies.filter(BlockFamily::shouldGenerateRecipe)
                .forEach(blockFamily -> this.generateRecipes(blockFamily, FeatureFlags.DEFAULT_FLAGS));
    }

    @Override
    public void generateRecipes(BlockFamily blockFamily, FeatureFlagSet requiredFeatures) {
        super.generateRecipes(blockFamily, requiredFeatures);
        // also automatically generate stone-cutting recipes
        blockFamily.getVariants().forEach((BlockFamily.Variant variant, Block block) -> {
            if (block.requiredFeatures().isSubsetOf(requiredFeatures)) {
                BiFunction<ItemLike, ItemLike, RecipeBuilder> biFunction = STONECUTTING_BUILDERS.get(variant);
                ItemLike itemLike = this.getBaseBlock(blockFamily, variant);
                if (biFunction != null) {
                    RecipeBuilder recipeBuilder = biFunction.apply(block, itemLike);
                    recipeBuilder.unlockedBy(blockFamily.getRecipeUnlockedBy().orElseGet(() -> getHasName(itemLike)),
                            this.has(itemLike));
                    recipeBuilder.save(this.output, getStonecuttingRecipeName(block, itemLike));
                }
            }
        });
    }

    public void stair(RecipeCategory recipeCategory, ItemLike resultItem, ItemLike ingredientItem) {
        this.stairBuilder(recipeCategory, resultItem, Ingredient.of(ingredientItem))
                .unlockedBy(getHasName(ingredientItem), this.has(ingredientItem))
                .save(this.output);
    }

    public RecipeBuilder stairBuilder(RecipeCategory recipeCategory, ItemLike resultItem, Ingredient ingredient) {
        return this.shaped(recipeCategory, resultItem, 4)
                .define('#', ingredient)
                .pattern("#  ")
                .pattern("## ")
                .pattern("###");
    }

    public void metalCooking(ItemLike resultItem, ItemLike ingredientItem, float experience) {
        this.metalCooking(resultItem, ingredientItem, experience, 200);
    }

    public void metalCooking(ItemLike resultItem, ItemLike ingredientItem, float experience, int baseCookingTime) {
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(ingredientItem),
                RecipeCategory.MISC,
                resultItem,
                experience,
                baseCookingTime).unlockedBy(getHasName(ingredientItem), this.has(ingredientItem)).save(this.output);
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(ingredientItem),
                        RecipeCategory.MISC,
                        resultItem,
                        experience,
                        baseCookingTime / 2)
                .unlockedBy(getHasName(ingredientItem), this.has(ingredientItem))
                .save(this.output, getBlastingRecipeName(resultItem));
    }

    public void foodCooking(ItemLike resultItem, ItemLike ingredientItem) {
        this.foodCooking(resultItem, ingredientItem, 0.35F, 200);
    }

    public void foodCooking(ItemLike resultItem, ItemLike ingredientItem, float experienceReward, int baseCookingTime) {
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(ingredientItem),
                RecipeCategory.FOOD,
                resultItem,
                experienceReward,
                baseCookingTime).unlockedBy(getHasName(ingredientItem), this.has(ingredientItem)).save(this.output);
        SimpleCookingRecipeBuilder.smoking(Ingredient.of(ingredientItem),
                        RecipeCategory.FOOD,
                        resultItem,
                        experienceReward,
                        baseCookingTime / 2)
                .unlockedBy(getHasName(ingredientItem), this.has(ingredientItem))
                .save(this.output, getCraftingMethodRecipeName(resultItem, RecipeSerializer.SMOKING_RECIPE));
        SimpleCookingRecipeBuilder.campfireCooking(Ingredient.of(ingredientItem),
                        RecipeCategory.FOOD,
                        resultItem,
                        experienceReward,
                        baseCookingTime * 3)
                .unlockedBy(getHasName(ingredientItem), this.has(ingredientItem))
                .save(this.output, getCraftingMethodRecipeName(resultItem, RecipeSerializer.CAMPFIRE_COOKING_RECIPE));
    }

    public RecipeBuilder stonecutterResultFromBaseBuilder(RecipeCategory recipeCategory, ItemLike resultItem, Ingredient ingredient) {
        return this.stonecutterResultFromBaseBuilder(recipeCategory, resultItem, ingredient, 1);
    }

    public RecipeBuilder stonecutterResultFromBaseBuilder(RecipeCategory recipeCategory, ItemLike resultItem, Ingredient ingredient, int count) {
        return SingleItemRecipeBuilder.stonecutting(ingredient, recipeCategory, resultItem, count);
    }

    public void smithing(RecipeCategory recipeCategory, ItemLike resultItem, ItemLike templateItem, ItemLike baseItem, ItemLike materialItem) {
        SmithingTransformRecipeBuilder.smithing(Ingredient.of(templateItem),
                        Ingredient.of(baseItem),
                        Ingredient.of(materialItem),
                        recipeCategory,
                        resultItem.asItem())
                .unlocks(getHasName(materialItem), this.has(materialItem))
                .save(this.output, getSmithingRecipeName(resultItem));
    }

    public void waxing(ItemLike resultItem, ItemLike ingredientItem) {
        ShapelessRecipeBuilder.shapeless(this.items, RecipeCategory.BUILDING_BLOCKS, resultItem)
                .requires(ingredientItem)
                .requires(Items.HONEYCOMB)
                .group(getItemName(resultItem))
                .unlockedBy(getHasName(ingredientItem), this.has(ingredientItem))
                .save(this.output, getConversionRecipeName(resultItem, Items.HONEYCOMB));
    }

    public static String getCraftingMethodRecipeName(ItemLike resultItem, RecipeSerializer<?> recipeSerializer) {
        ResourceLocation resourceLocation = BuiltInRegistries.RECIPE_SERIALIZER.getKey(recipeSerializer);
        Objects.requireNonNull(resourceLocation, "resource location is null");
        return getCraftingMethodRecipeName(resultItem, resourceLocation.getPath());
    }

    public static String getCraftingMethodRecipeName(ItemLike resultItem, String craftingMethod) {
        return getItemName(resultItem) + "_from_" + craftingMethod;
    }

    public static String getStonecuttingRecipeName(ItemLike resultItem, ItemLike material) {
        return getConversionRecipeName(resultItem, material) + "_stonecutting";
    }

    public static String getSmithingRecipeName(ItemLike resultItem) {
        return getItemName(resultItem) + "_smithing";
    }

    public static String getHasName(TagKey<Item> tagKey) {
        return "has_" + tagKey.location().getPath();
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
