package fuzs.puzzleslib.api.entity.v1;

import fuzs.puzzleslib.api.core.v1.CommonAbstractions;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * A helper class containing a bunch of {@link Explosion} related methods from {@link Level}.
 * <p>
 * The methods additionally to the explosion parameters take an {@link ExplosionFactory}, for providing a custom
 * implementation of {@link Explosion}.
 */
public final class GenericExplosionHelper {

    private GenericExplosionHelper() {
        // NO-OP
    }

    /**
     * Creates and explodes an {@link Explosion} in a level.
     *
     * @param factory              provider for a custom {@link Explosion} implementation
     * @param level                the level the explosion is happening in
     * @param source               an entity that has caused the explosion, like a creeper, end crystal, or primed tnt,
     *                             or null when caused by a block like a bed
     * @param x                    explosion x-position
     * @param y                    explosion y-position
     * @param z                    explosion z-position
     * @param radius               explosion radius
     * @param explosionInteraction should the explosion destroy terrain and should destroyed blocks be dropped
     * @return the already exploded explosion instance
     */
    public static <T extends Explosion> T explode(ExplosionFactory<T> factory, Level level, @Nullable Entity source, double x, double y, double z, float radius, Level.ExplosionInteraction explosionInteraction) {
        return explode(factory,
                level,
                source,
                Explosion.getDefaultDamageSource(level, source),
                null,
                x,
                y,
                z,
                radius,
                false,
                explosionInteraction,
                ParticleTypes.EXPLOSION,
                ParticleTypes.EXPLOSION_EMITTER,
                SoundEvents.GENERIC_EXPLODE
        );
    }

    /**
     * Creates and explodes an {@link Explosion} in a level.
     *
     * @param factory                 provider for a custom {@link Explosion} implementation
     * @param level                   the level the explosion is happening in
     * @param source                  an entity that has caused the explosion, like a creeper, end crystal, or primed
     *                                tnt, or null when caused by a block like a bed
     * @param damageSource            the damage source for hurting hit entities, will fall back to
     *                                {@link Explosion#getDefaultDamageSource(Level, Entity)}
     * @param damageCalculator        the damage calculator, can selectively filter out entities, mainly used to allow
     *                                wind charges to not damage terrain nor entities
     * @param x                       explosion x-position
     * @param y                       explosion y-position
     * @param z                       explosion z-position
     * @param radius                  explosion radius
     * @param fire                    should the explosion spawn fire blocks in the explosion radius, used by explosion
     *                                from fireballs
     * @param explosionInteraction    should the explosion destroy terrain and should destroyed blocks be dropped
     * @param smallExplosionParticles explosion particles, usually {@link ParticleTypes#EXPLOSION}
     * @param largeExplosionParticles explosion particles, usually {@link ParticleTypes#EXPLOSION_EMITTER}
     * @param explosionSound          explosion particles, usually {@link SoundEvents#GENERIC_EXPLODE}
     * @return the already exploded explosion instance
     */
    public static <T extends Explosion> T explode(ExplosionFactory<T> factory, Level level, @Nullable Entity source, @Nullable DamageSource damageSource, @Nullable ExplosionDamageCalculator damageCalculator, double x, double y, double z, float radius, boolean fire, Level.ExplosionInteraction explosionInteraction, ParticleOptions smallExplosionParticles, ParticleOptions largeExplosionParticles, Holder<SoundEvent> explosionSound) {
        T explosion = explode(factory,
                level,
                source,
                damageSource,
                damageCalculator,
                x,
                y,
                z,
                radius,
                fire,
                explosionInteraction,
                level.isClientSide,
                smallExplosionParticles,
                largeExplosionParticles,
                explosionSound
        );

        if (!level.isClientSide) {

            if (!explosion.interactsWithBlocks()) {
                explosion.clearToBlow();
            }

            for (ServerPlayer serverplayer : ((ServerLevel) level).players()) {
                if (serverplayer.distanceToSqr(x, y, z) < 4096.0) {
                    serverplayer.connection.send(new ClientboundExplodePacket(x,
                            y,
                            z,
                            radius,
                            explosion.getToBlow(),
                            explosion.getHitPlayers().get(serverplayer),
                            explosion.getBlockInteraction(),
                            explosion.getSmallExplosionParticles(),
                            explosion.getLargeExplosionParticles(),
                            explosion.getExplosionSound()
                    ));
                }
            }
        }

        return explosion;
    }

    private static <T extends Explosion> T explode(ExplosionFactory<T> factory, Level level, @Nullable Entity source, @Nullable DamageSource damageSource, @Nullable ExplosionDamageCalculator damageCalculator, double x, double y, double z, float radius, boolean fire, Level.ExplosionInteraction explosionInteraction, boolean spawnParticles, ParticleOptions smallExplosionParticles, ParticleOptions largeExplosionParticles, Holder<SoundEvent> explosionSound) {
        T explosion = factory.create(level,
                source,
                damageSource,
                damageCalculator,
                x,
                y,
                z,
                radius,
                fire,
                getBlockInteraction(level, source, explosionInteraction),
                smallExplosionParticles,
                largeExplosionParticles,
                explosionSound
        );
        if (CommonAbstractions.INSTANCE.onExplosionStart(level, explosion)) {
            return explosion;
        } else {
            explosion.explode();
            explosion.finalizeExplosion(spawnParticles);
            return explosion;
        }
    }

    private static Explosion.BlockInteraction getBlockInteraction(Level level, @Nullable Entity source, Level.ExplosionInteraction explosionInteraction) {
        return switch (explosionInteraction) {
            case NONE -> Explosion.BlockInteraction.KEEP;
            case BLOCK -> getDestroyType(level.getGameRules(), GameRules.RULE_BLOCK_EXPLOSION_DROP_DECAY);
            case MOB -> CommonAbstractions.INSTANCE.getMobGriefingRule(level, source) ?
                    getDestroyType(level.getGameRules(), GameRules.RULE_MOB_EXPLOSION_DROP_DECAY) :
                    Explosion.BlockInteraction.KEEP;
            case TNT -> getDestroyType(level.getGameRules(), GameRules.RULE_TNT_EXPLOSION_DROP_DECAY);
            case TRIGGER -> Explosion.BlockInteraction.TRIGGER_BLOCK;
        };
    }

    private static Explosion.BlockInteraction getDestroyType(GameRules gameRules, GameRules.Key<GameRules.BooleanValue> gameRule) {
        return gameRules.getBoolean(gameRule) ?
                Explosion.BlockInteraction.DESTROY_WITH_DECAY :
                Explosion.BlockInteraction.DESTROY;
    }

    /**
     * Creates and explodes an {@link Explosion} in a level.
     *
     * @param factory              provider for a custom {@link Explosion} implementation
     * @param level                the level the explosion is happening in
     * @param source               an entity that has caused the explosion, like a creeper, end crystal, or primed tnt,
     *                             or null when caused by a block like a bed
     * @param x                    explosion x-position
     * @param y                    explosion y-position
     * @param z                    explosion z-position
     * @param radius               explosion radius
     * @param fire                 should the explosion spawn fire blocks in the explosion radius, used by explosion
     *                             from fireballs
     * @param explosionInteraction should the explosion destroy terrain and should destroyed blocks be dropped
     * @return the already exploded explosion instance
     */
    public static <T extends Explosion> T explode(ExplosionFactory<T> factory, Level level, @Nullable Entity source, double x, double y, double z, float radius, boolean fire, Level.ExplosionInteraction explosionInteraction) {
        return explode(factory,
                level,
                source,
                Explosion.getDefaultDamageSource(level, source),
                null,
                x,
                y,
                z,
                radius,
                fire,
                explosionInteraction,
                ParticleTypes.EXPLOSION,
                ParticleTypes.EXPLOSION_EMITTER,
                SoundEvents.GENERIC_EXPLODE
        );
    }

    /**
     * Creates and explodes an {@link Explosion} in a level.
     *
     * @param factory              provider for a custom {@link Explosion} implementation
     * @param level                the level the explosion is happening in
     * @param source               an entity that has caused the explosion, like a creeper, end crystal, or primed tnt,
     *                             or null when caused by a block like a bed
     * @param damageSource         the damage source for hurting hit entities, will fall back to
     *                             {@link Explosion#getDefaultDamageSource(Level, Entity)}
     * @param damageCalculator     the damage calculator, can selectively filter out entities, mainly used to allow wind
     *                             charges to not damage terrain nor entities
     * @param pos                  explosion position
     * @param radius               explosion radius
     * @param fire                 should the explosion spawn fire blocks in the explosion radius, used by explosion
     *                             from fireballs
     * @param explosionInteraction should the explosion destroy terrain and should destroyed blocks be dropped
     * @return the already exploded explosion instance
     */
    public static <T extends Explosion> T explode(ExplosionFactory<T> factory, Level level, @Nullable Entity source, @Nullable DamageSource damageSource, @Nullable ExplosionDamageCalculator damageCalculator, Vec3 pos, float radius, boolean fire, Level.ExplosionInteraction explosionInteraction) {
        return explode(factory,
                level,
                source,
                damageSource,
                damageCalculator,
                pos.x(),
                pos.y(),
                pos.z(),
                radius,
                fire,
                explosionInteraction,
                ParticleTypes.EXPLOSION,
                ParticleTypes.EXPLOSION_EMITTER,
                SoundEvents.GENERIC_EXPLODE
        );
    }

    /**
     * Creates and explodes an {@link Explosion} in a level.
     *
     * @param factory              provider for a custom {@link Explosion} implementation
     * @param level                the level the explosion is happening in
     * @param source               an entity that has caused the explosion, like a creeper, end crystal, or primed tnt,
     *                             or null when caused by a block like a bed
     * @param damageSource         the damage source for hurting hit entities, will fall back to
     *                             {@link Explosion#getDefaultDamageSource(Level, Entity)}
     * @param damageCalculator     the damage calculator, can selectively filter out entities, mainly used to allow wind
     *                             charges to not damage terrain nor entities
     * @param x                    explosion x-position
     * @param y                    explosion y-position
     * @param z                    explosion z-position
     * @param radius               explosion radius
     * @param fire                 should the explosion spawn fire blocks in the explosion radius, used by explosion
     *                             from fireballs
     * @param explosionInteraction should the explosion destroy terrain and should destroyed blocks be dropped
     * @return the already exploded explosion instance
     */
    public static <T extends Explosion> T explode(ExplosionFactory<T> factory, Level level, @Nullable Entity source, @Nullable DamageSource damageSource, @Nullable ExplosionDamageCalculator damageCalculator, double x, double y, double z, float radius, boolean fire, Level.ExplosionInteraction explosionInteraction) {
        return explode(factory,
                level,
                source,
                damageSource,
                damageCalculator,
                x,
                y,
                z,
                radius,
                fire,
                explosionInteraction,
                ParticleTypes.EXPLOSION,
                ParticleTypes.EXPLOSION_EMITTER,
                SoundEvents.GENERIC_EXPLODE
        );
    }

    /**
     * A simple reference for an {@link Explosion} constructor.
     */
    @FunctionalInterface
    public interface ExplosionFactory<T extends Explosion> {

        /**
         * Creates a custom {@link Explosion} implementation.
         *
         * @param level                   the level the explosion is happening in
         * @param source                  an entity that has caused the explosion, like a creeper, end crystal, or
         *                                primed tnt, or null when caused by a block like a bed
         * @param damageSource            the damage source for hurting hit entities, will fall back to
         *                                {@link Explosion#getDefaultDamageSource(Level, Entity)}
         * @param damageCalculator        the damage calculator, can selectively filter out entities, mainly used to
         *                                allow wind charges to not damage terrain nor entities
         * @param x                       explosion x-position
         * @param y                       explosion y-position
         * @param z                       explosion z-position
         * @param radius                  explosion radius
         * @param fire                    should the explosion spawn fire blocks in the explosion radius, used by
         *                                explosion from fireballs
         * @param blockInteraction        should the explosion destroy terrain and should destroyed blocks be dropped
         * @param smallExplosionParticles explosion particles, usually {@link ParticleTypes#EXPLOSION}
         * @param largeExplosionParticles explosion particles, usually {@link ParticleTypes#EXPLOSION_EMITTER}
         * @param explosionSound          explosion particles, usually {@link SoundEvents#GENERIC_EXPLODE}
         * @return the created explosion instance
         */
        T create(Level level, @Nullable Entity source, @Nullable DamageSource damageSource, @Nullable ExplosionDamageCalculator damageCalculator, double x, double y, double z, float radius, boolean fire, Explosion.BlockInteraction blockInteraction, ParticleOptions smallExplosionParticles, ParticleOptions largeExplosionParticles, Holder<SoundEvent> explosionSound);
    }
}
