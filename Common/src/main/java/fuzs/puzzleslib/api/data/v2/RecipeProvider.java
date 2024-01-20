package fuzs.puzzleslib.api.data.v2;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.EnterBlockTrigger;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.*;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

/**
 * Copied from vanilla as NeoForge patches in an additional constructor argument making it impossible to invoke the vanilla class from the Common subproject.
 */
@Deprecated
public abstract class RecipeProvider implements DataProvider {
    public final PackOutput.PathProvider recipePathProvider;
    public final PackOutput.PathProvider advancementPathProvider;
    private static final Map<BlockFamily.Variant, BiFunction<ItemLike, ItemLike, RecipeBuilder>> SHAPE_BUILDERS = ImmutableMap.<BlockFamily.Variant, BiFunction<ItemLike, ItemLike, RecipeBuilder>>builder()
            .put(BlockFamily.Variant.BUTTON, (itemLike, itemLike2) -> buttonBuilder(itemLike, Ingredient.of(itemLike2)))
            .put(BlockFamily.Variant.CHISELED, (itemLike, itemLike2) -> chiseledBuilder(RecipeCategory.BUILDING_BLOCKS, itemLike, Ingredient.of(itemLike2)))
            .put(BlockFamily.Variant.CUT, (itemLike, itemLike2) -> cutBuilder(RecipeCategory.BUILDING_BLOCKS, itemLike, Ingredient.of(itemLike2)))
            .put(BlockFamily.Variant.DOOR, (itemLike, itemLike2) -> doorBuilder(itemLike, Ingredient.of(itemLike2)))
            .put(BlockFamily.Variant.CUSTOM_FENCE, (itemLike, itemLike2) -> fenceBuilder(itemLike, Ingredient.of(itemLike2)))
            .put(BlockFamily.Variant.FENCE, (itemLike, itemLike2) -> fenceBuilder(itemLike, Ingredient.of(itemLike2)))
            .put(BlockFamily.Variant.CUSTOM_FENCE_GATE, (itemLike, itemLike2) -> fenceGateBuilder(itemLike, Ingredient.of(itemLike2)))
            .put(BlockFamily.Variant.FENCE_GATE, (itemLike, itemLike2) -> fenceGateBuilder(itemLike, Ingredient.of(itemLike2)))
            .put(BlockFamily.Variant.SIGN, (itemLike, itemLike2) -> signBuilder(itemLike, Ingredient.of(itemLike2)))
            .put(BlockFamily.Variant.SLAB, (itemLike, itemLike2) -> slabBuilder(RecipeCategory.BUILDING_BLOCKS, itemLike, Ingredient.of(itemLike2)))
            .put(BlockFamily.Variant.STAIRS, (itemLike, itemLike2) -> stairBuilder(itemLike, Ingredient.of(itemLike2)))
            .put(BlockFamily.Variant.PRESSURE_PLATE, (itemLike, itemLike2) -> pressurePlateBuilder(RecipeCategory.REDSTONE, itemLike, Ingredient.of(itemLike2)))
            .put(BlockFamily.Variant.POLISHED, (itemLike, itemLike2) -> polishedBuilder(RecipeCategory.BUILDING_BLOCKS, itemLike, Ingredient.of(itemLike2)))
            .put(BlockFamily.Variant.TRAPDOOR, (itemLike, itemLike2) -> trapdoorBuilder(itemLike, Ingredient.of(itemLike2)))
            .put(BlockFamily.Variant.WALL, (itemLike, itemLike2) -> wallBuilder(RecipeCategory.DECORATIONS, itemLike, Ingredient.of(itemLike2)))
            .build();

    public RecipeProvider(PackOutput output) {
        this.recipePathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "recipes");
        this.advancementPathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "advancements");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        final Set<ResourceLocation> set = Sets.newHashSet();
        final List<CompletableFuture<?>> list = new ArrayList<>();
        this.buildRecipes(new RecipeOutput() {
            @Override
            public void accept(ResourceLocation location, Recipe<?> recipe, @Nullable AdvancementHolder advancement) {
                if (!set.add(location)) {
                    throw new IllegalStateException("Duplicate recipe " + location);
                } else {
                    list.add(DataProvider.saveStable(output, Recipe.CODEC, recipe, RecipeProvider.this.recipePathProvider.json(location)));
                    if (advancement != null) {
                        list.add(DataProvider.saveStable(output, Advancement.CODEC, advancement.value(), RecipeProvider.this.advancementPathProvider.json(advancement.id())));
                    }
                }
            }

            @Override
            public Advancement.Builder advancement() {
                return Advancement.Builder.recipeAdvancement().parent(RecipeBuilder.ROOT_RECIPE_ADVANCEMENT);
            }
        });
        return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
    }

    public CompletableFuture<?> buildAdvancement(CachedOutput output, AdvancementHolder advancementBuilder) {
        return DataProvider.saveStable(output, Advancement.CODEC, advancementBuilder.value(), this.advancementPathProvider.json(advancementBuilder.id()));
    }

    public abstract void buildRecipes(RecipeOutput recipeOutput);

    public static void generateForEnabledBlockFamilies(RecipeOutput recipeOutput, FeatureFlagSet enabledFeatures) {
        BlockFamilies.getAllFamilies().filter(BlockFamily::shouldGenerateRecipe).forEach(blockFamily -> generateRecipes(recipeOutput, blockFamily, enabledFeatures));
    }

    public static void oneToOneConversionRecipe(RecipeOutput recipeOutput, ItemLike result, ItemLike ingredient, @Nullable String group) {
        oneToOneConversionRecipe(recipeOutput, result, ingredient, group, 1);
    }

    public static void oneToOneConversionRecipe(RecipeOutput recipeOutput, ItemLike result, ItemLike ingredient, @Nullable String group, int resultCount) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, result, resultCount)
                .requires(ingredient)
                .group(group)
                .unlockedBy(getHasName(ingredient), has(ingredient))
                .save(recipeOutput, getConversionRecipeName(result, ingredient));
    }

    public static void oreSmelting(
            RecipeOutput recipeOutput, List<ItemLike> ingredients, RecipeCategory category, ItemLike result, float experience, int cookingTime, String group
    ) {
        oreCooking(
                recipeOutput, RecipeSerializer.SMELTING_RECIPE, SmeltingRecipe::new, ingredients, category, result, experience, cookingTime, group, "_from_smelting"
        );
    }

    public static void oreBlasting(
            RecipeOutput recipeOutput, List<ItemLike> ingredients, RecipeCategory category, ItemLike result, float experience, int cookingTime, String group
    ) {
        oreCooking(
                recipeOutput, RecipeSerializer.BLASTING_RECIPE, BlastingRecipe::new, ingredients, category, result, experience, cookingTime, group, "_from_blasting"
        );
    }

    public static <T extends AbstractCookingRecipe> void oreCooking(
            RecipeOutput recipeOutput,
            RecipeSerializer<T> serializer,
            AbstractCookingRecipe.Factory<T> recipeFactory,
            List<ItemLike> ingredients,
            RecipeCategory category,
            ItemLike result,
            float experience,
            int cookingTime,
            String group,
            String suffix
    ) {
        for(ItemLike itemLike : ingredients) {
            SimpleCookingRecipeBuilder.generic(Ingredient.of(itemLike), category, result, experience, cookingTime, serializer, recipeFactory)
                    .group(group)
                    .unlockedBy(getHasName(itemLike), has(itemLike))
                    .save(recipeOutput, getItemName(result) + suffix + "_" + getItemName(itemLike));
        }
    }

    public static void netheriteSmithing(RecipeOutput recipeOutput, Item ingredientItem, RecipeCategory category, Item resultItem) {
        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), Ingredient.of(ingredientItem), Ingredient.of(Items.NETHERITE_INGOT), category, resultItem
                )
                .unlocks("has_netherite_ingot", has(Items.NETHERITE_INGOT))
                .save(recipeOutput, getItemName(resultItem) + "_smithing");
    }

    public static void trimSmithing(RecipeOutput recipeOutput, Item ingredientItem, ResourceLocation location) {
        SmithingTrimRecipeBuilder.smithingTrim(
                        Ingredient.of(ingredientItem), Ingredient.of(ItemTags.TRIMMABLE_ARMOR), Ingredient.of(ItemTags.TRIM_MATERIALS), RecipeCategory.MISC
                )
                .unlocks("has_smithing_trim_template", has(ingredientItem))
                .save(recipeOutput, location);
    }

    public static void twoByTwoPacker(RecipeOutput recipeOutput, RecipeCategory category, ItemLike packed, ItemLike unpacked) {
        ShapedRecipeBuilder.shaped(category, packed, 1)
                .define('#', unpacked)
                .pattern("##")
                .pattern("##")
                .unlockedBy(getHasName(unpacked), has(unpacked))
                .save(recipeOutput);
    }

    public static void threeByThreePacker(RecipeOutput recipeOutput, RecipeCategory category, ItemLike packed, ItemLike unpacked, String criterionName) {
        ShapelessRecipeBuilder.shapeless(category, packed).requires(unpacked, 9).unlockedBy(criterionName, has(unpacked)).save(recipeOutput);
    }

    public static void threeByThreePacker(RecipeOutput recipeOutput, RecipeCategory category, ItemLike packed, ItemLike unpacked) {
        threeByThreePacker(recipeOutput, category, packed, unpacked, getHasName(unpacked));
    }

    public static void planksFromLog(RecipeOutput recipeOutput, ItemLike planks, TagKey<Item> logs, int resultCount) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, planks, resultCount)
                .requires(logs)
                .group("planks")
                .unlockedBy("has_log", has(logs))
                .save(recipeOutput);
    }

    public static void planksFromLogs(RecipeOutput recipeOutput, ItemLike planks, TagKey<Item> logs, int result) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, planks, result)
                .requires(logs)
                .group("planks")
                .unlockedBy("has_logs", has(logs))
                .save(recipeOutput);
    }

    public static void woodFromLogs(RecipeOutput recipeOutput, ItemLike wood, ItemLike log) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, wood, 3)
                .define('#', log)
                .pattern("##")
                .pattern("##")
                .group("bark")
                .unlockedBy("has_log", has(log))
                .save(recipeOutput);
    }

    public static void woodenBoat(RecipeOutput recipeOutput, ItemLike boat, ItemLike material) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, boat)
                .define('#', material)
                .pattern("# #")
                .pattern("###")
                .group("boat")
                .unlockedBy("in_water", insideOf(Blocks.WATER))
                .save(recipeOutput);
    }

    public static void chestBoat(RecipeOutput recipeOutput, ItemLike boat, ItemLike material) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.TRANSPORTATION, boat)
                .requires(Blocks.CHEST)
                .requires(material)
                .group("chest_boat")
                .unlockedBy("has_boat", has(ItemTags.BOATS))
                .save(recipeOutput);
    }

    public static RecipeBuilder buttonBuilder(ItemLike button, Ingredient material) {
        return ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, button).requires(material);
    }

    public static RecipeBuilder doorBuilder(ItemLike door, Ingredient material) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, door, 3).define('#', material).pattern("##").pattern("##").pattern("##");
    }

    public static RecipeBuilder fenceBuilder(ItemLike fence, Ingredient material) {
        int i = fence == Blocks.NETHER_BRICK_FENCE ? 6 : 3;
        Item item = fence == Blocks.NETHER_BRICK_FENCE ? Items.NETHER_BRICK : Items.STICK;
        return ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, fence, i).define('W', material).define('#', item).pattern("W#W").pattern("W#W");
    }

    public static RecipeBuilder fenceGateBuilder(ItemLike fenceGate, Ingredient material) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, fenceGate).define('#', Items.STICK).define('W', material).pattern("#W#").pattern("#W#");
    }

    public static void pressurePlate(RecipeOutput recipeOutput, ItemLike pressurePlate, ItemLike material) {
        pressurePlateBuilder(RecipeCategory.REDSTONE, pressurePlate, Ingredient.of(material)).unlockedBy(getHasName(material), has(material)).save(recipeOutput);
    }

    public static RecipeBuilder pressurePlateBuilder(RecipeCategory category, ItemLike pressurePlate, Ingredient material) {
        return ShapedRecipeBuilder.shaped(category, pressurePlate).define('#', material).pattern("##");
    }

    public static void slab(RecipeOutput recipeOutput, RecipeCategory category, ItemLike slab, ItemLike material) {
        slabBuilder(category, slab, Ingredient.of(material)).unlockedBy(getHasName(material), has(material)).save(recipeOutput);
    }

    public static RecipeBuilder slabBuilder(RecipeCategory category, ItemLike slab, Ingredient material) {
        return ShapedRecipeBuilder.shaped(category, slab, 6).define('#', material).pattern("###");
    }

    public static RecipeBuilder stairBuilder(ItemLike stairs, Ingredient material) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, stairs, 4).define('#', material).pattern("#  ").pattern("## ").pattern("###");
    }

    public static RecipeBuilder trapdoorBuilder(ItemLike trapdoor, Ingredient material) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, trapdoor, 2).define('#', material).pattern("###").pattern("###");
    }

    public static RecipeBuilder signBuilder(ItemLike sign, Ingredient material) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, sign, 3)
                .group("sign")
                .define('#', material)
                .define('X', Items.STICK)
                .pattern("###")
                .pattern("###")
                .pattern(" X ");
    }

    public static void hangingSign(RecipeOutput recipeOutput, ItemLike sign, ItemLike material) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, sign, 6)
                .group("hanging_sign")
                .define('#', material)
                .define('X', Items.CHAIN)
                .pattern("X X")
                .pattern("###")
                .pattern("###")
                .unlockedBy("has_stripped_logs", has(material))
                .save(recipeOutput);
    }

    public static void colorBlockWithDye(RecipeOutput recipeOutput, List<Item> dyes, List<Item> dyeableItems, String group) {
        for(int i = 0; i < dyes.size(); ++i) {
            Item item = dyes.get(i);
            Item item2 = dyeableItems.get(i);
            ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, item2)
                    .requires(item)
                    .requires(Ingredient.of(dyeableItems.stream().filter(item2x -> !item2x.equals(item2)).map(ItemStack::new)))
                    .group(group)
                    .unlockedBy("has_needed_dye", has(item))
                    .save(recipeOutput, "dye_" + getItemName(item2));
        }
    }

    public static void carpet(RecipeOutput recipeOutput, ItemLike carpet, ItemLike material) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, carpet, 3)
                .define('#', material)
                .pattern("##")
                .group("carpet")
                .unlockedBy(getHasName(material), has(material))
                .save(recipeOutput);
    }

    public static void bedFromPlanksAndWool(RecipeOutput recipeOutput, ItemLike bed, ItemLike wool) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, bed)
                .define('#', wool)
                .define('X', ItemTags.PLANKS)
                .pattern("###")
                .pattern("XXX")
                .group("bed")
                .unlockedBy(getHasName(wool), has(wool))
                .save(recipeOutput);
    }

    public static void banner(RecipeOutput recipeOutput, ItemLike banner, ItemLike material) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, banner)
                .define('#', material)
                .define('|', Items.STICK)
                .pattern("###")
                .pattern("###")
                .pattern(" | ")
                .group("banner")
                .unlockedBy(getHasName(material), has(material))
                .save(recipeOutput);
    }

    public static void stainedGlassFromGlassAndDye(RecipeOutput recipeOutput, ItemLike stainedGlass, ItemLike dye) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, stainedGlass, 8)
                .define('#', Blocks.GLASS)
                .define('X', dye)
                .pattern("###")
                .pattern("#X#")
                .pattern("###")
                .group("stained_glass")
                .unlockedBy("has_glass", has(Blocks.GLASS))
                .save(recipeOutput);
    }

    public static void stainedGlassPaneFromStainedGlass(RecipeOutput recipeOutput, ItemLike stainedGlassPane, ItemLike stainedGlass) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, stainedGlassPane, 16)
                .define('#', stainedGlass)
                .pattern("###")
                .pattern("###")
                .group("stained_glass_pane")
                .unlockedBy("has_glass", has(stainedGlass))
                .save(recipeOutput);
    }

    public static void stainedGlassPaneFromGlassPaneAndDye(RecipeOutput recipeOutput, ItemLike stainedGlassPane, ItemLike dye) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, stainedGlassPane, 8)
                .define('#', Blocks.GLASS_PANE)
                .define('$', dye)
                .pattern("###")
                .pattern("#$#")
                .pattern("###")
                .group("stained_glass_pane")
                .unlockedBy("has_glass_pane", has(Blocks.GLASS_PANE))
                .unlockedBy(getHasName(dye), has(dye))
                .save(recipeOutput, getConversionRecipeName(stainedGlassPane, Blocks.GLASS_PANE));
    }

    public static void coloredTerracottaFromTerracottaAndDye(RecipeOutput recipeOutput, ItemLike terracotta, ItemLike dye) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, terracotta, 8)
                .define('#', Blocks.TERRACOTTA)
                .define('X', dye)
                .pattern("###")
                .pattern("#X#")
                .pattern("###")
                .group("stained_terracotta")
                .unlockedBy("has_terracotta", has(Blocks.TERRACOTTA))
                .save(recipeOutput);
    }

    public static void concretePowder(RecipeOutput recipeOutput, ItemLike concretePowder, ItemLike dye) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, concretePowder, 8)
                .requires(dye)
                .requires(Blocks.SAND, 4)
                .requires(Blocks.GRAVEL, 4)
                .group("concrete_powder")
                .unlockedBy("has_sand", has(Blocks.SAND))
                .unlockedBy("has_gravel", has(Blocks.GRAVEL))
                .save(recipeOutput);
    }

    public static void candle(RecipeOutput recipeOutput, ItemLike candle, ItemLike dye) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, candle)
                .requires(Blocks.CANDLE)
                .requires(dye)
                .group("dyed_candle")
                .unlockedBy(getHasName(dye), has(dye))
                .save(recipeOutput);
    }

    public static void wall(RecipeOutput recipeOutput, RecipeCategory category, ItemLike wall, ItemLike material) {
        wallBuilder(category, wall, Ingredient.of(material)).unlockedBy(getHasName(material), has(material)).save(recipeOutput);
    }

    public static RecipeBuilder wallBuilder(RecipeCategory category, ItemLike wall, Ingredient material) {
        return ShapedRecipeBuilder.shaped(category, wall, 6).define('#', material).pattern("###").pattern("###");
    }

    public static void polished(RecipeOutput recipeOutput, RecipeCategory category, ItemLike result, ItemLike material) {
        polishedBuilder(category, result, Ingredient.of(material)).unlockedBy(getHasName(material), has(material)).save(recipeOutput);
    }

    public static RecipeBuilder polishedBuilder(RecipeCategory category, ItemLike result, Ingredient material) {
        return ShapedRecipeBuilder.shaped(category, result, 4).define('S', material).pattern("SS").pattern("SS");
    }

    public static void cut(RecipeOutput recipeOutput, RecipeCategory category, ItemLike cutResult, ItemLike material) {
        cutBuilder(category, cutResult, Ingredient.of(material)).unlockedBy(getHasName(material), has(material)).save(recipeOutput);
    }

    public static ShapedRecipeBuilder cutBuilder(RecipeCategory category, ItemLike cutResult, Ingredient material) {
        return ShapedRecipeBuilder.shaped(category, cutResult, 4).define('#', material).pattern("##").pattern("##");
    }

    public static void chiseled(RecipeOutput recipeOutput, RecipeCategory category, ItemLike chiseledResult, ItemLike material) {
        chiseledBuilder(category, chiseledResult, Ingredient.of(material)).unlockedBy(getHasName(material), has(material)).save(recipeOutput);
    }

    public static void mosaicBuilder(RecipeOutput recipeOutput, RecipeCategory category, ItemLike result, ItemLike material) {
        ShapedRecipeBuilder.shaped(category, result)
                .define('#', material)
                .pattern("#")
                .pattern("#")
                .unlockedBy(getHasName(material), has(material))
                .save(recipeOutput);
    }

    public static ShapedRecipeBuilder chiseledBuilder(RecipeCategory category, ItemLike chiseledResult, Ingredient material) {
        return ShapedRecipeBuilder.shaped(category, chiseledResult).define('#', material).pattern("#").pattern("#");
    }

    public static void stonecutterResultFromBase(RecipeOutput recipeOutput, RecipeCategory category, ItemLike result, ItemLike material) {
        stonecutterResultFromBase(recipeOutput, category, result, material, 1);
    }

    public static void stonecutterResultFromBase(RecipeOutput recipeOutput, RecipeCategory category, ItemLike result, ItemLike material, int resultCount) {
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(material), category, result, resultCount)
                .unlockedBy(getHasName(material), has(material))
                .save(recipeOutput, getConversionRecipeName(result, material) + "_stonecutting");
    }

    public static void smeltingResultFromBase(RecipeOutput recipeOutput, ItemLike result, ItemLike ingredient) {
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(ingredient), RecipeCategory.BUILDING_BLOCKS, result, 0.1F, 200)
                .unlockedBy(getHasName(ingredient), has(ingredient))
                .save(recipeOutput);
    }

    public static void nineBlockStorageRecipes(
            RecipeOutput recipeOutput, RecipeCategory unpackedCategory, ItemLike unpacked, RecipeCategory packedCategory, ItemLike packed
    ) {
        nineBlockStorageRecipes(
                recipeOutput, unpackedCategory, unpacked, packedCategory, packed, getSimpleRecipeName(packed), null, getSimpleRecipeName(unpacked), null
        );
    }

    public static void nineBlockStorageRecipesWithCustomPacking(
            RecipeOutput recipeOutput,
            RecipeCategory unpackedCategory,
            ItemLike unpacked,
            RecipeCategory packedCategory,
            ItemLike packed,
            String packedName,
            String packedGroup
    ) {
        nineBlockStorageRecipes(recipeOutput, unpackedCategory, unpacked, packedCategory, packed, packedName, packedGroup, getSimpleRecipeName(unpacked), null);
    }

    public static void nineBlockStorageRecipesRecipesWithCustomUnpacking(
            RecipeOutput recipeOutput,
            RecipeCategory unpackedCategory,
            ItemLike unpacked,
            RecipeCategory packedCategory,
            ItemLike packed,
            String unpackedName,
            String unpackedGroup
    ) {
        nineBlockStorageRecipes(recipeOutput, unpackedCategory, unpacked, packedCategory, packed, getSimpleRecipeName(packed), null, unpackedName, unpackedGroup);
    }

    public static void nineBlockStorageRecipes(
            RecipeOutput recipeOutput,
            RecipeCategory unpackedCategory,
            ItemLike unpacked,
            RecipeCategory packedCategory,
            ItemLike packed,
            String packedName,
            @Nullable String packedGroup,
            String unpackedName,
            @Nullable String unpackedGroup
    ) {
        ShapelessRecipeBuilder.shapeless(unpackedCategory, unpacked, 9)
                .requires(packed)
                .group(unpackedGroup)
                .unlockedBy(getHasName(packed), has(packed))
                .save(recipeOutput, new ResourceLocation(unpackedName));
        ShapedRecipeBuilder.shaped(packedCategory, packed)
                .define('#', unpacked)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .group(packedGroup)
                .unlockedBy(getHasName(unpacked), has(unpacked))
                .save(recipeOutput, new ResourceLocation(packedName));
    }

    public static void copySmithingTemplate(RecipeOutput recipeOutput, ItemLike template, TagKey<Item> baseMaterial) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, template, 2)
                .define('#', Items.DIAMOND)
                .define('C', baseMaterial)
                .define('S', template)
                .pattern("#S#")
                .pattern("#C#")
                .pattern("###")
                .unlockedBy(getHasName(template), has(template))
                .save(recipeOutput);
    }

    public static void copySmithingTemplate(RecipeOutput recipeOutput, ItemLike template, ItemLike baseItem) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, template, 2)
                .define('#', Items.DIAMOND)
                .define('C', baseItem)
                .define('S', template)
                .pattern("#S#")
                .pattern("#C#")
                .pattern("###")
                .unlockedBy(getHasName(template), has(template))
                .save(recipeOutput);
    }

    public static <T extends AbstractCookingRecipe> void cookRecipes(
            RecipeOutput recipeOutput, String cookingMethod, RecipeSerializer<T> cookingSerializer, AbstractCookingRecipe.Factory<T> recipeFactory, int cookingTime
    ) {
        simpleCookingRecipe(recipeOutput, cookingMethod, cookingSerializer, recipeFactory, cookingTime, Items.BEEF, Items.COOKED_BEEF, 0.35F);
        simpleCookingRecipe(recipeOutput, cookingMethod, cookingSerializer, recipeFactory, cookingTime, Items.CHICKEN, Items.COOKED_CHICKEN, 0.35F);
        simpleCookingRecipe(recipeOutput, cookingMethod, cookingSerializer, recipeFactory, cookingTime, Items.COD, Items.COOKED_COD, 0.35F);
        simpleCookingRecipe(recipeOutput, cookingMethod, cookingSerializer, recipeFactory, cookingTime, Items.KELP, Items.DRIED_KELP, 0.1F);
        simpleCookingRecipe(recipeOutput, cookingMethod, cookingSerializer, recipeFactory, cookingTime, Items.SALMON, Items.COOKED_SALMON, 0.35F);
        simpleCookingRecipe(recipeOutput, cookingMethod, cookingSerializer, recipeFactory, cookingTime, Items.MUTTON, Items.COOKED_MUTTON, 0.35F);
        simpleCookingRecipe(recipeOutput, cookingMethod, cookingSerializer, recipeFactory, cookingTime, Items.PORKCHOP, Items.COOKED_PORKCHOP, 0.35F);
        simpleCookingRecipe(recipeOutput, cookingMethod, cookingSerializer, recipeFactory, cookingTime, Items.POTATO, Items.BAKED_POTATO, 0.35F);
        simpleCookingRecipe(recipeOutput, cookingMethod, cookingSerializer, recipeFactory, cookingTime, Items.RABBIT, Items.COOKED_RABBIT, 0.35F);
    }

    public static <T extends AbstractCookingRecipe> void simpleCookingRecipe(
            RecipeOutput recipeOutput,
            String cookingMethod,
            RecipeSerializer<T> cookingSerializer,
            AbstractCookingRecipe.Factory<T> recipeFactory,
            int cookingTime,
            ItemLike material,
            ItemLike result,
            float experience
    ) {
        SimpleCookingRecipeBuilder.generic(Ingredient.of(material), RecipeCategory.FOOD, result, experience, cookingTime, cookingSerializer, recipeFactory)
                .unlockedBy(getHasName(material), has(material))
                .save(recipeOutput, getItemName(result) + "_from_" + cookingMethod);
    }

    public static void waxRecipes(RecipeOutput recipeOutput, FeatureFlagSet requiredFeatures) {
        HoneycombItem.WAXABLES.get()
                .forEach(
                        (block, block2) -> {
                            if (block2.requiredFeatures().isSubsetOf(requiredFeatures)) {
                                ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, block2)
                                        .requires(block)
                                        .requires(Items.HONEYCOMB)
                                        .group(getItemName(block2))
                                        .unlockedBy(getHasName(block), has(block))
                                        .save(recipeOutput, getConversionRecipeName(block2, Items.HONEYCOMB));
                            }
                        }
                );
    }

    public static void grate(RecipeOutput recipeOutput, Block grateBlock, Block material) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, grateBlock, 4)
                .define('M', material)
                .pattern(" M ")
                .pattern("M M")
                .pattern(" M ")
                .unlockedBy(getHasName(material), has(material))
                .save(recipeOutput);
    }

    public static void copperBulb(RecipeOutput recipeOutput, Block bulbBlock, Block material) {
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, bulbBlock, 4)
                .define('C', material)
                .define('R', Items.REDSTONE)
                .define('B', Items.BLAZE_ROD)
                .pattern(" C ")
                .pattern("CBC")
                .pattern(" R ")
                .unlockedBy(getHasName(material), has(material))
                .save(recipeOutput);
    }

    public static void generateRecipes(RecipeOutput recipeOutput, BlockFamily blockFamily, FeatureFlagSet requiredFeatures) {
        blockFamily.getVariants()
                .forEach(
                        (variant, block) -> {
                            if (block.requiredFeatures().isSubsetOf(requiredFeatures)) {
                                BiFunction<ItemLike, ItemLike, RecipeBuilder> biFunction = SHAPE_BUILDERS.get(variant);
                                ItemLike itemLike = getBaseBlock(blockFamily, variant);
                                if (biFunction != null) {
                                    RecipeBuilder recipeBuilder = biFunction.apply(block, itemLike);
                                    blockFamily.getRecipeGroupPrefix()
                                            .ifPresent(string -> recipeBuilder.group(string + (variant == BlockFamily.Variant.CUT ? "" : "_" + variant.getRecipeGroup())));
                                    recipeBuilder.unlockedBy(blockFamily.getRecipeUnlockedBy().orElseGet(() -> getHasName(itemLike)), has(itemLike));
                                    recipeBuilder.save(recipeOutput);
                                }

                                if (variant == BlockFamily.Variant.CRACKED) {
                                    smeltingResultFromBase(recipeOutput, block, itemLike);
                                }
                            }
                        }
                );
    }

    public static Block getBaseBlock(BlockFamily family, BlockFamily.Variant variant) {
        if (variant == BlockFamily.Variant.CHISELED) {
            if (!family.getVariants().containsKey(BlockFamily.Variant.SLAB)) {
                throw new IllegalStateException("Slab is not defined for the family.");
            } else {
                return family.get(BlockFamily.Variant.SLAB);
            }
        } else {
            return family.getBaseBlock();
        }
    }

    public static Criterion<EnterBlockTrigger.TriggerInstance> insideOf(Block block) {
        return CriteriaTriggers.ENTER_BLOCK
                .createCriterion(new EnterBlockTrigger.TriggerInstance(Optional.empty(), Optional.of(block.builtInRegistryHolder()), Optional.empty()));
    }

    public static Criterion<InventoryChangeTrigger.TriggerInstance> has(MinMaxBounds.Ints count, ItemLike item) {
        return inventoryTrigger(ItemPredicate.Builder.item().of(item).withCount(count));
    }

    public static Criterion<InventoryChangeTrigger.TriggerInstance> has(ItemLike itemLike) {
        return inventoryTrigger(ItemPredicate.Builder.item().of(itemLike));
    }

    public static Criterion<InventoryChangeTrigger.TriggerInstance> has(TagKey<Item> tag) {
        return inventoryTrigger(ItemPredicate.Builder.item().of(tag));
    }

    public static Criterion<InventoryChangeTrigger.TriggerInstance> inventoryTrigger(ItemPredicate.Builder... items) {
        return inventoryTrigger((ItemPredicate[])Arrays.stream(items).map(ItemPredicate.Builder::build).toArray(ItemPredicate[]::new));
    }

    public static Criterion<InventoryChangeTrigger.TriggerInstance> inventoryTrigger(ItemPredicate... predicates) {
        return CriteriaTriggers.INVENTORY_CHANGED
                .createCriterion(new InventoryChangeTrigger.TriggerInstance(Optional.empty(), InventoryChangeTrigger.TriggerInstance.Slots.ANY, List.of(predicates)));
    }

    public static String getHasName(ItemLike itemLike) {
        return "has_" + getItemName(itemLike);
    }

    public static String getItemName(ItemLike itemLike) {
        return BuiltInRegistries.ITEM.getKey(itemLike.asItem()).getPath();
    }

    public static String getSimpleRecipeName(ItemLike itemLike) {
        return getItemName(itemLike);
    }

    public static String getConversionRecipeName(ItemLike result, ItemLike ingredient) {
        return getItemName(result) + "_from_" + getItemName(ingredient);
    }

    public static String getSmeltingRecipeName(ItemLike itemLike) {
        return getItemName(itemLike) + "_from_smelting";
    }

    public static String getBlastingRecipeName(ItemLike itemLike) {
        return getItemName(itemLike) + "_from_blasting";
    }

    @Override
    public String getName() {
        return "Recipes";
    }
}
