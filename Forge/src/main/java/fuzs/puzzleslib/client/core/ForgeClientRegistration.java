package fuzs.puzzleslib.client.core;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import fuzs.puzzleslib.PuzzlesLib;
import fuzs.puzzleslib.client.init.builder.ModScreenConstructor;
import fuzs.puzzleslib.client.init.builder.ModSpriteParticleRegistration;
import fuzs.puzzleslib.registry.RegistryReference;
import net.minecraft.client.gui.screens.MenuScreens;
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
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Forge implementation of {@link ClientRegistration}
 * content is collected first and then registered when the appropriate event is fired on the mod event bus
 * registration of this class to the mod event bus is done automatically whenever it is used by a mod
 *
 * this could probably also be done the other way around by implementing registry methods every time they are to be used and having consumers in those for registering content
 * it would circumvent having to store everything in some collection as is done here, but I had trouble with generics once again, so I skipped that idea haha
 *
 * @deprecated use {@link ClientModConstructor} instead for implementing everything directly on the main mod client class
 */
@Deprecated
public class ForgeClientRegistration implements ClientRegistration {
    /**
     * all the mod event buses this instance has been registered to,
     * it is important to not register more than once as the events will also run every time, resulting in duplicate content
     */
    private final Set<IEventBus> modEventBuses = Collections.synchronizedSet(Sets.newIdentityHashSet());
    /**
     * collected providers for {@link EntityRendererProvider}
     */
    private final Map<EntityType<? extends Entity>, EntityRendererProvider<? extends Entity>> entityRendererProviders = Maps.newConcurrentMap();
    /**
     * collected providers for {@link BlockEntityRendererProvider}
     */
    private final Map<BlockEntityType<? extends BlockEntity>, BlockEntityRendererProvider<? extends BlockEntity>> blockEntityRendererProviders = Maps.newConcurrentMap();
    /**
     * collected factories for building {@link ClientTooltipComponent} from {@link TooltipComponent}
     */
    private final Map<Class<? extends TooltipComponent>, Function<TooltipComponent, ClientTooltipComponent>> clientTooltipComponents = Maps.newConcurrentMap();
    /**
     * particle types registered via particle providers
     */
    private final Map<RegistryReference<? extends ParticleType<? extends ParticleOptions>>, ParticleProvider<?>> particleProviders = Maps.newConcurrentMap();
    /**
     * particle types registered via sprite factories
     */
    private final Map<RegistryReference<? extends ParticleType<? extends ParticleOptions>>, ModSpriteParticleRegistration<?>> spriteParticleFactories = Maps.newConcurrentMap();
    /**
     * sprites that need to be registered to a specific {@link net.minecraft.client.renderer.texture.TextureAtlas}
     */
    private final Map<ResourceLocation, ResourceLocation> atlasSprites = Maps.newConcurrentMap();
    /**
     * layer definitions for registering
     */
    private final Map<ModelLayerLocation, Supplier<LayerDefinition>> layerDefinitions = Maps.newConcurrentMap();

    @Override
    public <T extends Entity> void registerEntityRenderer(EntityType<? extends T> entityType, EntityRendererProvider<T> entityRendererProvider) {
        this.registerModEventBus();
        this.entityRendererProviders.put(entityType, entityRendererProvider);
    }

    @Override
    public <T extends BlockEntity> void registerBlockEntityRenderer(BlockEntityType<? extends T> blockEntityType, BlockEntityRendererProvider<T> blockEntityRendererProvider) {
        this.registerModEventBus();
        this.blockEntityRendererProviders.put(blockEntityType, blockEntityRendererProvider);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends TooltipComponent> void registerClientTooltipComponent(Class<T> type, Function<? super T, ? extends ClientTooltipComponent> factory) {
        this.registerModEventBus();
        this.clientTooltipComponents.put(type, (Function<TooltipComponent, ClientTooltipComponent>) factory);
    }

    @Override
    public <T extends ParticleOptions> void registerParticleProvider(RegistryReference<? extends ParticleType<T>> type, ParticleProvider<T> provider) {
        this.registerModEventBus();
        this.particleProviders.put(type, provider);
    }

    @Override
    public <T extends ParticleOptions> void registerParticleProvider(RegistryReference<? extends ParticleType<T>> type, ModSpriteParticleRegistration<T> factory) {
        this.registerModEventBus();
        this.spriteParticleFactories.put(type, factory);
    }

    @Override
    public <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void registerMenuScreen(MenuType<? extends M> menuType, ModScreenConstructor<M, U> factory) {
        MenuScreens.register(menuType, factory::create);
    }

    @Override
    public void registerAtlasSprite(ResourceLocation atlasId, ResourceLocation spriteId) {
        this.registerModEventBus();
        this.atlasSprites.put(atlasId, spriteId);
    }

    @Override
    public void registerLayerDefinition(ModelLayerLocation layerLocation, Supplier<LayerDefinition> supplier) {
        this.registerModEventBus();
        this.layerDefinitions.put(layerLocation, supplier);
    }

    @SuppressWarnings("unchecked")
    @SubscribeEvent
    public void onRegisterRenderers(final EntityRenderersEvent.RegisterRenderers evt) {
        this.entityRendererProviders.forEach((key, value) -> evt.registerEntityRenderer(key, (EntityRendererProvider<Entity>) value));
        this.blockEntityRendererProviders.forEach((key, value) -> evt.registerBlockEntityRenderer(key, (BlockEntityRendererProvider<BlockEntity>) value));
    }

    @SubscribeEvent
    public void onRegisterClientTooltipComponentFactories(final RegisterClientTooltipComponentFactoriesEvent evt) {
        this.clientTooltipComponents.forEach(evt::register);
    }

    @SuppressWarnings("unchecked")
    @SubscribeEvent
    public void onRegisterParticleProviders(final RegisterParticleProvidersEvent evt) {
        this.particleProviders.forEach((key, value) -> evt.register((ParticleType<ParticleOptions>) key.get(), (ParticleProvider<ParticleOptions>) value));
        this.spriteParticleFactories.forEach((key, value) -> evt.register((ParticleType<ParticleOptions>) key.get(), spriteSet -> (ParticleProvider<ParticleOptions>) value.create(spriteSet)));
    }

    @SubscribeEvent
    public void onTextureStitch(final TextureStitchEvent.Pre evt) {
        this.atlasSprites.forEach((key, value) -> {
            if (evt.getAtlas().location().equals(key)) {
                evt.addSprite(value);
            }
        });
    }

    @SubscribeEvent
    public void onRegisterLayerDefinitions(final EntityRenderersEvent.RegisterLayerDefinitions evt) {
        this.layerDefinitions.forEach(evt::registerLayerDefinition);
    }

    /**
     * register this singleton instance to the provided mod event bus in case we haven't done so yet
     * call this in every base method inherited from {@link ClientRegistration}
     */
    private void registerModEventBus() {
        if (this.modEventBuses.add(FMLJavaModLoadingContext.get().getModEventBus())) {
            FMLJavaModLoadingContext.get().getModEventBus().register(this);
            PuzzlesLib.LOGGER.info("Added listener to client registration of mod {}", ModLoadingContext.get().getActiveNamespace());
        }
    }
}
