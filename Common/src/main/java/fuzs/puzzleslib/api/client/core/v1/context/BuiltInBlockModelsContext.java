package fuzs.puzzleslib.api.client.core.v1.context;

import net.minecraft.client.renderer.block.BuiltInBlockModels;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.world.level.block.Block;

public interface BuiltInBlockModelsContext {

    /**
     * Register a custom unbaked special model renderer implementation to be used for statically rendered blocks, such
     * as blocks visually appearing in minecarts and held by enderman.
     *
     * @param block             the block requiring a special model renderer
     * @param unbakedBlockModel the unbaked block model
     */
    void registerUnbakedBlockModel(Block block, BlockModel.Unbaked unbakedBlockModel);

    /**
     * Register a custom unbaked special model renderer implementation to be used for statically rendered blocks, such
     * as blocks visually appearing in minecarts and held by enderman.
     *
     * @param block        the block requiring a special model renderer
     * @param modelFactory the block model factory
     */
    void registerModelFactory(Block block, BuiltInBlockModels.ModelFactory modelFactory);
}
