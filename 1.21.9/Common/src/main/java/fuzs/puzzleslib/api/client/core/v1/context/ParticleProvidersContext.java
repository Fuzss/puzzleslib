package fuzs.puzzleslib.api.client.core.v1.context;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleResources;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.util.RandomSource;

import java.util.Objects;

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
    default <T extends ParticleOptions> void registerParticleProvider(ParticleType<T> particleType, ParticleProvider.Sprite<T> particleProvider) {
        Objects.requireNonNull(particleType, "particle type is null");
        Objects.requireNonNull(particleProvider, "particle provider is null");
        this.registerParticleProvider(particleType, (SpriteSet spriteSet) -> {
            return (T particleOptions, ClientLevel clientLevel, double x, double y, double z, double xd, double yd, double zd, RandomSource randomSource) -> {
                SingleQuadParticle particle = particleProvider.createParticle(particleOptions,
                        clientLevel,
                        x,
                        y,
                        z,
                        xd,
                        yd,
                        zd,
                        randomSource);
                if (particle != null) {
                    spriteSet.get(randomSource);
                }

                return particle;
            };
        });
    }

    /**
     * Register a client-side sprite based factory for a particle type.
     *
     * @param particleType    common particle type
     * @param particleFactory particle factory
     * @param <T>             type of particle
     */
    <T extends ParticleOptions> void registerParticleProvider(ParticleType<T> particleType, ParticleResources.SpriteParticleRegistration<T> particleFactory);
}
