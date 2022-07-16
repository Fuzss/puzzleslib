package fuzs.puzzleslib.client.core;

import fuzs.puzzleslib.client.init.builder.ModScreenConstructor;
import fuzs.puzzleslib.client.init.builder.ModSpriteParticleRegistration;
import fuzs.puzzleslib.registry.RegistryReference;
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
 * a collection of utility methods for registering client side content
 */
public interface ClientRegistration {

    /**
     * registers an {@link net.minecraft.client.renderer.entity.EntityRenderer} for a given entity
     *
     * @param entityType entity type token to render for
     * @param entityRendererProvider entity renderer provider
     * @param <T> type of entity
     */
    <T extends Entity> void registerEntityRenderer(EntityType<? extends T> entityType, EntityRendererProvider<T> entityRendererProvider);

    /**
     * registers an {@link net.minecraft.client.renderer.blockentity.BlockEntityRenderer} for a given block entity
     *
     * @param blockEntityType             block entity type token to render for
     * @param blockEntityRendererProvider   block entity renderer provider
     * @param <T> type of entity
     */
    <T extends BlockEntity> void registerBlockEntityRenderer(BlockEntityType<? extends T> blockEntityType, BlockEntityRendererProvider<T> blockEntityRendererProvider);

    /**
     * register custom tooltip components
     *
     * @param type common {@link TooltipComponent} class
     * @param factory factory for creating {@link ClientTooltipComponent} from <code>type</code>
     * @param <T>     type of common component
     */
    <T extends TooltipComponent> void registerClientTooltipComponent(Class<T> type, Function<? super T, ? extends ClientTooltipComponent> factory);

    /**
     * registers a factory for a particle type client side
     *
     * @param type     particle type (registered separately)
     * @param provider particle factory
     * @param <T>      type of particle
     */
    <T extends ParticleOptions> void registerParticleProvider(RegistryReference<? extends ParticleType<T>> type, ParticleProvider<T> provider);

    /**
     * registers a factory for a particle type client side
     *
     * @param type     particle type (registered separately)
     * @param factory particle factory
     * @param <T>      type of particle
     */
    <T extends ParticleOptions> void registerParticleProvider(RegistryReference<? extends ParticleType<T>> type, ModSpriteParticleRegistration<T> factory);

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

    /**
     * registers a sprite for being stitched onto an atlas
     *
     * @param atlasId the atlas to register to, since 1.14 there are multiples
     * @param spriteId the sprite to register
     */
    void registerAtlasSprite(ResourceLocation atlasId, ResourceLocation spriteId);

    /**
     * registers a new layer definition (used for entity model parts)
     *
     * @param layerLocation model location
     * @param supplier      layer definition supplier
     */
    void registerLayerDefinition(ModelLayerLocation layerLocation, Supplier<LayerDefinition> supplier);
}
