package fuzs.puzzleslib.client.core;

import fuzs.puzzleslib.client.init.builder.ModScreenConstructor;
import fuzs.puzzleslib.client.init.builder.ModSpriteParticleRegistration;
import fuzs.puzzleslib.registry.RegistryReference;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
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
 * Fabric implementation of {@link ClientRegistration}
 * everything is handled directly, as Fabric has no loading stages
 */
public class FabricClientRegistration implements ClientRegistration {

    @Override
    public <T extends Entity> void registerEntityRenderer(EntityType<? extends T> entityType, EntityRendererProvider<T> entityRendererProvider) {
        EntityRendererRegistry.register(entityType, entityRendererProvider);
    }

    @Override
    public <T extends BlockEntity> void registerBlockEntityRenderer(BlockEntityType<? extends T> blockEntityType, BlockEntityRendererProvider<T> blockEntityRendererProvider) {
        BlockEntityRendererRegistry.register(blockEntityType, blockEntityRendererProvider);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends TooltipComponent> void registerClientTooltipComponent(Class<T> type, Function<? super T, ? extends ClientTooltipComponent> factory) {
        TooltipComponentCallback.EVENT.register((TooltipComponent data) -> {
            if (data.getClass() == type) return factory.apply((T) data);
            return null;
        });
    }

    @Override
    public <T extends ParticleOptions> void registerParticleProvider(RegistryReference<? extends ParticleType<T>> type, ParticleProvider<T> provider) {
        ParticleFactoryRegistry.getInstance().register(type.get(), provider);
    }

    @Override
    public <T extends ParticleOptions> void registerParticleProvider(RegistryReference<? extends ParticleType<T>> type, ModSpriteParticleRegistration<T> factory) {
        ParticleFactoryRegistry.getInstance().register(type.get(), factory::create);
    }

    @Override
    public <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void registerMenuScreen(MenuType<? extends M> menuType, ModScreenConstructor<M, U> factory) {
        MenuScreens.register(menuType, factory::create);
    }

    @Override
    public void registerAtlasSprite(ResourceLocation atlasId, ResourceLocation spriteId) {
        ClientSpriteRegistryCallback.event(atlasId).register((TextureAtlas atlasTexture, ClientSpriteRegistryCallback.Registry registry) -> {
            registry.register(spriteId);
        });
    }

    @Override
    public void registerLayerDefinition(ModelLayerLocation layerLocation, Supplier<LayerDefinition> supplier) {
        EntityModelLayerRegistry.registerModelLayer(layerLocation, supplier::get);
    }
}
