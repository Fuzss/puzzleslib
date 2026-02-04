package fuzs.puzzleslib.api.data.v2;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.JsonOps;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.puzzleslib.api.init.v3.family.BlockSetFamily;
import fuzs.puzzleslib.api.init.v3.family.BlockSetVariant;
import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Stream;

public abstract class AbstractRecipeProvider extends RecipeProvider implements DataProvider {
    private static final RegistryAccess REGISTRY_ACCESS = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
    private static final RecipeOutput RECIPE_OUTPUT = ProxyImpl.get().getThrowingRecipeOutput();
    /**
     * @see #generateFor(BlockSetFamily, Map)
     */
    public static final Map<BlockSetVariant, FamilyRecipeProvider> VARIANT_STONE_PROVIDERS = ImmutableMap.<BlockSetVariant, FamilyRecipeProvider>builder()
            .put(BlockSetVariant.CHISELED, FamilyRecipeProvider.stonecutting())
            .put(BlockSetVariant.CUT, FamilyRecipeProvider.stonecutting())
            .put(BlockSetVariant.SLAB, FamilyRecipeProvider.stonecutting(2))
            .put(BlockSetVariant.STAIRS, FamilyRecipeProvider.stonecutting())
            .put(BlockSetVariant.POLISHED, FamilyRecipeProvider.stonecutting())
            .put(BlockSetVariant.WALL, FamilyRecipeProvider.stonecutting())
            .build();

    private final String modId;
    private final PackOutput packOutput;
    private final CompletableFuture<HolderLookup.Provider> registries;

    public AbstractRecipeProvider(DataProviderContext context) {
        this(context.getModId(), context.getPackOutput(), context.getRegistries());
    }

    public AbstractRecipeProvider(String modId, PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
        super(REGISTRY_ACCESS, RECIPE_OUTPUT);
        this.modId = modId;
        this.packOutput = packOutput;
        this.registries = registries;
    }

    @Nullable
    public static <T> JsonElement searchAndReplaceValue(@Nullable JsonElement jsonElement, T searchFor, T replaceWith) {
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

    /**
     * @see #generateFor(BlockSetFamily, Map)
     */
    public static Map<BlockSetVariant, FamilyRecipeProvider> createVariantWoodProviders(BlockSetFamily blockSetFamily, Block strippedBlock) {
        return ImmutableMap.<BlockSetVariant, FamilyRecipeProvider>builder()
                .put(BlockSetVariant.HANGING_SIGN,
                        (RecipeProvider recipeProvider, ItemLike result, ItemLike input, Optional<String> recipeGroupPrefix, Optional<String> recipeUnlockedBy) -> {
                            recipeProvider.hangingSign(result, strippedBlock);
                        })
                .put(BlockSetVariant.HANGING_SIGN,
                        (RecipeProvider recipeProvider, ItemLike result, ItemLike input, Optional<String> recipeGroupPrefix, Optional<String> recipeUnlockedBy) -> {
                            recipeProvider.shelf(result, strippedBlock);
                        })
                .put(BlockSetVariant.BOAT,
                        (RecipeProvider recipeProvider, ItemLike result, ItemLike input, Optional<String> recipeGroupPrefix, Optional<String> recipeUnlockedBy) -> {
                            recipeProvider.woodenBoat(result, input);
                        })
                .put(BlockSetVariant.CHEST_BOAT,
                        (RecipeProvider recipeProvider, ItemLike result, ItemLike input, Optional<String> recipeGroupPrefix, Optional<String> recipeUnlockedBy) -> {
                            Holder.Reference<Item> boatItem = blockSetFamily.getItem(BlockSetVariant.BOAT);
                            Objects.requireNonNull(boatItem, "boat item is null");
                            recipeProvider.woodenBoat(result, boatItem.value());
                        })
                .build();
    }

    @Deprecated(forRemoval = true)
    public void generateForBlockFamilies(Stream<BlockFamily> blockFamilies) {
        blockFamilies.filter(BlockFamily::shouldGenerateRecipe)
                .forEach((BlockFamily blockFamily) -> this.generateRecipes(blockFamily, FeatureFlags.DEFAULT_FLAGS));
    }

    public void generateFor(BlockSetFamily blockSetFamily, Map<BlockSetVariant, FamilyRecipeProvider> variants) {
        BlockFamily blockFamily = blockSetFamily.getBlockFamily();
        this.generateRecipes(blockFamily, FeatureFlags.DEFAULT_FLAGS);
        if (blockFamily.shouldGenerateRecipe()) {
            blockSetFamily.getItemVariants().forEach((BlockSetVariant variant, Holder.Reference<Item> holder) -> {
                FamilyRecipeProvider recipeProvider = variants.get(variant);
                if (recipeProvider != null) {
                    ItemLike baseBlock;
                    if (variant.toVanilla() != null) {
                        baseBlock = this.getBaseBlock(blockFamily, variant.toVanilla());
                    } else {
                        baseBlock = blockSetFamily.getBaseBlock().value();
                    }

                    recipeProvider.create(this,
                            holder.value(),
                            baseBlock,
                            blockFamily.getRecipeGroupPrefix(),
                            blockFamily.getRecipeUnlockedBy());
                }
            });
        }
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
        Identifier identifier = BuiltInRegistries.RECIPE_SERIALIZER.getKey(recipeSerializer);
        Objects.requireNonNull(identifier, "identifier is null");
        return getCraftingMethodRecipeName(resultItem, identifier.getPath());
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
            RecipeOutput recipeOutput = ProxyImpl.get()
                    .getRecipeProviderOutput(output, this.modId, this.packOutput, registries, completableFutures::add);
            this.injectRegistries(registries, recipeOutput);
            this.addRecipes(recipeOutput);
            return CompletableFuture.allOf(completableFutures.toArray(CompletableFuture[]::new));
        }).thenRun(() -> {
            this.injectRegistries(REGISTRY_ACCESS, RECIPE_OUTPUT);
        });
    }

    private void injectRegistries(HolderLookup.Provider registries, RecipeOutput recipeOutput) {
        super.registries = registries;
        super.items = registries.lookupOrThrow(Registries.ITEM);
        super.output = recipeOutput;
    }

    public final HolderLookup.Provider registries() {
        Preconditions.checkState(super.registries != REGISTRY_ACCESS, "registry access is empty");
        return super.registries;
    }

    public final HolderGetter<Item> items() {
        Preconditions.checkState(super.registries != REGISTRY_ACCESS, "registry access is empty");
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

    @FunctionalInterface
    public interface FamilyRecipeProvider {
        void create(RecipeProvider recipeProvider, ItemLike result, ItemLike input, Optional<String> recipeGroupPrefix, Optional<String> recipeUnlockedBy);

        static FamilyRecipeProvider stonecutting() {
            return stonecutting(1);
        }

        static FamilyRecipeProvider stonecutting(int count) {
            return (RecipeProvider recipeProvider, ItemLike result, ItemLike input, Optional<String> recipeGroupPrefix, Optional<String> recipeUnlockedBy) -> {
                SingleItemRecipeBuilder recipeBuilder = SingleItemRecipeBuilder.stonecutting(Ingredient.of(input),
                        RecipeCategory.BUILDING_BLOCKS,
                        result,
                        count);
                recipeBuilder.unlockedBy(recipeUnlockedBy.orElseGet(() -> getHasName(input)),
                        recipeProvider.has(input));
                recipeBuilder.save(recipeProvider.output, getStonecuttingRecipeName(result, input));
            };
        }
    }

    @ApiStatus.Internal
    public static abstract class RecipeOutputImpl implements RecipeOutput {
        private final CachedOutput output;
        private final String modId;
        private final PackOutput.PathProvider recipePathProvider;
        private final PackOutput.PathProvider advancementPathProvider;
        private final HolderLookup.Provider registries;
        private final Consumer<CompletableFuture<?>> consumer;
        private final Set<ResourceKey<Recipe<?>>> recipes = new HashSet<>();

        protected RecipeOutputImpl(CachedOutput output, String modId, PackOutput packOutput, HolderLookup.Provider registries, Consumer<CompletableFuture<?>> consumer) {
            this.output = output;
            this.modId = modId;
            this.recipePathProvider = packOutput.createRegistryElementsPathProvider(Registries.RECIPE);
            this.advancementPathProvider = packOutput.createRegistryElementsPathProvider(Registries.ADVANCEMENT);
            this.registries = registries;
            this.consumer = consumer;
        }

        @Override
        public void accept(ResourceKey<Recipe<?>> resourceKey, Recipe<?> recipe, @Nullable AdvancementHolder advancementHolder) {
            final ResourceKey<Recipe<?>> originalResourceKey = resourceKey;
            // relocate all recipes to the mod id, so they do not depend on the item namespace which would
            // place e.g. new recipes for vanilla items in 'minecraft' which is not desired
            Identifier identifier = Identifier.fromNamespaceAndPath(this.modId, resourceKey.identifier().getPath());
            resourceKey = ResourceKey.create(Registries.RECIPE, identifier);
            if (!this.recipes.add(resourceKey)) {
                throw new IllegalStateException("Duplicate recipe " + resourceKey);
            } else {
                this.consumer.accept(DataProvider.saveStable(this.output,
                        this.registries,
                        Recipe.CODEC,
                        recipe,
                        this.recipePathProvider.json(resourceKey.identifier())));
                if (advancementHolder != null) {
                    RegistryOps<JsonElement> registryOps = this.registries.createSerializationContext(JsonOps.INSTANCE);
                    JsonElement jsonElement = Advancement.CODEC.encodeStart(registryOps, advancementHolder.value())
                            .getOrThrow();
                    jsonElement = searchAndReplaceValue(jsonElement, originalResourceKey, resourceKey);
                    Identifier advancementLocation = Identifier.fromNamespaceAndPath(this.modId,
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
