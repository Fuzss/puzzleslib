package fuzs.puzzleslib.fabric.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.BlockStateResolverContext;
import net.fabricmc.fabric.api.client.model.loading.v1.BlockStateResolver;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.renderer.block.model.UnbakedBlockStateModel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public record BlockStateResolverContextFabricImpl(ModelLoadingPlugin.Context context) implements BlockStateResolverContext {

    @Override
    public void registerBlockStateResolver(Block block, Consumer<BiConsumer<BlockState, UnbakedBlockStateModel>> blockStateConsumer) {
        this.context.registerBlockStateResolver(block, (BlockStateResolver.Context context) -> {
            blockStateConsumer.accept(context::setModel);
        });
    }
}
