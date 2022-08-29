package fuzs.puzzleslib.client.core;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.client.extension.WrappedClientItemExtension;
import fuzs.puzzleslib.client.init.builder.ModScreenConstructor;
import fuzs.puzzleslib.client.init.builder.ModSpriteParticleRegistration;
import fuzs.puzzleslib.client.renderer.DynamicBuiltinModelItemRenderer;
import fuzs.puzzleslib.client.resources.model.DynamicModelBakingContext;
import fuzs.puzzleslib.core.ModConstructor;
import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.mixin.client.accessor.ItemAccessor;
import fuzs.puzzleslib.util.PuzzlesUtilForge;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.client.searchtree.SearchRegistry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.logging.log4j.util.Strings;

import java.util.List;
import java.util.function.Consumer;

/**
 * wrapper class for {@link ClientModConstructor} for calling all required registration methods at the correct time
 * most things need events for registering
 *
 * <p>we use this wrapper style to allow for already registered to be used within the registration methods instead of having to use suppliers
 */
public class ForgeClientModConstructor {
    /**
     * the mod id
     */
    private final String modId;
    /**
     * mod base class
     */
    private final ClientModConstructor constructor;
    /**
     * actions to run each time after baked models have been reloaded
     */
    private final List<Consumer<DynamicModelBakingContext>> modelBakingConsumers = Lists.newArrayList();

    /**
     * only calls {@link ModConstructor#onConstructMod()}, everything else is done via events later
     *
     * @param modId         the mod id
     * @param constructor   mod base class
     */
    private ForgeClientModConstructor(String modId, ClientModConstructor constructor) {
        this.modId = modId;
        this.constructor = constructor;
        constructor.onConstructMod();
    }

    @SubscribeEvent
    public void onClientSetup(final FMLClientSetupEvent evt) {
        this.constructor.onClientSetup();
        this.constructor.onRegisterMenuScreens(this.getMenuScreensContext());
        this.constructor.onRegisterSearchTrees(this.getSearchRegistryContext());
        this.constructor.onRegisterModelBakingCompletedListeners(this.modelBakingConsumers::add);
        this.constructor.onRegisterItemModelProperties(this.getItemPropertiesContext());
        this.constructor.onRegisterBuiltinModelItemRenderers(this.getBuiltinModelItemRendererContext());
    }

    private ClientModConstructor.MenuScreensContext getMenuScreensContext() {
        return new ClientModConstructor.MenuScreensContext() {

            @Override
            public <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void registerMenuScreen(MenuType<? extends M> menuType, ModScreenConstructor<M, U> factory) {
                MenuScreens.register(menuType, factory::create);
            }
        };
    }

    private ClientModConstructor.SearchRegistryContext getSearchRegistryContext() {
        return new ClientModConstructor.SearchRegistryContext() {

            @Override
            public <T> void registerSearchTree(SearchRegistry.Key<T> searchRegistryKey, SearchRegistry.TreeBuilderSupplier<T> treeBuilder) {
                Minecraft.getInstance().getSearchTreeManager().register(searchRegistryKey, treeBuilder);
            }
        };
    }

    private ClientModConstructor.ItemModelPropertiesContext getItemPropertiesContext() {
        return new ClientModConstructor.ItemModelPropertiesContext() {

            @Override
            public void register(ResourceLocation name, ClampedItemPropertyFunction property) {
                ItemProperties.registerGeneric(name, property);
            }

            @Override
            public void registerItem(Item item, ResourceLocation name, ClampedItemPropertyFunction property) {
                ItemProperties.register(item, name, property);
            }
        };
    }

    private ClientModConstructor.BuiltinModelItemRendererContext getBuiltinModelItemRendererContext() {
        return (ItemLike item, DynamicBuiltinModelItemRenderer renderer) -> {
            // copied from Forge, supposed to break data gen otherwise
            if (FMLLoader.getLaunchHandler().isData()) return;
            // this solution is very dangerous as it relies on internal stuff in Forge
            // but there is no other way for multi-loader and without making this a huge inconvenience so ¯\_(ツ)_/¯
            final IClientItemExtensions clientItemExtension = new IClientItemExtensions() {

                @Override
                public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                    Minecraft minecraft = Minecraft.getInstance();
                    return new BlockEntityWithoutLevelRenderer(minecraft.getBlockEntityRenderDispatcher(), minecraft.getEntityModels()) {

                        @Override
                        public void renderByItem(ItemStack stack, ItemTransforms.TransformType mode, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
                            renderer.renderByItem(stack, mode, matrices, vertexConsumers, light, overlay);
                        }

                        @Override
                        public void onResourceManagerReload(ResourceManager resourceManager) {

                        }
                    };
                }
            };
            Object currentClientItemExtension = ((ItemAccessor) item.asItem()).getRenderProperties();
            ((ItemAccessor) item.asItem()).setRenderProperties(currentClientItemExtension != null ? new WrappedClientItemExtension((IClientItemExtensions) currentClientItemExtension) {

                @Override
                public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                    return clientItemExtension.getCustomRenderer();
                }
            } : clientItemExtension);
        };
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
        final DynamicModelBakingContext context = new DynamicModelBakingContext(evt.getModelManager(), evt.getModels(), evt.getModelBakery()) {

            @Override
            public BakedModel bakeModel(ResourceLocation modelLocation) {
                UnbakedModel unbakedModel = this.modelBakery.getModel(modelLocation);
                return unbakedModel.bake(this.modelBakery, this.modelBakery.getAtlasSet()::getSprite, BlockModelRotation.X0_Y0, modelLocation);
            }
        };
        for (Consumer<DynamicModelBakingContext> listener : this.modelBakingConsumers) {
            try {
                listener.accept(context);
            } catch (Exception e) {
                PuzzlesLib.LOGGER.error("Unable to execute additional resource pack model processing provided by {}", this.modId, e);
            }
        }
    }

    @SubscribeEvent
    public void onRegisterAdditional(final ModelEvent.RegisterAdditional evt) {
        this.constructor.onRegisterAdditionalModels(evt::register);
    }

    @SubscribeEvent
    public void onRegisterItemDecorations(final RegisterItemDecorationsEvent evt) {
        this.constructor.onRegisterItemDecorations((itemLike, decorator) -> evt.register(itemLike, decorator::renderItemDecorations));
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
        ForgeClientModConstructor forgeModConstructor = new ForgeClientModConstructor(modId, constructor);
        PuzzlesUtilForge.findModEventBus(modId).register(forgeModConstructor);
    }
}
