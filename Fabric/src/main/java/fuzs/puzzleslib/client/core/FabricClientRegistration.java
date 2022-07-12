package fuzs.puzzleslib.client.core;

import fuzs.puzzleslib.client.init.builder.ModSpriteParticleRegistration;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

import java.util.function.Function;

/**
 * Fabric implementation of {@link ClientRegistration}
 * everything is handled directly, as Fabric has no loading stages
 */
public class FabricClientRegistration implements ClientRegistration {

    @Override
    public <T extends TooltipComponent> void registerClientTooltipComponent(Class<T> type, Function<? super T, ? extends ClientTooltipComponent> factory) {
        TooltipComponentCallback.EVENT.register((TooltipComponent data) -> {
            if (data.getClass() == type) return factory.apply((T) data);
            return null;
        });
    }

    @Override
    public <T extends ParticleOptions> void registerParticleProvider(ParticleType<T> type, ParticleProvider<T> provider) {
        ParticleFactoryRegistry.getInstance().register(type, provider);
    }

    @Override
    public <T extends ParticleOptions> void registerParticleProvider(ParticleType<T> type, ModSpriteParticleRegistration<T> factory) {
        ParticleFactoryRegistry.getInstance().register(type, factory::create);
    }
}
