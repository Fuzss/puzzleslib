package fuzs.puzzleslib.impl.client;

import com.google.common.base.Suppliers;
import com.google.common.collect.Maps;
import fuzs.puzzleslib.api.event.v1.core.EventResultHolder;
import net.minecraft.Util;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class BlockModelHandler {
    private static final Supplier<Map<ModelResourceLocation, ModelResourceLocation>> MODEL_LOCATIONS;

    static {
        MODEL_LOCATIONS = Suppliers.memoize(() -> {
            return Map.of(Blocks.SPRUCE_STAIRS, Blocks.OAK_STAIRS, Blocks.BIRCH_STAIRS, Blocks.OAK_STAIRS, Blocks.JUNGLE_STAIRS, Blocks.OAK_STAIRS, Blocks.DARK_OAK_STAIRS, Blocks.OAK_STAIRS, Blocks.ACACIA_STAIRS, Blocks.OAK_STAIRS, Blocks.MANGROVE_STAIRS, Blocks.OAK_STAIRS, Blocks.CHERRY_STAIRS, Blocks.OAK_STAIRS, Blocks.BAMBOO_STAIRS, Blocks.OAK_STAIRS).entrySet().stream().flatMap(entry -> {
                return convertAllBlockStates(entry.getKey(), entry.getValue()).entrySet().stream();
            }).collect(Util.toMap());
        });
    }

    public static EventResultHolder<UnbakedModel> onModifyUnbakedModel(ModelResourceLocation modelLocation, Supplier<UnbakedModel> unbakedModel, Function<ModelResourceLocation, UnbakedModel> modelGetter, BiConsumer<ResourceLocation, UnbakedModel> modelAdder) {
        if (MODEL_LOCATIONS.get().containsKey(modelLocation)) {
            return EventResultHolder.interrupt(modelGetter.apply(MODEL_LOCATIONS.get().get(modelLocation)));
        } else {
            return EventResultHolder.pass();
        }
    }

    private static Map<ModelResourceLocation, ModelResourceLocation> convertAllBlockStates(Block oldBlock, Block newBlock) {
        Map<ModelResourceLocation, ModelResourceLocation> modelLocations = Maps.newHashMap();
        for (BlockState oldBlockState : oldBlock.getStateDefinition().getPossibleStates()) {
            BlockState newBlockState = convertBlockState(newBlock.getStateDefinition(), oldBlockState);
            modelLocations.put(BlockModelShaper.stateToModelLocation(oldBlockState), BlockModelShaper.stateToModelLocation(newBlockState));
        }
        return modelLocations;
    }

    private static BlockState convertBlockState(StateDefinition<Block, BlockState> newStateDefinition, BlockState oldBlockState) {
        BlockState newBlockState = newStateDefinition.any();
        for (Map.Entry<Property<?>, Comparable<?>> entry : oldBlockState.getValues().entrySet()) {
            newBlockState = setBlockStateValue(entry.getKey(), entry.getValue(), newStateDefinition::getProperty, newBlockState);
        }
        return newBlockState;
    }

    private static <T extends Comparable<T>, V extends T> BlockState setBlockStateValue(Property<?> oldProperty, Comparable<?> oldValue, Function<String, @Nullable Property<?>> propertyGetter, BlockState blockState) {
        Property<?> newProperty = propertyGetter.apply(oldProperty.getName());
        if (newProperty != null) {
            return blockState.setValue((Property<T>) newProperty, (V) oldValue);
        }
        return blockState;
    }
}
