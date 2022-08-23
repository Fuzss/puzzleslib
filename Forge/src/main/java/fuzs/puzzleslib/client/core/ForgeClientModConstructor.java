package fuzs.puzzleslib.client.core;

import fuzs.puzzleslib.PuzzlesLib;
import fuzs.puzzleslib.PuzzlesLibForge;
import fuzs.puzzleslib.client.init.builder.ModScreenConstructor;
import fuzs.puzzleslib.client.init.builder.ModSpriteParticleRegistration;
import fuzs.puzzleslib.core.ModConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.searchtree.SearchRegistry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.client.event.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.apache.logging.log4j.util.Strings;

/**
 * wrapper class for {@link ClientModConstructor} for calling all required registration methods at the correct time
 * most things need events for registering
 *
 * <p>we use this wrapper style to allow for already registered to be used within the registration methods instead of having to use suppliers
 */
public class ForgeClientModConstructor {
    /**
     * mod base class
     */
    private final ClientModConstructor constructor;

    /**
     * only calls {@link ModConstructor#onConstructMod()}, everything else is done via events later
     *
     * @param constructor mod base class
     */
    private ForgeClientModConstructor(ClientModConstructor constructor) {
        this.constructor = constructor;
        constructor.onConstructMod();
    }

    @SubscribeEvent
    public void onClientSetup(final FMLClientSetupEvent evt) {
        this.constructor.onClientSetup();
        this.constructor.onRegisterMenuScreens(new ClientModConstructor.MenuScreensContext() {

            @Override
            public <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void registerMenuScreen(MenuType<? extends M> menuType, ModScreenConstructor<M, U> factory) {
                MenuScreens.register(menuType, factory::create);
            }
        });
        this.constructor.onRegisterSearchTrees(new ClientModConstructor.SearchRegistryContext() {

            @Override
            public <T> void registerSearchTree(SearchRegistry.Key<T> searchRegistryKey, SearchRegistry.TreeBuilderSupplier<T> treeBuilder) {
                Minecraft.getInstance().getSearchTreeManager().register(searchRegistryKey, treeBuilder);
            }
        });
    }

    @SubscribeEvent
    public void onRegisterRenderers(final EntityRenderersEvent.RegisterRenderers evt) {
        this.constructor.onRegisterEntityRenderers(evt::registerEntityRenderer);
        this.constructor.onRegisterBlockEntityRenderers(evt::registerBlockEntityRenderer);
    }

    @SubscribeEvent
    public void onRegisterClientTooltipComponentFactories(final RegisterClientTooltipComponentFactoriesEvent evt) {
        this.constructor.onRegisterClientTooltipComponents(evt::register);
    }

    @SubscribeEvent
    public void onRegisterParticleProviders(final RegisterParticleProvidersEvent evt) {
        this.constructor.onRegisterParticleProviders(new ClientModConstructor.ParticleProvidersContext() {

            @Override
            public <T extends ParticleOptions> void registerParticleProvider(ParticleType<T> type, ParticleProvider<T> provider) {
                evt.register(type, provider);
            }

            @Override
            public <T extends ParticleOptions> void registerParticleFactory(ParticleType<T> type, ModSpriteParticleRegistration<T> factory) {
                evt.register(type, factory::create);
            }
        });
    }

    @SubscribeEvent
    public void onTextureStitch(final TextureStitchEvent.Pre evt) {
        this.constructor.onRegisterAtlasSprites((ResourceLocation atlasId, ResourceLocation spriteId) -> {
            if (evt.getAtlas().location().equals(atlasId)) {
                evt.addSprite(spriteId);
            }
        });
    }

    @SubscribeEvent
    public void onRegisterLayerDefinitions(final EntityRenderersEvent.RegisterLayerDefinitions evt) {
        this.constructor.onRegisterLayerDefinitions(evt::registerLayerDefinition);
    }

    @SubscribeEvent
    public void onBakingCompleted(final ModelEvent.BakingCompleted evt) {
        this.constructor.onLoadModels(new ClientModConstructor.LoadModelsContext(evt.getModelManager(), evt.getModels(), evt.getModelBakery()));
    }

    @SubscribeEvent
    public void onRegisterAdditional(final ModelEvent.RegisterAdditional evt) {
        this.constructor.onRegisterAdditionalModels(evt::register);
    }

    /**
     * construct the mod, calling all necessary registration methods
     * we don't need the object, it's only important for being registered to the necessary events buses
     *
     * @param modId the mod id for registering events on Forge to the correct mod event bus
     * @param constructor mod base class
     */
    public static void construct(String modId, ClientModConstructor constructor) {
        if (Strings.isBlank(modId)) throw new IllegalArgumentException("modId cannot be empty");
        PuzzlesLib.LOGGER.info("Constructing client components for mod {}", modId);
        ForgeClientModConstructor forgeModConstructor = new ForgeClientModConstructor(constructor);
        PuzzlesLibForge.findModEventBus(modId).register(forgeModConstructor);
    }
}
