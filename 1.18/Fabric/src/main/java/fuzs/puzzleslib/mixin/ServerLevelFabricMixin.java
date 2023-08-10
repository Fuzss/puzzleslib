package fuzs.puzzleslib.mixin;

import fuzs.puzzleslib.api.event.v1.FabricLevelEvents;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.DefaultedFloat;
import fuzs.puzzleslib.api.event.v1.data.DefaultedValue;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;
import java.util.function.Supplier;

@Mixin(ServerLevel.class)
abstract class ServerLevelFabricMixin extends Level {
    @Unique
    private DefaultedValue<SoundEvent> puzzleslib$sound;
    @Unique
    private DefaultedValue<SoundSource> puzzleslib$source;
    @Unique
    private DefaultedFloat puzzleslib$volume;
    @Unique
    private DefaultedFloat puzzleslib$pitch;
    @Nullable
    @Unique
    private Explosion puzzleslib$activeExplosion;

    protected ServerLevelFabricMixin(WritableLevelData writableLevelData, ResourceKey<Level> resourceKey, Holder<DimensionType> holder, Supplier<ProfilerFiller> supplier, boolean bl, boolean bl2, long l) {
        super(writableLevelData, resourceKey, holder, supplier, bl, bl2, l);
    }

    @ModifyVariable(method = "explode(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;Lnet/minecraft/world/level/ExplosionDamageCalculator;DDDFZLnet/minecraft/world/level/Explosion$BlockInteraction;)Lnet/minecraft/world/level/Explosion;", at = @At("STORE"), ordinal = 0)
    public Explosion explode$0(Explosion explosion) {
        this.puzzleslib$activeExplosion = explosion;
        return explosion;
    }

    @Inject(method = "explode(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;Lnet/minecraft/world/level/ExplosionDamageCalculator;DDDFZLnet/minecraft/world/level/Explosion$BlockInteraction;)Lnet/minecraft/world/level/Explosion;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Explosion;explode()V", shift = At.Shift.BEFORE), cancellable = true)
    public void explode$1(@Nullable Entity source, @Nullable DamageSource damageSource, @Nullable ExplosionDamageCalculator damageCalculator, double x, double y, double z, float radius, boolean fire, Explosion.BlockInteraction explosionInteraction, CallbackInfoReturnable<Explosion> callback) {
        Objects.requireNonNull(this.puzzleslib$activeExplosion, "active explosion is null");
        EventResult result = FabricLevelEvents.EXPLOSION_START.invoker().onExplosionStart(this, this.puzzleslib$activeExplosion);
        if (result.isInterrupt()) callback.setReturnValue(this.puzzleslib$activeExplosion);
        this.puzzleslib$activeExplosion = null;
    }

    @Inject(method = "playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V", at = @At("HEAD"), cancellable = true)
    public void playSound$0(@Nullable Player player, double x, double y, double z, SoundEvent soundEvent, SoundSource source, float volume, float pitch, CallbackInfo callback) {
        this.puzzleslib$sound = DefaultedValue.fromValue(soundEvent);
        this.puzzleslib$source = DefaultedValue.fromValue(source);
        this.puzzleslib$volume = DefaultedFloat.fromValue(volume);
        this.puzzleslib$pitch = DefaultedFloat.fromValue(pitch);
        EventResult result = FabricLevelEvents.PLAY_LEVEL_SOUND_AT_POSITION.invoker().onPlaySoundAtPosition(this, new Vec3(x, y, z), this.puzzleslib$sound, this.puzzleslib$source, this.puzzleslib$volume, this.puzzleslib$pitch);
        if (result.isInterrupt() || this.puzzleslib$sound.get() == null) callback.cancel();
    }

    @Inject(method = "playSound(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V", at = @At("HEAD"), cancellable = true)
    public void playSound$0(@Nullable Player player, Entity entity, SoundEvent soundEvent, SoundSource source, float volume, float pitch, CallbackInfo callback) {
        this.puzzleslib$sound = DefaultedValue.fromValue(soundEvent);
        this.puzzleslib$source = DefaultedValue.fromValue(source);
        this.puzzleslib$volume = DefaultedFloat.fromValue(volume);
        this.puzzleslib$pitch = DefaultedFloat.fromValue(pitch);
        EventResult result = FabricLevelEvents.PLAY_LEVEL_SOUND_AT_ENTITY.invoker().onPlaySoundAtEntity(this, entity, this.puzzleslib$sound, this.puzzleslib$source, this.puzzleslib$volume, this.puzzleslib$pitch);
        if (result.isInterrupt() || this.puzzleslib$sound.get() == null) callback.cancel();
    }

    @ModifyVariable(method = {"playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V", "playSound(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"}, at = @At("HEAD"), ordinal = 0)
    public SoundEvent playSound$1(SoundEvent soundEvent) {
        Objects.requireNonNull(this.puzzleslib$sound, "sound is null");
        soundEvent = this.puzzleslib$sound.getAsOptional().orElse(soundEvent);
        this.puzzleslib$sound = null;
        return soundEvent;
    }

    @ModifyVariable(method = {"playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V", "playSound(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"}, at = @At("HEAD"), ordinal = 0)
    public SoundSource playSound$2(SoundSource source) {
        Objects.requireNonNull(this.puzzleslib$source, "source is null");
        source = this.puzzleslib$source.getAsOptional().orElse(source);
        this.puzzleslib$source = null;
        return source;
    }

    @ModifyVariable(method = {"playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V", "playSound(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"}, at = @At("HEAD"), ordinal = 0)
    public float playSound$3(float volume) {
        Objects.requireNonNull(this.puzzleslib$volume, "sound is null");
        volume = this.puzzleslib$volume.getAsOptionalFloat().orElse(volume);
        this.puzzleslib$volume = null;
        return volume;
    }

    @ModifyVariable(method = {"playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V", "playSound(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"}, at = @At("HEAD"), ordinal = 1)
    public float playSound$4(float pitch) {
        Objects.requireNonNull(this.puzzleslib$pitch, "pitch is null");
        pitch = this.puzzleslib$pitch.getAsOptionalFloat().orElse(pitch);
        this.puzzleslib$pitch = null;
        return pitch;
    }
}