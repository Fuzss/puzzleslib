package fuzs.puzzleslib.api.client.core.v1;

import fuzs.puzzleslib.api.client.core.v1.context.ClientTooltipComponentsContext;
import fuzs.puzzleslib.api.core.v1.ServiceProviderHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
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
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

import java.util.List;

/**
 * Useful methods for client related things that require mod loader specific abstractions.
 */
public interface ClientAbstractions {
    ClientAbstractions INSTANCE = ServiceProviderHelper.load(ClientAbstractions.class);

    /**
     * Checks if the connected server declared the ability to receive a specific type of packet.
     *
     * @param clientPacketListener the client packet listener
     * @param type                 the packet type
     * @return if the connected server has declared the ability to receive a specific type of packet
     */
    boolean hasChannel(ClientPacketListener clientPacketListener, CustomPacketPayload.Type<?> type);

    /**
     * Checks if a key mapping is pressed.
     * <p>
     * NeoForge replaces the vanilla call to {@link KeyMapping#matches(int, int)} in a few places to account for key
     * activation contexts (game &amp; screen environments).
     *
     * @param keyMapping the key mapping to check if pressed
     * @param keyCode    the current key code
     * @param scanCode   the key scan code
     * @return is the key mapping pressed
     */
    boolean isKeyActiveAndMatches(KeyMapping keyMapping, int keyCode, int scanCode);

    /**
     * Converts an image {@link TooltipComponent} into the appropriate client-side component.
     * <p>
     * {@link ClientTooltipComponent}s must first be registered in
     * {@link ClientModConstructor#onRegisterClientTooltipComponents(ClientTooltipComponentsContext)}, otherwise
     * {@link IllegalArgumentException} will be thrown.
     * <p>
     * For simple text based components directly use {@link ClientTooltipComponent#create(FormattedCharSequence)}
     * instead.
     *
     * @param imageComponent the un-sided {@link TooltipComponent} to convert
     * @return the client tooltip components representation
     */
    ClientTooltipComponent createImageComponent(TooltipComponent imageComponent);

    /**
     * Retrieves a model from the {@link ModelManager}, allows for using {@link ResourceLocation} instead of
     * {@link net.minecraft.client.resources.model.ModelResourceLocation}.
     *
     * @param resourceLocation the model resource location
     * @return the model, or the missing model if not found
     *
     * @deprecated use {@link #getBakedModel(ModelManager, ResourceLocation)}
     */
    @Deprecated(forRemoval = true)
    default BakedModel getBakedModel(ResourceLocation resourceLocation) {
        return this.getBakedModel(Minecraft.getInstance().getModelManager(), resourceLocation);
    }

    /**
     * Retrieves a model from the {@link ModelManager}, allows for using {@link ResourceLocation} instead of
     * {@link net.minecraft.client.resources.model.ModelResourceLocation}.
     *
     * @param modelManager     the model manager instance
     * @param resourceLocation the model resource location
     * @return the model, or the missing model if not found
     */
    BakedModel getBakedModel(ModelManager modelManager, ResourceLocation resourceLocation);

    /**
     * Allows for retrieving the {@link RenderType} that has been registered for a block.
     * <p>
     * When no render type is registered, {@link RenderType#solid()} is returned.
     *
     * @param block the block to get the render type for
     * @return the render type
     */
    RenderType getRenderType(Block block);

    /**
     * Allows for retrieving the {@link RenderType} that has been registered for a fluid.
     * <p>
     * When no render type is registered, {@link RenderType#solid()} is returned.
     *
     * @param fluid the fluid to get the render type for
     * @return the render type
     */
    default RenderType getRenderType(Fluid fluid) {
        return ItemBlockRenderTypes.getRenderLayer(fluid.defaultFluidState());
    }

    /**
     * Allows for registering a {@link RenderType} for a block.
     * <p>
     * When no render type is registered, {@link RenderType#solid()} is used.
     *
     * @param block      the block to register the render type for
     * @param renderType the render type
     */
    void registerRenderType(Block block, RenderType renderType);

    /**
     * Allows for registering a {@link RenderType} for a fluid.
     * <p>
     * When no render type is registered, {@link RenderType#solid()} is used.
     *
     * @param fluid      the fluid to register the render type for
     * @param renderType the render type
     */
    void registerRenderType(Fluid fluid, RenderType renderType);

    /**
     * Get the current partial tick time while taking into account whether the game is paused.
     *
     * @return current partial tick time
     */
    default float getPartialTick() {
        return Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false);
    }

    /**
     * Called just before a tooltip is drawn on a screen, allows for preventing the tooltip from drawing.
     *
     * @param guiGraphics the gui graphics instance
     * @param font        the font instance
     * @param mouseX      x position of the mouse cursor
     * @param mouseY      y position of the mouse cursor
     * @param components  components to render in the tooltip
     * @param positioner  positioner for placing the tooltip in relation to provided mouse coordinates
     * @return <code>true</code> to prevent the tooltip from rendering, allows for fully taking over rendering
     */
    boolean onRenderTooltip(GuiGraphics guiGraphics, Font font, int mouseX, int mouseY, List<ClientTooltipComponent> components, ClientTooltipPositioner positioner);

    /**
     * Returns the current render height for hotbar decorations on the left side.
     * <p>
     * In vanilla this includes player health and armor level.
     *
     * @param gui the gui instance
     * @return the hotbar decorations render height
     */
    int getGuiLeftHeight(Gui gui);

    /**
     * Returns the current render height for hotbar decorations on the right side.
     * <p>
     * In vanilla this includes player food level, vehicle health and air supply.
     *
     * @param gui the gui instance
     * @return the hotbar decorations render height
     */
    int getGuiRightHeight(Gui gui);

    /**
     * Add to the current render height for hotbar decorations on the left side.
     *
     * @param gui        the gui instance
     * @param leftHeight the additional hotbar decorations render height
     */
    void addGuiLeftHeight(Gui gui, int leftHeight);

    /**
     * Add to the current render height for hotbar decorations on the right side.
     *
     * @param gui         the gui instance
     * @param rightHeight the additional hotbar decorations render height
     */
    void addGuiRightHeight(Gui gui, int rightHeight);
}
