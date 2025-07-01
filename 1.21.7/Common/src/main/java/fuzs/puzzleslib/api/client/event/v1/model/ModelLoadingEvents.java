package fuzs.puzzleslib.api.client.event.v1.model;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResultHolder;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public final class ModelLoadingEvents {
    public static final EventInvoker<LoadModel> LOAD_MODEL = EventInvoker.lookup(LoadModel.class);
    public static final EventInvoker<LoadBlockModel> LOAD_BLOCK_MODEL = EventInvoker.lookup(LoadBlockModel.class);

    private ModelLoadingEvents() {
        // NO-OP
    }

    @FunctionalInterface
    public interface LoadModel {

        /**
         * An event that runs for every unbaked model. Allows for replacing the model.
         *
         * @param resourceLocation the resource location for the model
         * @param unbakedModel     the unbaked model
         * @return <ul>
         *         <li>{@link EventResultHolder#interrupt(Object)} to replace the model</li>
         *         <li>{@link EventResultHolder#pass()} to allow the original model to be used</li>
         *         </ul>
         */
        EventResultHolder<UnbakedModel> onLoadModel(ResourceLocation resourceLocation, UnbakedModel unbakedModel);
    }

    @FunctionalInterface
    public interface LoadBlockModel {

        /**
         * An event that runs for every unbaked block model. Allows for replacing the model.
         *
         * @param blockState   the block state the model is loaded for
         * @param unbakedModel the unbaked block model
         * @return <ul>
         *         <li>{@link EventResultHolder#interrupt(Object)} to replace the model</li>
         *         <li>{@link EventResultHolder#pass()} to allow the original model to be used</li>
         *         </ul>
         */
        EventResultHolder<BlockStateModel.UnbakedRoot> onLoadBlockModel(BlockState blockState, BlockStateModel.UnbakedRoot unbakedModel);
    }
}
