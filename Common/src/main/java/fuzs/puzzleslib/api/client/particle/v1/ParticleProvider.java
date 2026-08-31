package fuzs.puzzleslib.api.client.particle.v1;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;

/**
 * Copied from Minecraft 26.2.
 */
@FunctionalInterface
public interface ParticleProvider<T extends ParticleOptions> extends net.minecraft.client.particle.ParticleProvider<T> {
    @Nullable Particle createParticle(T options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random);

    @Override
    @Nullable
    default Particle createParticle(T options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux) {
        return this.createParticle(options, level, x, y, z, xAux, yAux, zAux, RandomSource.create());
    }

    @FunctionalInterface
    interface Sprite<T extends ParticleOptions> extends net.minecraft.client.particle.ParticleProvider.Sprite<T> {
        @Nullable TextureSheetParticle createParticle(T options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random);

        @Override
        @Nullable
        default TextureSheetParticle createParticle(T options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux) {
            return this.createParticle(options, level, x, y, z, xAux, yAux, zAux, RandomSource.create());
        }
    }
}
