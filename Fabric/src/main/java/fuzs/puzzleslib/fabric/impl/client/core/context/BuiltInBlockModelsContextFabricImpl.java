package fuzs.puzzleslib.fabric.impl.client.core.context;

import fuzs.puzzleslib.common.api.client.core.v1.context.BuiltInBlockModelsContext;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltInBlockModelsCallback;
import net.minecraft.client.renderer.block.BuiltInBlockModels;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public final class BuiltInBlockModelsContextFabricImpl implements BuiltInBlockModelsContext {
    private final List<Consumer<BuiltInBlockModels.Builder>> blockModels = new ArrayList<>();

    @Override
    public void registerUnbakedBlockModel(Block block, BlockModel.Unbaked unbakedBlockModel) {
        Objects.requireNonNull(block, "block is null");
        Objects.requireNonNull(unbakedBlockModel, "unbaked block model is null");
        this.registerIfNecessary();
        this.blockModels.add((BuiltInBlockModels.Builder builder) -> {
            builder.put(unbakedBlockModel, block);
        });
    }

    @Override
    public void registerModelFactory(Block block, BuiltInBlockModels.ModelFactory modelFactory) {
        Objects.requireNonNull(block, "block is null");
        Objects.requireNonNull(modelFactory, "model factory is null");
        this.registerIfNecessary();
        this.blockModels.add((BuiltInBlockModels.Builder builder) -> {
            builder.put(modelFactory, block);
        });
    }

    private void registerIfNecessary() {
        if (this.blockModels.isEmpty()) {
            BuiltInBlockModelsCallback.EVENT.register((BuiltInBlockModels.Builder context) -> {
                for (Consumer<BuiltInBlockModels.Builder> blockModel : this.blockModels) {
                    blockModel.accept(context);
                }
            });
        }
    }
}
