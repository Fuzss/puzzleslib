package fuzs.puzzleslib.api.client.core.v1;

import fuzs.puzzleslib.api.client.core.v1.context.ClientTooltipComponentsContext;
import fuzs.puzzleslib.api.core.v1.ServiceProviderHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.searchtree.SearchRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

/**
 * useful methods for client related things that require mod loader specific abstractions
 */
public interface ClientAbstractions {
    /**
     * instance of the client factories SPI
     */
    ClientAbstractions INSTANCE = ServiceProviderHelper.load(ClientAbstractions.class);

    /**
     * checks if a <code>keyMapping</code> is active (=pressed), Forge replaces this everywhere, so we need an abstraction
     *
     * @param keyMapping the key mapping to check if pressed
     * @param keyCode    current key code
     * @param scanCode   scan code
     * @return is <code>keyMapping</code> active
     */
    boolean isKeyActiveAndMatches(KeyMapping keyMapping, int keyCode, int scanCode);

    /**
     * Converts an image {@link TooltipComponent} into the appropriate client-side component.
     * <p>{@link ClientTooltipComponent}s must first be registered in {@link ClientModConstructor#onRegisterClientTooltipComponents(ClientTooltipComponentsContext)},
     * otherwise {@link IllegalArgumentException} will be thrown.
     * <p>For simple text based components directly use {@link ClientTooltipComponent#create(FormattedCharSequence)} instead.
     *
     * @param imageComponent the un-sided {@link TooltipComponent} to convert
     * @return the client tooltip components representation
     */
    ClientTooltipComponent createImageComponent(TooltipComponent imageComponent);

    /**
     * Retrieves a model from the {@link ModelManager}, allows for using {@link ResourceLocation} instead of {@link net.minecraft.client.resources.model.ModelResourceLocation}.
     *
     * @param identifier   model identifier
     * @return the model, possibly the missing model
     */
    BakedModel getBakedModel(ResourceLocation identifier);

    /**
     * Retrieves a model from the {@link ModelManager}, allows for using {@link ResourceLocation} instead of {@link net.minecraft.client.resources.model.ModelResourceLocation}.
     *
     * @param modelManager the model manager instance for retrieving the model
     * @param identifier   model identifier
     * @return the model, possibly the missing model
     */
    @Deprecated(forRemoval = true)
    default BakedModel getBakedModel(ModelManager modelManager, ResourceLocation identifier) {
        return this.getBakedModel(identifier);
    }

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
     * Get the current partial tick time.
     * <p>This is different from {@link Minecraft#getFrameTime()} in that the correct value is returned when the game is paused in {@link Minecraft#isPaused()}.
     *
     * @return current partial tick time
     */
    float getPartialTick();

    /**
     * Get the current partial tick time.
     * <p>This is different from {@link Minecraft#getFrameTime()} in that the correct value is returned when the game is paused in {@link Minecraft#isPaused()}.
     *
     * @param minecraft minecraft singleton instance
     * @return current partial tick time
     */
    @Deprecated(forRemoval = true)
    default float getPartialTick(Minecraft minecraft) {
        return this.getPartialTick();
    }

    /**
     * Get the search registry for registering a new search tree via {@link SearchRegistry#register(SearchRegistry.Key, SearchRegistry.TreeBuilderSupplier)}.
     *
     * @return the search registry
     */
    SearchRegistry getSearchRegistry();

    /**
     * Get the search registry for registering a new search tree via {@link SearchRegistry#register(SearchRegistry.Key, SearchRegistry.TreeBuilderSupplier)}.
     *
     * @param minecraft minecraft singleton instance
     * @return the search registry
     */
    @Deprecated(forRemoval = true)
    default SearchRegistry getSearchRegistry(Minecraft minecraft) {
        return this.getSearchRegistry();
    }
}
