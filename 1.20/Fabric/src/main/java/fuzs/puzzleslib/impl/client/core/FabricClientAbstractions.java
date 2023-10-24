package fuzs.puzzleslib.impl.client.core;

import fuzs.puzzleslib.api.client.core.v1.ClientAbstractions;
import fuzs.puzzleslib.mixin.client.accessor.MinecraftFabricAccessor;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.searchtree.SearchRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.level.block.Block;

public final class FabricClientAbstractions implements ClientAbstractions {

    @Override
    public boolean isKeyActiveAndMatches(KeyMapping keyMapping, int keyCode, int scanCode) {
        return keyMapping.matches(keyCode, scanCode);
    }

    @Override
    public ClientTooltipComponent createImageComponent(TooltipComponent imageComponent) {
        ClientTooltipComponent component = TooltipComponentCallback.EVENT.invoker().getComponent(imageComponent);
        if (component != null) return component;
        return ClientTooltipComponent.create(imageComponent);
    }

    @Override
    public BakedModel getBakedModel(ResourceLocation identifier) {
        return Minecraft.getInstance().getModelManager().getModel(identifier);
    }

    @Override
    public RenderType getRenderType(Block block) {
        return ItemBlockRenderTypes.getChunkRenderType(block.defaultBlockState());
    }

    @Override
    public float getPartialTick() {
        Minecraft minecraft = Minecraft.getInstance();
        return minecraft.isPaused() ? ((MinecraftFabricAccessor) minecraft).puzzleslib$getPausePartialTick() : minecraft.getFrameTime();
    }

    @Override
    public SearchRegistry getSearchRegistry() {
        return ((MinecraftFabricAccessor) Minecraft.getInstance()).puzzleslib$getSearchRegistry();
    }
}
