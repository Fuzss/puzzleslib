package fuzs.puzzleslib.api.client.event.v1.model;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResultHolder;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public final class ModelBakingEvents {
    public static final EventInvoker<BeforeBlock> BEFORE_BLOCK = EventInvoker.lookup(BeforeBlock.class);
    public static final EventInvoker<AfterBlock> AFTER_BLOCK = EventInvoker.lookup(AfterBlock.class);
    public static final EventInvoker<BeforeItem> BEFORE_ITEM = EventInvoker.lookup(BeforeItem.class);
    public static final EventInvoker<AfterItem> AFTER_ITEM = EventInvoker.lookup(AfterItem.class);

    private ModelBakingEvents() {
        // NO-OP
    }

    @FunctionalInterface
    public interface BeforeBlock {

        /**
         * An event that runs for every unbaked block model. Allows for replacing the model.
         *
         * @param blockState   the block state the model is loaded for
         * @param unbakedModel the unbaked block model
         * @param modelBaker   the model baker instance
         * @return <ul>
         *         <li>{@link EventResultHolder#interrupt(Object)} to replace the model</li>
         *         <li>{@link EventResultHolder#pass()} to allow the original model to be used</li>
         *         </ul>
         */
        EventResultHolder<BlockStateModel.UnbakedRoot> onBeforeBakeBlock(BlockState blockState, BlockStateModel.UnbakedRoot unbakedModel, ModelBaker modelBaker);
    }

    @FunctionalInterface
    public interface AfterBlock {

        /**
         * An event that runs for every baked block model. Allows for replacing the model.
         *
         * @param blockState   the block state the model is loaded for
         * @param bakedModel   the baked block model
         * @param unbakedModel the original unbaked block model
         * @param modelBaker   the model baker instance
         * @return <ul>
         *         <li>{@link EventResultHolder#interrupt(Object)} to replace the model</li>
         *         <li>{@link EventResultHolder#pass()} to allow the original model to be used</li>
         *         </ul>
         */
        EventResultHolder<BlockStateModel> onAfterBakeBlock(BlockState blockState, BlockStateModel bakedModel, BlockStateModel.UnbakedRoot unbakedModel, ModelBaker modelBaker);
    }

    @FunctionalInterface
    public interface BeforeItem {

        /**
         * An event that runs for every unbaked item model. Allows for replacing the model.
         *
         * @param resourceLocation the resource location for the item
         * @param unbakedModel     the unbaked item model
         * @param context          the item baking context instance
         * @return <ul>
         *         <li>{@link EventResultHolder#interrupt(Object)} to replace the model</li>
         *         <li>{@link EventResultHolder#pass()} to allow the original model to be used</li>
         *         </ul>
         */
        EventResultHolder<ItemModel.Unbaked> onBeforeBakeItem(ResourceLocation resourceLocation, ItemModel.Unbaked unbakedModel, ItemModel.BakingContext context);
    }

    @FunctionalInterface
    public interface AfterItem {

        /**
         * An event that runs for every baked item model. Allows for replacing the model.
         *
         * @param resourceLocation the resource location for the item
         * @param bakedModel       the baked item model
         * @param unbakedModel     the original unbaked item model
         * @param context          the item baking context instance
         * @return <ul>
         *         <li>{@link EventResultHolder#interrupt(Object)} to replace the model</li>
         *         <li>{@link EventResultHolder#pass()} to allow the original model to be used</li>
         *         </ul>
         */
        EventResultHolder<ItemModel> onAfterBakeItem(ResourceLocation resourceLocation, ItemModel bakedModel, ItemModel.Unbaked unbakedModel, ItemModel.BakingContext context);
    }
}
