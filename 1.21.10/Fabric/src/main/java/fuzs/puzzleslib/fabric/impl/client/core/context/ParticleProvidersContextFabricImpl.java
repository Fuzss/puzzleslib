package fuzs.puzzleslib.fabric.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.ParticleProvidersContext;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.particle.v1.ParticleRendererRegistry;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

import java.util.Objects;
import java.util.function.Function;

public final class ParticleProvidersContextFabricImpl implements ParticleProvidersContext {

    @Override
    public <T extends ParticleOptions> void registerParticleProvider(ParticleType<T> particleType, ParticleProvider<T> particleProvider) {
        Objects.requireNonNull(particleType, "particle type is null");
        Objects.requireNonNull(particleProvider, "particle provider is null");
        ParticleFactoryRegistry.getInstance().register(particleType, particleProvider);
    }

    @Override
    public <T extends ParticleOptions> void registerParticleProvider(ParticleType<T> particleType, ParticleResources.SpriteParticleRegistration<T> particleFactory) {
        Objects.requireNonNull(particleType, "particle type is null");
        Objects.requireNonNull(particleFactory, "particle provider factory is null");
        ParticleFactoryRegistry.getInstance().register(particleType, particleFactory::create);
    }

    @Override
    public void registerParticleRenderType(ParticleRenderType particleRenderType, Function<ParticleEngine, ParticleGroup<?>> particleGroupFactory) {
        Objects.requireNonNull(particleRenderType, "particle render type is null");
        Objects.requireNonNull(particleGroupFactory, "particle group factory is null");
        ParticleRendererRegistry.register(particleRenderType, particleGroupFactory);
    }
}
