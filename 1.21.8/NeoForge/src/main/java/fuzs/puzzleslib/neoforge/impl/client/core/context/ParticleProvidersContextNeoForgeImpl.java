package fuzs.puzzleslib.neoforge.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.ParticleProvidersContext;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

import java.util.Objects;

public record ParticleProvidersContextNeoForgeImpl(RegisterParticleProvidersEvent event) implements ParticleProvidersContext {

    @Override
    public <T extends ParticleOptions> void registerParticleProvider(ParticleType<T> particleType, ParticleProvider<T> particleProvider) {
        Objects.requireNonNull(particleType, "particle type is null");
        Objects.requireNonNull(particleProvider, "particle provider is null");
        this.event.registerSpecial(particleType, particleProvider);
    }

    @Override
    public <T extends ParticleOptions> void registerParticleProvider(ParticleType<T> particleType, ParticleProvider.Sprite<T> particleProvider) {
        Objects.requireNonNull(particleType, "particle type is null");
        Objects.requireNonNull(particleProvider, "particle provider is null");
        this.event.registerSprite(particleType, particleProvider);
    }

    @Override
    public <T extends ParticleOptions> void registerParticleProvider(ParticleType<T> particleType, ParticleEngine.SpriteParticleRegistration<T> particleFactory) {
        Objects.requireNonNull(particleType, "particle type is null");
        Objects.requireNonNull(particleFactory, "particle provider factory is null");
        this.event.registerSpriteSet(particleType, particleFactory);
    }
}
