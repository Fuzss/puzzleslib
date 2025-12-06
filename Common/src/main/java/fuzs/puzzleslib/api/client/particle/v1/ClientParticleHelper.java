package fuzs.puzzleslib.api.client.particle.v1;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * A helper class for particle related methods found in {@link net.minecraft.client.multiplayer.ClientLevel} returning
 * the created particle. Particles will be spawned when a {@link net.minecraft.client.multiplayer.ClientLevel} is
 * provided.
 */
@Deprecated
public final class ClientParticleHelper {

    private ClientParticleHelper() {
        // NO-OP
    }

    /**
     * Copied from
     * {@link net.minecraft.client.multiplayer.ClientLevel#addParticle(ParticleOptions, double, double, double, double,
     * double, double)}.
     */
    @Nullable
    public static Particle addParticle(Level level, ParticleOptions particleData, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        return level.isClientSide ?
                fuzs.puzzleslib.api.client.util.v1.ClientParticleHelper.addParticle((ClientLevel) level,
                        particleData,
                        x,
                        y,
                        z,
                        xSpeed,
                        ySpeed,
                        zSpeed) : null;
    }

    /**
     * Copied from
     * {@link net.minecraft.client.multiplayer.ClientLevel#addParticle(ParticleOptions, boolean, double, double, double,
     * double, double, double)}.
     */
    @Nullable
    public static Particle addParticle(Level level, ParticleOptions particleData, boolean forceAlwaysRender, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        return level.isClientSide ?
                fuzs.puzzleslib.api.client.util.v1.ClientParticleHelper.addParticle((ClientLevel) level,
                        particleData,
                        forceAlwaysRender,
                        x,
                        y,
                        z,
                        xSpeed,
                        ySpeed,
                        zSpeed) : null;
    }

    /**
     * Copied from
     * {@link net.minecraft.client.multiplayer.ClientLevel#addAlwaysVisibleParticle(ParticleOptions, double, double,
     * double, double, double, double)}.
     */
    @Nullable
    public static Particle addAlwaysVisibleParticle(Level level, ParticleOptions particleData, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        return level.isClientSide ?
                fuzs.puzzleslib.api.client.util.v1.ClientParticleHelper.addParticle((ClientLevel) level,
                        particleData,
                        x,
                        y,
                        z,
                        xSpeed,
                        ySpeed,
                        zSpeed) : null;
    }

    /**
     * Copied from
     * {@link net.minecraft.client.multiplayer.ClientLevel#addAlwaysVisibleParticle(ParticleOptions, boolean, double,
     * double, double, double, double, double)}.
     */
    @Nullable
    public static Particle addAlwaysVisibleParticle(Level level, ParticleOptions particleData, boolean ignoreRange, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        return level.isClientSide ?
                fuzs.puzzleslib.api.client.util.v1.ClientParticleHelper.addParticle((ClientLevel) level,
                        particleData,
                        ignoreRange,
                        x,
                        y,
                        z,
                        xSpeed,
                        ySpeed,
                        zSpeed) : null;
    }

    /**
     * Copied from
     * {@link net.minecraft.client.renderer.LevelRenderer#addParticle(ParticleOptions, boolean, boolean, double, double,
     * double, double, double, double)}.
     */
    @Nullable
    public static Particle addParticle(Level level, ParticleOptions options, boolean force, boolean decreased, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        return level.isClientSide ?
                fuzs.puzzleslib.api.client.util.v1.ClientParticleHelper.addParticle((ClientLevel) level,
                        options,
                        force,
                        decreased,
                        x,
                        y,
                        z,
                        xSpeed,
                        ySpeed,
                        zSpeed) : null;
    }
}
