package fuzs.puzzleslib.client.core;

import fuzs.puzzleslib.client.init.builder.ModScreenConstructor;
import fuzs.puzzleslib.client.init.builder.ModSpriteParticleRegistration;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * wrapper class for {@link ClientModConstructor} for calling all required registration methods at the correct time,
 * which means everything is called immediately on Fabric (but in the correct order)
 *
 * we use this wrapper style to allow for already registered to be used within the registration methods instead of having to use suppliers
 * (this doesn't really matter on Fabric)
 */
public class FabricClientModConstructor {

    /**
     * @param constructor the common mod main class implementation
     */
    private FabricClientModConstructor(ClientModConstructor constructor) {
        // only call ModConstructor::onConstructMod during object construction to be similar to Forge
        constructor.onConstructMod();
    }

    @SuppressWarnings("unchecked")
    private <T extends TooltipComponent> void registerClientTooltipComponent(Class<T> type, Function<? super T, ? extends ClientTooltipComponent> factory) {
        TooltipComponentCallback.EVENT.register((TooltipComponent data) -> {
            if (data.getClass() == type) return factory.apply((T) data);
            return null;
        });
    }

    private <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void registerMenuScreen(MenuType<? extends M> menuType, ModScreenConstructor<M, U> factory) {
        MenuScreens.register(menuType, factory::create);
    }

    private void registerAtlasSprite(ResourceLocation atlasId, ResourceLocation spriteId) {
        ClientSpriteRegistryCallback.event(atlasId).register((TextureAtlas atlasTexture, ClientSpriteRegistryCallback.Registry registry) -> {
            registry.register(spriteId);
        });
    }

    private void registerLayerDefinition(ModelLayerLocation layerLocation, Supplier<LayerDefinition> supplier) {
        EntityModelLayerRegistry.registerModelLayer(layerLocation, supplier::get);
    }

    /**
     * construct the mod, calling all necessary registration methods (we don't need the object, it's only useful on Forge)
     *
     * @param constructor mod base class
     */
    public static void construct(ClientModConstructor constructor) {
        FabricClientModConstructor fabricClientModConstructor = new FabricClientModConstructor(constructor);
        // everything after this is done on Forge using events called by the mod event bus
        // this is done since Forge works with loading stages, Fabric doesn't have those stages, so everything is called immediately
        constructor.onClientSetup();
        constructor.onRegisterEntityRenderers(EntityRendererRegistry::register);
        constructor.onRegisterBlockEntityRenderers(BlockEntityRendererRegistry::register);
        constructor.onRegisterClientTooltipComponents(fabricClientModConstructor::registerClientTooltipComponent);
        constructor.onRegisterParticleProviders(new ClientModConstructor.ParticleProviderConsumer() {

            @Override
            public <T extends ParticleOptions> void register(ParticleType<T> type, ParticleProvider<T> provider) {
                ParticleFactoryRegistry.getInstance().register(type, provider);
            }

            @Override
            public <T extends ParticleOptions> void register(ParticleType<T> type, ModSpriteParticleRegistration<T> factory) {
                ParticleFactoryRegistry.getInstance().register(type, factory::create);
            }
        });
        constructor.onRegisterMenuScreens(fabricClientModConstructor::registerMenuScreen);
        constructor.onRegisterAtlasSprites(fabricClientModConstructor::registerAtlasSprite);
        constructor.onRegisterLayerDefinitions(fabricClientModConstructor::registerLayerDefinition);
    }
}
