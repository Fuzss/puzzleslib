package fuzs.puzzleslib.api.client.core.v1.contexts;

import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

/**
 * register a particle provider for a particle type
 */
public interface ParticleProvidersContext {

    /**
     * registers a factory for a particle type client side
     *
     * @param type     particle type (registered separately)
     * @param provider particle factory
     * @param <T>      type of particle
     */
    <T extends ParticleOptions> void registerParticleProvider(ParticleType<T> type, ParticleProvider<T> provider);

    /**
     * registers a factory for a particle type client side
     *
     * @param type    particle type (registered separately)
     * @param factory particle factory
     * @param <T>     type of particle
     */
    <T extends ParticleOptions> void registerParticleFactory(ParticleType<T> type, ParticleEngine.SpriteParticleRegistration<T> factory);
}
