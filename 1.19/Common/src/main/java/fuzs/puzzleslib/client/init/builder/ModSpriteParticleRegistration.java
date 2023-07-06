package fuzs.puzzleslib.client.init.builder;

import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;

/**
 * copy of SpriteParticleRegistration which is package-private
 * @param <T> particle type
 */
@FunctionalInterface
public interface ModSpriteParticleRegistration<T extends ParticleOptions> {

    /**
     * @param spriteSet sprites to draw from
     * @return the new particle instance
     */
    ParticleProvider<T> create(SpriteSet spriteSet);
}
