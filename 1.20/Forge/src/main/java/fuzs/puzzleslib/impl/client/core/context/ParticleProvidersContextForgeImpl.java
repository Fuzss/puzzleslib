package fuzs.puzzleslib.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.ParticleProvidersContext;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;

import java.util.Objects;

public record ParticleProvidersContextForgeImpl(
        RegisterParticleProvidersEvent evt) implements ParticleProvidersContext {

    @Override
    public <T extends ParticleOptions> void registerParticleProvider(ParticleType<T> type, ParticleProvider<T> provider) {
        Objects.requireNonNull(type, "particle type is null");
        Objects.requireNonNull(provider, "particle provider is null");
        this.evt.registerSpecial(type, provider);
    }

    @Override
    public <T extends ParticleOptions> void registerParticleFactory(ParticleType<T> type, ParticleEngine.SpriteParticleRegistration<T> factory) {
        Objects.requireNonNull(type, "particle type is null");
        Objects.requireNonNull(factory, "particle provider factory is null");
        this.evt.registerSpriteSet(type, factory);
    }
}
