package fuzs.puzzleslib.impl.client.core.contexts;

import fuzs.puzzleslib.api.client.core.v1.contexts.ParticleProvidersContext;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

import java.util.Objects;

public final class ParticleProvidersContextFabricImpl implements ParticleProvidersContext {

    @Override
    public <T extends ParticleOptions> void registerParticleProvider(ParticleType<T> type, ParticleProvider<T> provider) {
        Objects.requireNonNull(type, "particle type is null");
        Objects.requireNonNull(provider, "particle provider is null");
        ParticleFactoryRegistry.getInstance().register(type, provider);
    }

    @Override
    public <T extends ParticleOptions> void registerParticleFactory(ParticleType<T> type, ParticleEngine.SpriteParticleRegistration<T> factory) {
        Objects.requireNonNull(type, "particle type is null");
        Objects.requireNonNull(factory, "particle provider factory is null");
        ParticleFactoryRegistry.getInstance().register(type, factory::create);
    }
}
