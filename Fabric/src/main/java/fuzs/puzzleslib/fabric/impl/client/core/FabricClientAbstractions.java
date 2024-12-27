package fuzs.puzzleslib.fabric.impl.client.core;

import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.client.ConfigScreenFactoryRegistry;
import fuzs.puzzleslib.api.client.core.v1.ClientAbstractions;
import fuzs.puzzleslib.fabric.api.client.event.v1.FabricGuiEvents;
import fuzs.puzzleslib.fabric.impl.client.config.MultiConfigurationScreen;
import fuzs.puzzleslib.fabric.impl.client.event.FabricGuiEventHelper;
import fuzs.puzzleslib.impl.core.EventHandlerProvider;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

import java.util.List;
import java.util.Objects;

public final class FabricClientAbstractions implements ClientAbstractions, EventHandlerProvider {

    @Override
    public boolean hasChannel(ClientPacketListener clientPacketListener, CustomPacketPayload.Type<?> type) {
        return ClientPlayNetworking.canSend(type);
    }

    @Override
    public boolean isKeyActiveAndMatches(KeyMapping keyMapping, int keyCode, int scanCode) {
        return keyMapping.matches(keyCode, scanCode);
    }

    @Override
    public ClientTooltipComponent createImageComponent(TooltipComponent imageComponent) {
        ClientTooltipComponent component = TooltipComponentCallback.EVENT.invoker().getComponent(imageComponent);
        return Objects.requireNonNullElseGet(component, () -> ClientTooltipComponent.create(imageComponent));
    }

    @Override
    public BakedModel getBakedModel(ModelManager modelManager, ResourceLocation resourceLocation) {
        return modelManager.getModel(resourceLocation);
    }

    @Override
    public RenderType getRenderType(Block block) {
        return ItemBlockRenderTypes.getChunkRenderType(block.defaultBlockState());
    }

    @Override
    public void registerRenderType(Block block, RenderType renderType) {
        BlockRenderLayerMap.INSTANCE.putBlock(block, renderType);
    }

    @Override
    public void registerRenderType(Fluid fluid, RenderType renderType) {
        BlockRenderLayerMap.INSTANCE.putFluid(fluid, renderType);
    }

    @Override
    public float getPartialTick(EntityRenderState renderState) {
        return Mth.frac(renderState.ageInTicks);
    }

    @Override
    public boolean onRenderTooltip(GuiGraphics guiGraphics, Font font, int mouseX, int mouseY, List<ClientTooltipComponent> components, ClientTooltipPositioner positioner) {
        return FabricGuiEvents.RENDER_TOOLTIP.invoker()
                .onRenderTooltip(guiGraphics, font, mouseX, mouseY, components, positioner)
                .isInterrupt();
    }

    @Override
    public int getGuiLeftHeight(Gui gui) {
        return FabricGuiEventHelper.getGuiLeftHeight();

    }

    @Override
    public int getGuiRightHeight(Gui gui) {
        return FabricGuiEventHelper.getGuiRightHeight();
    }

    @Override
    public void addGuiLeftHeight(Gui gui, int leftHeight) {
        FabricGuiEventHelper.setGuiLeftHeight(this.getGuiLeftHeight(gui) + leftHeight);
    }

    @Override
    public void addGuiRightHeight(Gui gui, int rightHeight) {
        FabricGuiEventHelper.setGuiRightHeight(this.getGuiRightHeight(gui) + rightHeight);
    }

    public void registerConfigScreenFactory(String modId, String... mergedModIds) {
        ConfigScreenFactoryRegistry.INSTANCE.register(modId, MultiConfigurationScreen.getFactory(mergedModIds));
    }

    @Override
    public void registerEventHandlers() {
        FabricGuiEventHelper.registerEventHandlers();
    }
}
