package fuzs.puzzleslib.impl.client.core.contexts;

import fuzs.puzzleslib.api.client.core.v1.contexts.RenderTypesContext;
import fuzs.puzzleslib.api.core.v1.contexts.MultiRegistrationContext;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;

public final class BlockRenderTypesContextFabricImpl implements RenderTypesContext<Block>, MultiRegistrationContext<Block, RenderType> {

    @Override
    public void registerRenderType(RenderType renderType, Block object, Block... objects) {
        this.register(renderType, object, objects);
    }

    @Override
    public void register(Block object, RenderType type) {
        BlockRenderLayerMap.INSTANCE.putBlock(object, type);
    }
}
