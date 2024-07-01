package fuzs.puzzleslib.api.client.particle.v1;

import fuzs.puzzleslib.impl.client.particle.ClientParticleTypesImpl;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * A fully client-side particle factory registration helper for particle types that have not been registered in common and will not be present on dedicated servers.
 * <p>Intended for client-only mods to allow for custom particles, which would otherwise be unavailable playing on dedicated servers if registered normally due to registry sync (at least on Forge).
 */
public interface ClientParticleTypes {
    ClientParticleTypes INSTANCE = new ClientParticleTypesImpl();

    /**
     * A convenient overload for {@link #createParticle(ResourceLocation, ParticleOptions, double, double, double, double, double, double)} that does not require a likely useless {@link ParticleOptions} instance.
     */
    default @Nullable Particle createParticle(ResourceLocation identifier, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        return this.createParticle(identifier, new SimpleParticleType(false), x, y, z, xSpeed, ySpeed, zSpeed);
    }

    /**
     * The fully client-side alternative to {@link net.minecraft.client.particle.ParticleEngine#createParticle(ParticleOptions, double, double, double, double, double, double)}.
     */
    @Nullable Particle createParticle(ResourceLocation identifier, ParticleOptions particleData, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed);
}
