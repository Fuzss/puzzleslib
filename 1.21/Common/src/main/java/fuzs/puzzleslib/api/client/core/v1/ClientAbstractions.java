package fuzs.puzzleslib.api.client.core.v1;

import fuzs.puzzleslib.api.client.core.v1.context.ClientTooltipComponentsContext;
import fuzs.puzzleslib.api.core.v1.ServiceProviderHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.multiplayer.SessionSearchTrees;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
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
     * checks if a <code>keyMapping</code> is active (=pressed), Forge replaces this everywhere, so we need an
     * abstraction
     *
     * @param keyMapping the key mapping to check if pressed
     * @param keyCode    current key code
     * @param scanCode   scan code
     * @return is <code>keyMapping</code> active
     */
    boolean isKeyActiveAndMatches(KeyMapping keyMapping, int keyCode, int scanCode);

    /**
     * Converts an image {@link TooltipComponent} into the appropriate client-side component.
     * <p>{@link ClientTooltipComponent}s must first be registered in
     * {@link ClientModConstructor#onRegisterClientTooltipComponents(ClientTooltipComponentsContext)},
     * otherwise {@link IllegalArgumentException} will be thrown.
     * <p>For simple text based components directly use {@link ClientTooltipComponent#create(FormattedCharSequence)}
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
     * @param identifier model identifier
     * @return the model, possibly the missing model
     */
    BakedModel getBakedModel(ResourceLocation identifier);

    /**
     * Allows for retrieving the {@link RenderType} that has been registered for a block.
     * <p>When not render type is registered {@link RenderType#solid()} is returned.
     *
     * @param block the block to get the render type for
     * @return the render type
     */
    RenderType getRenderType(Block block);

    /**
     * Allows for retrieving the {@link RenderType} that has been registered for a fluid.
     * <p>When not render type is registered {@link RenderType#solid()} is returned.
     *
     * @param fluid the fluid to get the render type for
     * @return the render type
     */
    default RenderType getRenderType(Fluid fluid) {
        return ItemBlockRenderTypes.getRenderLayer(fluid.defaultFluidState());
    }

    /**
     * Allows for registering a {@link RenderType} for a block.
     * <p>When not render type is registered {@link RenderType#solid()} is used.
     *
     * @param block      the block to register the render type for
     * @param renderType the render type
     */
    void registerRenderType(Block block, RenderType renderType);

    /**
     * Allows for registering a {@link RenderType} for a fluid.
     * <p>When not render type is registered {@link RenderType#solid()} is used.
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
     * Get the search registry for registering a new search tree via
     * {@link SearchRegistry#register(SearchRegistry.Key, SearchRegistry.TreeBuilderSupplier)}.
     *
     * @return the search registry
     */
    default SessionSearchTrees getSearchRegistry() {
        return Minecraft.getInstance().getConnection().searchTrees();
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
}
