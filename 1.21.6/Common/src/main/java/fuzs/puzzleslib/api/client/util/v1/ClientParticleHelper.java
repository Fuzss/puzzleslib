package fuzs.puzzleslib.api.client.util.v1;

import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * A helper class for particle related methods found in {@link net.minecraft.client.multiplayer.ClientLevel} returning
 * the created particle.
 * <p>
 * Particles will only be spawned when a client level instance is provided.
 */
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
        return addParticle(level,
                particleData,
                particleData.getType().getOverrideLimiter(),
                false,
                x,
                y,
                z,
                xSpeed,
                ySpeed,
                zSpeed);
    }

    /**
     * Copied from
     * {@link net.minecraft.client.multiplayer.ClientLevel#addParticle(ParticleOptions, boolean, boolean, double,
     * double, double, double, double, double)}.
     */
    @Nullable
    public static Particle addParticle(Level level, ParticleOptions particleData, boolean forceAlwaysRender, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        return addParticle(level,
                particleData,
                particleData.getType().getOverrideLimiter() || forceAlwaysRender,
                false,
                x,
                y,
                z,
                xSpeed,
                ySpeed,
                zSpeed);
    }

    /**
     * Copied from
     * {@link net.minecraft.client.multiplayer.ClientLevel#addAlwaysVisibleParticle(ParticleOptions, double, double,
     * double, double, double, double)}.
     */
    @Nullable
    public static Particle addAlwaysVisibleParticle(Level level, ParticleOptions particleData, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        return addParticle(level, particleData, false, true, x, y, z, xSpeed, ySpeed, zSpeed);
    }

    /**
     * Copied from
     * {@link net.minecraft.client.multiplayer.ClientLevel#addAlwaysVisibleParticle(ParticleOptions, boolean, double,
     * double, double, double, double, double)}.
     */
    @Nullable
    public static Particle addAlwaysVisibleParticle(Level level, ParticleOptions particleData, boolean ignoreRange, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        return addParticle(level,
                particleData,
                particleData.getType().getOverrideLimiter() || ignoreRange,
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
    public static Particle addParticle(Level level, ParticleOptions options, boolean force, boolean decreased, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        if (level.isClientSide) {
            try {
                return Minecraft.getInstance().levelRenderer.addParticleInternal(options,
                        force,
                        decreased,
                        x,
                        y,
                        z,
                        xSpeed,
                        ySpeed,
                        zSpeed);
            } catch (Throwable var19) {
                CrashReport crashReport = CrashReport.forThrowable(var19, "Exception while adding particle");
                CrashReportCategory crashReportCategory = crashReport.addCategory("Particle being added");
                crashReportCategory.setDetail("ID", BuiltInRegistries.PARTICLE_TYPE.getKey(options.getType()));
                crashReportCategory.setDetail("Parameters",
                        () -> ParticleTypes.CODEC.encodeStart(level.registryAccess()
                                .createSerializationContext(NbtOps.INSTANCE), options).toString());
                crashReportCategory.setDetail("Position", () -> CrashReportCategory.formatLocation(level, x, y, z));
                throw new ReportedException(crashReport);
            }
        } else {
            return null;
        }
    }
}
