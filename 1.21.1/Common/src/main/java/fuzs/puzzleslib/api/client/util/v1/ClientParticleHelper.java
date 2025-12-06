package fuzs.puzzleslib.api.client.util.v1;

import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtOps;
import org.jetbrains.annotations.Nullable;

/**
 * A helper class for particle-related methods found in {@link ClientLevel} returning the created particle.
 * <p>
 * Particles will only be spawned when a client-level instance is provided.
 */
public final class ClientParticleHelper {

    private ClientParticleHelper() {
        // NO-OP
    }

    /**
     * @see ClientLevel#addParticle(ParticleOptions, double, double, double, double, double, double)
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
     * @see ClientLevel#addParticle(ParticleOptions, boolean, boolean, double, double, double, double, double,
     *         double)
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
     * @see ClientLevel#addAlwaysVisibleParticle(ParticleOptions, double, double, double, double, double, double)
     */
    @Nullable
    public static Particle addAlwaysVisibleParticle(ClientLevel clientLevel, ParticleOptions particleOptions, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        return addParticle(clientLevel, particleOptions, false, true, x, y, z, xSpeed, ySpeed, zSpeed);
    }

    /**
     * @see ClientLevel#addAlwaysVisibleParticle(ParticleOptions, boolean, double, double, double, double, double,
     *         double)
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
     * Copied from
     * {@link net.minecraft.client.renderer.LevelRenderer#addParticle(ParticleOptions, boolean, boolean, double, double,
     * double, double, double, double)}.
     */
    @Nullable
    public static Particle addParticle(ClientLevel clientLevel, ParticleOptions options, boolean force, boolean decreased, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        try {
            return clientLevel.levelRenderer.addParticleInternal(options,
                    force,
                    decreased,
                    x,
                    y,
                    z,
                    xSpeed,
                    ySpeed,
                    zSpeed);
        } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.forThrowable(throwable, "Exception while adding particle");
            CrashReportCategory crashReportCategory = crashReport.addCategory("Particle being added");
            crashReportCategory.setDetail("ID", BuiltInRegistries.PARTICLE_TYPE.getKey(options.getType()));
            crashReportCategory.setDetail("Parameters",
                    () -> ParticleTypes.CODEC.encodeStart(clientLevel.registryAccess()
                            .createSerializationContext(NbtOps.INSTANCE), options).toString());
            crashReportCategory.setDetail("Position", () -> CrashReportCategory.formatLocation(clientLevel, x, y, z));
            throw new ReportedException(crashReport);
        }
    }
}
