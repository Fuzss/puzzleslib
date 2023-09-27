package fuzs.puzzleslib.impl;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.data.v2.AbstractRecipeProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.puzzleslib.api.data.v2.core.DataProviderHelper;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

import java.util.function.Consumer;

@Mod(PuzzlesLib.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class PuzzlesLibForge {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ModConstructor.construct(PuzzlesLib.MOD_ID, PuzzlesLibMod::new);
        DataProviderHelper.registerDataProviders(PuzzlesLib.MOD_ID, ModRecipeProvider::new);
    }

    private static class ModRecipeProvider extends AbstractRecipeProvider {

        public ModRecipeProvider(DataProviderContext context) {
            super(context);
        }

        @Override
        public void addRecipes(Consumer<FinishedRecipe> exporter) {
            stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.COBBLED_DEEPSLATE, Blocks.DEEPSLATE);
            stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.COBBLED_DEEPSLATE_SLAB, Blocks.DEEPSLATE, 2);
            stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.COBBLED_DEEPSLATE_STAIRS, Blocks.DEEPSLATE);
            stonecutterResultFromBase(exporter, RecipeCategory.DECORATIONS, Blocks.COBBLED_DEEPSLATE_WALL, Blocks.DEEPSLATE);
            stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_DEEPSLATE, Blocks.DEEPSLATE);
            stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_DEEPSLATE, Blocks.DEEPSLATE);
            stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_DEEPSLATE_SLAB, Blocks.DEEPSLATE, 2);
            stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_DEEPSLATE_STAIRS, Blocks.DEEPSLATE);
            stonecutterResultFromBase(exporter, RecipeCategory.DECORATIONS, Blocks.POLISHED_DEEPSLATE_WALL, Blocks.DEEPSLATE);
            stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_BRICKS, Blocks.DEEPSLATE);
            stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_BRICK_SLAB, Blocks.DEEPSLATE, 2);
            stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_BRICK_STAIRS, Blocks.DEEPSLATE);
            stonecutterResultFromBase(exporter, RecipeCategory.DECORATIONS, Blocks.DEEPSLATE_BRICK_WALL, Blocks.DEEPSLATE);
            stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_TILES, Blocks.DEEPSLATE);
            stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_TILE_SLAB, Blocks.DEEPSLATE, 2);
            stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_TILE_STAIRS, Blocks.DEEPSLATE);
            stonecutterResultFromBase(exporter, RecipeCategory.DECORATIONS, Blocks.DEEPSLATE_TILE_WALL, Blocks.DEEPSLATE);
        }
    }
}
