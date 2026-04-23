package fuzs.puzzleslib.neoforge.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.BuiltInBlockModelsContext;
import net.minecraft.client.renderer.block.BuiltInBlockModels;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.event.RegisterBlockModelsEvent;

import java.util.Objects;

public record BuiltInBlockModelsContextNeoForgeImpl(RegisterBlockModelsEvent event) implements BuiltInBlockModelsContext {

    @Override
    public void registerUnbakedBlockModel(Block block, BlockModel.Unbaked unbakedBlockModel) {
        Objects.requireNonNull(block, "block is null");
        Objects.requireNonNull(unbakedBlockModel, "unbaked block model is null");
        this.event.register(unbakedBlockModel, block);
    }

    @Override
    public void registerModelFactory(Block block, BuiltInBlockModels.ModelFactory modelFactory) {
        Objects.requireNonNull(block, "block is null");
        Objects.requireNonNull(modelFactory, "model factory is null");
        this.event.register(modelFactory, block);
    }
}
