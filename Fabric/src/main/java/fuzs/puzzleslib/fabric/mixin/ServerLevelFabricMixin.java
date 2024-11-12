package fuzs.puzzleslib.fabric.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.DefaultedFloat;
import fuzs.puzzleslib.api.event.v1.data.DefaultedValue;
import fuzs.puzzleslib.fabric.api.event.v1.FabricEntityEvents;
import fuzs.puzzleslib.fabric.api.event.v1.FabricLevelEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerExplosion;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(ServerLevel.class)
abstract class ServerLevelFabricMixin extends Level {
    @Unique
    private final ThreadLocal<DefaultedValue<Holder<SoundEvent>>> puzzleslib$sound = new ThreadLocal<>();
    @Unique
    private final ThreadLocal<DefaultedValue<SoundSource>> puzzleslib$source = new ThreadLocal<>();
    @Unique
    private final ThreadLocal<DefaultedFloat> puzzleslib$volume = new ThreadLocal<>();
    @Unique
    private final ThreadLocal<DefaultedFloat> puzzleslib$pitch = new ThreadLocal<>();

    protected ServerLevelFabricMixin(WritableLevelData writableLevelData, ResourceKey<Level> resourceKey, RegistryAccess registryAccess, Holder<DimensionType> holder, boolean bl, boolean bl2, long l, int i) {
        super(writableLevelData, resourceKey, registryAccess, holder, bl, bl2, l, i);
    }

    @WrapWithCondition(
            method = "tickNonPassenger",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;tick()V")
    )
    public boolean tickNonPassenger(Entity entity, @Share("isEntityTickCancelled") LocalBooleanRef isEntityTickCancelled) {
        // avoid using @WrapOperation, so we are not blamed for any overhead from running the entity tick
        EventResult result = FabricEntityEvents.ENTITY_TICK_START.invoker().onStartEntityTick(entity);
        isEntityTickCancelled.set(result.isInterrupt());
        return result.isPass();
    }

    @Inject(
            method = "tickNonPassenger",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;tick()V", shift = At.Shift.AFTER)
    )
    public void tickNonPassenger(Entity entity, CallbackInfo callback, @Share("isEntityTickCancelled") LocalBooleanRef isEntityTickCancelled) {
        if (!isEntityTickCancelled.get()) {
            FabricEntityEvents.ENTITY_TICK_END.invoker().onEndEntityTick(entity);
        }
    }

    @Inject(
            method = "explode",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/ServerExplosion;explode()V")
    )
    public void explode(@Nullable Entity entity, @Nullable DamageSource damageSource, @Nullable ExplosionDamageCalculator explosionDamageCalculator, double x, double y, double z, float radius, boolean fire, Level.ExplosionInteraction explosionInteraction, ParticleOptions smallExplosionParticles, ParticleOptions largeExplosionParticles, Holder<SoundEvent> explosionSound, CallbackInfo callback, @Local ServerExplosion explosion) {
        EventResult result = FabricLevelEvents.EXPLOSION_START.invoker().onExplosionStart(ServerLevel.class.cast(this),
                explosion
        );
        if (result.isInterrupt()) callback.cancel();
    }

    @Inject(
            method = "playSeededSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;FFJ)V",
            at = @At("HEAD"),
            cancellable = true
    )
    public void playSeededSound$0(@Nullable Player player, double x, double y, double z, Holder<SoundEvent> soundEvent, SoundSource source, float volume, float pitch, long seed, CallbackInfo callback) {
        this.puzzleslib$sound.set(DefaultedValue.fromValue(soundEvent));
        this.puzzleslib$source.set(DefaultedValue.fromValue(source));
        this.puzzleslib$volume.set(DefaultedFloat.fromValue(volume));
        this.puzzleslib$pitch.set(DefaultedFloat.fromValue(pitch));
        EventResult result = FabricLevelEvents.PLAY_LEVEL_SOUND_AT_POSITION.invoker().onPlaySoundAtPosition(this,
                new Vec3(x, y, z), this.puzzleslib$sound.get(), this.puzzleslib$source.get(),
                this.puzzleslib$volume.get(), this.puzzleslib$pitch.get()
        );
        if (result.isInterrupt() || this.puzzleslib$sound.get().get() == null) callback.cancel();
    }

    @Inject(
            method = "playSeededSound(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;FFJ)V",
            at = @At("HEAD"),
            cancellable = true
    )
    public void playSeededSound$0(@Nullable Player player, Entity entity, Holder<SoundEvent> soundEvent, SoundSource source, float volume, float pitch, long seed, CallbackInfo callback) {
        this.puzzleslib$sound.set(DefaultedValue.fromValue(soundEvent));
        this.puzzleslib$source.set(DefaultedValue.fromValue(source));
        this.puzzleslib$volume.set(DefaultedFloat.fromValue(volume));
        this.puzzleslib$pitch.set(DefaultedFloat.fromValue(pitch));
        EventResult result = FabricLevelEvents.PLAY_LEVEL_SOUND_AT_ENTITY.invoker().onPlaySoundAtEntity(this, entity,
                this.puzzleslib$sound.get(), this.puzzleslib$source.get(), this.puzzleslib$volume.get(),
                this.puzzleslib$pitch.get()
        );
        if (result.isInterrupt() || this.puzzleslib$sound.get().get() == null) callback.cancel();
    }

    @ModifyVariable(
            method = {
                    "playSeededSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;FFJ)V",
                    "playSeededSound(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;FFJ)V"
            }, at = @At("HEAD"), ordinal = 0
    )
    public Holder<SoundEvent> playSeededSound$1(Holder<SoundEvent> soundEvent) {
        Objects.requireNonNull(this.puzzleslib$sound.get(), "sound is null");
        soundEvent = this.puzzleslib$sound.get().getAsOptional().orElse(soundEvent);
        this.puzzleslib$sound.remove();
        return soundEvent;
    }

    @ModifyVariable(
            method = {
                    "playSeededSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;FFJ)V",
                    "playSeededSound(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;FFJ)V"
            }, at = @At("HEAD"), ordinal = 0
    )
    public SoundSource playSeededSound$2(SoundSource soundSource) {
        Objects.requireNonNull(this.puzzleslib$source.get(), "source is null");
        soundSource = this.puzzleslib$source.get().getAsOptional().orElse(soundSource);
        this.puzzleslib$source.remove();
        return soundSource;
    }

    @ModifyVariable(
            method = {
                    "playSeededSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;FFJ)V",
                    "playSeededSound(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;FFJ)V"
            }, at = @At("HEAD"), ordinal = 0
    )
    public float playSeededSound$3(float volume) {
        Objects.requireNonNull(this.puzzleslib$volume.get(), "sound is null");
        volume = this.puzzleslib$volume.get().getAsOptionalFloat().orElse(volume);
        this.puzzleslib$volume.remove();
        return volume;
    }

    @ModifyVariable(
            method = {
                    "playSeededSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;FFJ)V",
                    "playSeededSound(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;FFJ)V"
            }, at = @At("HEAD"), ordinal = 1
    )
    public float playSeededSound$4(float pitch) {
        Objects.requireNonNull(this.puzzleslib$pitch.get(), "pitch is null");
        pitch = this.puzzleslib$pitch.get().getAsOptionalFloat().orElse(pitch);
        this.puzzleslib$pitch.remove();
        return pitch;
    }
}
