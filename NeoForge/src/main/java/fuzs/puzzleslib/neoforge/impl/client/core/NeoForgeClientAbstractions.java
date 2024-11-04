package fuzs.puzzleslib.neoforge.impl.client.core;

import com.mojang.blaze3d.platform.InputConstants;
import fuzs.puzzleslib.api.client.core.v1.ClientAbstractions;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.ClientHooks;

import java.util.List;
import java.util.Objects;

public final class NeoForgeClientAbstractions implements ClientAbstractions {

    @Override
    public boolean hasChannel(ClientPacketListener clientPacketListener, CustomPacketPayload.Type<?> type) {
        Objects.requireNonNull(clientPacketListener, "client packet listener is null");
        return clientPacketListener.hasChannel(type);
    }

    @Override
    public boolean isKeyActiveAndMatches(KeyMapping keyMapping, int keyCode, int scanCode) {
        return keyMapping.isActiveAndMatches(InputConstants.getKey(keyCode, scanCode));
    }

    @Override
    public ClientTooltipComponent createImageComponent(TooltipComponent imageComponent) {
        return ClientTooltipComponent.create(imageComponent);
    }

    @Override
    public BakedModel getBakedModel(ModelManager modelManager, ResourceLocation resourceLocation) {
        return modelManager.getModel(ModelResourceLocation.standalone(resourceLocation));
    }

    @SuppressWarnings("deprecation")
    @Override
    public RenderType getRenderType(Block block) {
        ChunkRenderTypeSet renderTypes = ItemBlockRenderTypes.getRenderLayers(block.defaultBlockState());
        return renderTypes.isEmpty() ? RenderType.solid() : renderTypes.iterator().next();
    }

    @Override
    public void registerRenderType(Block block, RenderType renderType) {
        ItemBlockRenderTypes.setRenderLayer(block, renderType);
    }

    @Override
    public void registerRenderType(Fluid fluid, RenderType renderType) {
        ItemBlockRenderTypes.setRenderLayer(fluid, renderType);
    }

    @Override
    public boolean onRenderTooltip(GuiGraphics guiGraphics, Font font, int mouseX, int mouseY, List<ClientTooltipComponent> components, ClientTooltipPositioner positioner) {
        return ClientHooks.onRenderTooltipPre(ItemStack.EMPTY, guiGraphics, mouseX, mouseY, guiGraphics.guiWidth(),
                guiGraphics.guiHeight(), components, font, positioner
        ).isCanceled();
    }

    @Override
    public int getGuiLeftHeight(Gui gui) {
        return gui.leftHeight;
    }

    @Override
    public int getGuiRightHeight(Gui gui) {
        return gui.rightHeight;
    }

    @Override
    public void addGuiLeftHeight(Gui gui, int leftHeight) {
        gui.leftHeight += leftHeight;
    }

    @Override
    public void addGuiRightHeight(Gui gui, int rightHeight) {
        gui.rightHeight += rightHeight;
    }
}
