package fuzs.puzzleslib.api.client.core.v1.context;

import net.minecraft.client.particle.*;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

import java.util.Objects;
import java.util.function.Function;

/**
 * Register client-side particle providers for particle types.
 */
public interface ParticleProvidersContext {

    /**
     * Register a factory for a particle type.
     *
     * @param particleType     the particle type
     * @param particleProvider the particle factory
     * @param <T>              the type of particle
     */
    <T extends ParticleOptions> void registerParticleProvider(ParticleType<T> particleType, ParticleProvider<T> particleProvider);

    /**
     * Register a client-side factory for a particle type.
     *
     * @param particleType     common particle type
     * @param particleProvider particle factory
     * @param <T>              type of particle
     */
    @Deprecated(forRemoval = true)
    default <T extends ParticleOptions> void registerParticleProvider(ParticleType<T> particleType, ParticleProvider.Sprite<T> particleProvider) {
        Objects.requireNonNull(particleType, "particle type is null");
        Objects.requireNonNull(particleProvider, "particle provider is null");
        this.registerParticleProvider(particleType, (SpriteSet spriteSet) -> {
            return particleProvider::createParticle;
        });
    }

    /**
     * Register a factory for a particle type.
     *
     * @param particleType    the particle type
     * @param particleFactory the particle factory
     * @param <T>             the type of particle
     */
    <T extends ParticleOptions> void registerParticleProvider(ParticleType<T> particleType, ParticleResources.SpriteParticleRegistration<T> particleFactory);

    /**
     * Register a factory for a particle group.
     *
     * @param particleRenderType   the particle group type
     * @param particleGroupFactory the particle group factory
     */
    void registerParticleRenderType(ParticleRenderType particleRenderType, Function<ParticleEngine, ParticleGroup<?>> particleGroupFactory);
}
