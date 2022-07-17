package fuzs.puzzleslib.client.core;

import fuzs.puzzleslib.client.init.builder.ModScreenConstructor;
import fuzs.puzzleslib.client.init.builder.ModSpriteParticleRegistration;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * a base class for a mods main client class, contains a bunch of methods for registering various things
 */
public interface ClientModConstructor {

    /**
     * runs when the mod is first constructed, on the client only really used for registering event callbacks
     */
    default void onConstructMod() {

    }

    /**
     * runs after content has been registered, so it's safe to use here
     * used to set various values and settings for already registered content
     */
    default void onClientSetup() {

    }

    /**
     * @param consumer add a renderer to an entity
     */
    default void onRegisterEntityRenderers(EntityRendererConsumer consumer) {

    }

    /**
     * @param consumer add a renderer to a block entity
     */
    default void onRegisterBlockEntityRenderers(BlockEntityRendererConsumer consumer) {

    }

    /**
     * @param consumer add a client tooltip component to a common tooltip component
     */
    default void onRegisterClientTooltipComponents(ClientTooltipComponentConsumer consumer) {

    }

    /**
     * @param consumer add particle providers for a particle type
     */
    default void onRegisterParticleProviders(ParticleProviderConsumer consumer) {

    }

    /**
     * @param consumer register a screen for a menu type
     */
    default void onRegisterMenuScreens(MenuScreenConsumer consumer) {

    }

    /**
     * @param consumer add a sprite to a texture atlas
     */
    default void onRegisterAtlasSprites(AtlasSpriteConsumer consumer) {

    }

    /**
     * @param consumer add a layer definition for a {@link ModelLayerLocation}
     */
    default void onRegisterLayerDefinitions(LayerDefinitionConsumer consumer) {

    }

    /**
     * register a renderer for an entity
     */
    @FunctionalInterface
    interface EntityRendererConsumer {

        /**
         * registers an {@link net.minecraft.client.renderer.entity.EntityRenderer} for a given entity
         *
         * @param entityType entity type token to render for
         * @param entityRendererProvider entity renderer provider
         * @param <T> type of entity
         */
        <T extends Entity> void register(EntityType<? extends T> entityType, EntityRendererProvider<T> entityRendererProvider);
    }

    /**
     * register a renderer for a block entity
     */
    @FunctionalInterface
    interface BlockEntityRendererConsumer {

        /**
         * registers an {@link net.minecraft.client.renderer.blockentity.BlockEntityRenderer} for a given block entity
         *
         * @param blockEntityType             block entity type token to render for
         * @param blockEntityRendererProvider   block entity renderer provider
         * @param <T> type of entity
         */
        <T extends BlockEntity> void register(BlockEntityType<? extends T> blockEntityType, BlockEntityRendererProvider<T> blockEntityRendererProvider);
    }

    /**
     * register a client-side tooltip component factory
     */
    @FunctionalInterface
    interface ClientTooltipComponentConsumer {

        /**
         * register custom tooltip components
         *
         * @param type common {@link TooltipComponent} class
         * @param factory factory for creating {@link ClientTooltipComponent} from <code>type</code>
         * @param <T>     type of common component
         */
        <T extends TooltipComponent> void register(Class<T> type, Function<? super T, ? extends ClientTooltipComponent> factory);
    }

    /**
     * register a particle provider for a particle type
     */
    interface ParticleProviderConsumer {

        /**
         * registers a factory for a particle type client side
         *
         * @param type     particle type (registered separately)
         * @param provider particle factory
         * @param <T>      type of particle
         */
        <T extends ParticleOptions> void register(ParticleType<T> type, ParticleProvider<T> provider);

        /**
         * registers a factory for a particle type client side
         *
         * @param type     particle type (registered separately)
         * @param factory particle factory
         * @param <T>      type of particle
         */
        <T extends ParticleOptions> void register(ParticleType<T> type, ModSpriteParticleRegistration<T> factory);
    }

    /**
     * register a screen for a menu type
     */
    @FunctionalInterface
    interface MenuScreenConsumer {

        /**
         * register a factory for a {@link MenuType}
         * implementation is the same on Fabric and Forge, as both use and accesswidener/accesstransformer, which is not applied in common though
         *
         * @param menuType the menu type
         * @param factory  the factory to create a screen from when the menu is opened on the server
         * @param <M>      type of menu
         * @param <U>      type of screen
         */
        <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void registerMenuScreen(MenuType<? extends M> menuType, ModScreenConstructor<M, U> factory);
    }

    /**
     * stitch a custom sprite onto an atlas
     */
    @FunctionalInterface
    interface AtlasSpriteConsumer {

        /**
         * registers a sprite for being stitched onto an atlas
         *
         * @param atlasId the atlas to register to, since 1.14 there are multiples
         * @param spriteId the sprite to register
         */
        void register(ResourceLocation atlasId, ResourceLocation spriteId);
    }

    /**
     * register layer definitions for entity models
     */
    @FunctionalInterface
    interface LayerDefinitionConsumer {

        /**
         * registers a new layer definition (used for entity model parts)
         *
         * @param layerLocation model location
         * @param supplier      layer definition supplier
         */
        void register(ModelLayerLocation layerLocation, Supplier<LayerDefinition> supplier);
    }
}
