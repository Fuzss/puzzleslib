package fuzs.puzzleslib.api.client.util.v1;

import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ParticleStatus;
import org.jetbrains.annotations.Nullable;

/**
 * A helper class for particle-related methods found in {@link net.minecraft.client.multiplayer.ClientLevel} returning
 * the created particle.
 * <p>
 * Particles will only be spawned when a client level instance is provided.
 */
public final class ClientParticleHelper {

    private ClientParticleHelper() {
        // NO-OP
    }

    /**
     * @see net.minecraft.client.multiplayer.ClientLevel#addParticle(ParticleOptions, double, double, double, double,
     *         double, double)
     */
    @Nullable
    public static Particle addParticle(ClientLevel clientLevel, ParticleOptions particleOptions, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        return addParticle(clientLevel,
                particleOptions,
                particleOptions.getType().getOverrideLimiter(),
                false,
                x,
                y,
                z,
                xSpeed,
                ySpeed,
                zSpeed);
    }

    /**
     * @see net.minecraft.client.multiplayer.ClientLevel#addParticle(ParticleOptions, boolean, boolean, double,
     *         double, double, double, double, double)
     */
    @Nullable
    public static Particle addParticle(ClientLevel clientLevel, ParticleOptions particleOptions, boolean forceAlwaysRender, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        return addParticle(clientLevel,
                particleOptions,
                particleOptions.getType().getOverrideLimiter() || forceAlwaysRender,
                false,
                x,
                y,
                z,
                xSpeed,
                ySpeed,
                zSpeed);
    }

    /**
     * @see net.minecraft.client.multiplayer.ClientLevel#addAlwaysVisibleParticle(ParticleOptions, double, double,
     *         double, double, double, double)
     */
    @Nullable
    public static Particle addAlwaysVisibleParticle(ClientLevel clientLevel, ParticleOptions particleOptions, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        return addParticle(clientLevel, particleOptions, false, true, x, y, z, xSpeed, ySpeed, zSpeed);
    }

    /**
     * @see net.minecraft.client.multiplayer.ClientLevel#addAlwaysVisibleParticle(ParticleOptions, boolean, double,
     *         double, double, double, double, double)
     */
    @Nullable
    public static Particle addAlwaysVisibleParticle(ClientLevel clientLevel, ParticleOptions particleOptions, boolean ignoreRange, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        return addParticle(clientLevel,
                particleOptions,
                particleOptions.getType().getOverrideLimiter() || ignoreRange,
                true,
                x,
                y,
                z,
                xSpeed,
                ySpeed,
                zSpeed);
    }

    /**
     * @see net.minecraft.client.multiplayer.ClientLevel#doAddParticle(ParticleOptions, boolean, boolean, double,
     *         double, double, double, double, double)
     */
    @Nullable
    public static Particle addParticle(ClientLevel clientLevel, ParticleOptions particleOptions, boolean force, boolean decreased, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        if (clientLevel.isClientSide()) {
            try {
                Camera camera = clientLevel.minecraft.gameRenderer.getMainCamera();
                ParticleStatus particleStatus = clientLevel.calculateParticleLevel(decreased);
                if (force) {
                    return clientLevel.minecraft.particleEngine.createParticle(particleOptions,
                            x,
                            y,
                            z,
                            xSpeed,
                            ySpeed,
                            zSpeed);
                } else if (!(camera.getPosition().distanceToSqr(x, y, z) > 1024.0)) {
                    if (particleStatus != ParticleStatus.MINIMAL) {
                        return clientLevel.minecraft.particleEngine.createParticle(particleOptions,
                                x,
                                y,
                                z,
                                xSpeed,
                                ySpeed,
                                zSpeed);
                    }
                }
            } catch (Throwable throwable) {
                CrashReport crashReport = CrashReport.forThrowable(throwable, "Exception while adding particle");
                CrashReportCategory crashReportCategory = crashReport.addCategory("Particle being added");
                crashReportCategory.setDetail("ID", BuiltInRegistries.PARTICLE_TYPE.getKey(particleOptions.getType()));
                crashReportCategory.setDetail("Parameters",
                        () -> ParticleTypes.CODEC.encodeStart(clientLevel.registryAccess()
                                .createSerializationContext(NbtOps.INSTANCE), particleOptions).toString());
                crashReportCategory.setDetail("Position",
                        () -> CrashReportCategory.formatLocation(clientLevel, x, y, z));
                throw new ReportedException(crashReport);
            }
        }

        return null;
    }
}
