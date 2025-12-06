package fuzs.puzzleslib.api.client.core.v1.context;

import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

/**
 * Register a particle provider for a particle type.
 */
public interface ParticleProvidersContext {

    /**
     * Register a client-side factory for a particle type.
     *
     * @param particleType     common particle type
     * @param particleProvider particle factory
     * @param <T>              type of particle
     */
    <T extends ParticleOptions> void registerParticleProvider(ParticleType<T> particleType, ParticleProvider<T> particleProvider);

    /**
     * Register a client-side factory for a particle type.
     *
     * @param particleType     common particle type
     * @param particleProvider particle factory
     * @param <T>              type of particle
     */
    @Deprecated
    <T extends ParticleOptions> void registerParticleProvider(ParticleType<T> particleType, ParticleProvider.Sprite<T> particleProvider);

    /**
     * Register a client-side sprite based factory for a particle type.
     *
     * @param particleType    common particle type
     * @param particleFactory particle factory
     * @param <T>             type of particle
     */
    <T extends ParticleOptions> void registerParticleProvider(ParticleType<T> particleType, ParticleEngine.SpriteParticleRegistration<T> particleFactory);
}
