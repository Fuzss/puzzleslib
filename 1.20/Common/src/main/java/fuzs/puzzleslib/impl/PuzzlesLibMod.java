package fuzs.puzzleslib.impl;

import fuzs.puzzleslib.api.core.v1.CommonAbstractions;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.core.v1.context.PackRepositorySourcesContext;
import fuzs.puzzleslib.api.data.v2.AbstractRecipeProvider;
import fuzs.puzzleslib.api.network.v3.NetworkHandlerV3;
import fuzs.puzzleslib.api.resources.v1.DynamicPackResources;
import fuzs.puzzleslib.api.resources.v1.PackResourcesHelper;
import fuzs.puzzleslib.impl.capability.ClientboundSyncCapabilityMessage;
import fuzs.puzzleslib.impl.core.ClientboundModListMessage;
import fuzs.puzzleslib.impl.core.EventHandlerProvider;
import fuzs.puzzleslib.impl.entity.ClientboundAddEntityDataMessage;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.level.block.Blocks;

import java.util.function.Consumer;

/**
 * This has been separated from {@link PuzzlesLib} to prevent issues with static initialization when accessing constants in {@link PuzzlesLib} early.
 */
public class PuzzlesLibMod extends PuzzlesLib implements ModConstructor {
    public static final NetworkHandlerV3 NETWORK = NetworkHandlerV3.builder(MOD_ID)
            .registerSerializer(ClientboundAddEntityPacket.class, (friendlyByteBuf, clientboundAddEntityPacket) -> clientboundAddEntityPacket.write(friendlyByteBuf), ClientboundAddEntityPacket::new)
            .allAcceptVanillaOrMissing()
            .registerClientbound(ClientboundSyncCapabilityMessage.class)
            .registerClientbound(ClientboundAddEntityDataMessage.class)
            .registerClientbound(ClientboundModListMessage.class);

    @Override
    public void onConstructMod() {
        EventHandlerProvider.tryRegister(CommonAbstractions.INSTANCE);
    }

    @Override
    public void onAddDataPackFinders(PackRepositorySourcesContext context) {
        context.addRepositorySource(PackResourcesHelper.buildServerPack(id("dynamic_recipes"), DynamicPackResources.create(dataProviderContext -> {
            return new AbstractRecipeProvider(dataProviderContext) {

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
            };
        })));
    }
}
