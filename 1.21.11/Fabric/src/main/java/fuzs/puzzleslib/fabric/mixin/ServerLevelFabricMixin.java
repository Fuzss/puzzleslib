package fuzs.puzzleslib.fabric.mixin;

import com.google.common.base.Preconditions;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.MutableFloat;
import fuzs.puzzleslib.api.event.v1.data.MutableValue;
import fuzs.puzzleslib.fabric.api.event.v1.FabricEntityEvents;
import fuzs.puzzleslib.fabric.api.event.v1.FabricLevelEvents;
import fuzs.puzzleslib.fabric.impl.event.FabricEventImplHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ExplosionParticleInfo;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerExplosion;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ServerLevel.class)
abstract class ServerLevelFabricMixin extends Level {

    protected ServerLevelFabricMixin(WritableLevelData writableLevelData, ResourceKey<Level> resourceKey, RegistryAccess registryAccess, Holder<DimensionType> holder, boolean bl, boolean bl2, long l, int i) {
        super(writableLevelData, resourceKey, registryAccess, holder, bl, bl2, l, i);
    }

    @WrapWithCondition(method = "tickNonPassenger",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;tick()V"))
    public boolean tickNonPassenger(Entity entity, @Share("isEntityTickCancelled") LocalBooleanRef isEntityTickCancelled) {
        // avoid using @WrapOperation, so we are not blamed for any overhead from running the entity tick
        EventResult eventResult = FabricEntityEvents.ENTITY_TICK_START.invoker().onStartEntityTick(entity);
        isEntityTickCancelled.set(eventResult.isInterrupt());
        return eventResult.isPass();
    }

    @Inject(method = "tickNonPassenger",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;tick()V", shift = At.Shift.AFTER))
    public void tickNonPassenger(Entity entity, CallbackInfo callback, @Share("isEntityTickCancelled") LocalBooleanRef isEntityTickCancelled) {
        if (!isEntityTickCancelled.get()) {
            FabricEntityEvents.ENTITY_TICK_END.invoker().onEndEntityTick(entity);
        }
    }

    @Inject(method = "explode",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/ServerExplosion;explode()I"),
            cancellable = true)
    public void explode(@Nullable Entity entity, @Nullable DamageSource damageSource, @Nullable ExplosionDamageCalculator explosionDamageCalculator, double x, double y, double z, float radius, boolean fire, Level.ExplosionInteraction explosionInteraction, ParticleOptions smallExplosionParticles, ParticleOptions largeExplosionParticles, WeightedList<ExplosionParticleInfo> explosionParticles, Holder<SoundEvent> explosionSound, CallbackInfo callback, @Local ServerExplosion explosion) {
        EventResult eventResult = FabricLevelEvents.EXPLOSION_START.invoker()
                .onExplosionStart(ServerLevel.class.cast(this), explosion);
        if (eventResult.isInterrupt()) callback.cancel();
    }

    @ModifyArgs(method = "playSeededSound(Lnet/minecraft/world/entity/Entity;DDDLnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;FFJ)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/network/protocol/game/ClientboundSoundPacket;<init>(Lnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;DDDFFJ)V"))
    public void playSeededSound$0(Args args, @Cancellable CallbackInfo callback) {
        Preconditions.checkArgument(args.get(0) instanceof Holder<?>, "sound event is wrong type");
        EventResult eventResult = FabricEventImplHelper.onPlaySound((soundEvent, soundSource, soundVolume, soundPitch) -> {
                    return FabricLevelEvents.PLAY_SOUND_AT_POSITION.invoker()
                            .onPlaySoundAtPosition(this,
                                    new Vec3(args.get(2), args.get(3), args.get(4)),
                                    soundEvent,
                                    soundSource,
                                    soundVolume,
                                    soundPitch);
                },
                args,
                MutableValue.fromEvent((Holder<SoundEvent> holder) -> args.set(0, holder), () -> args.get(0)),
                1,
                5,
                6);
        if (eventResult.isInterrupt()) callback.cancel();
    }

    @ModifyArgs(method = "playSeededSound(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;FFJ)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/network/protocol/game/ClientboundSoundEntityPacket;<init>(Lnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;Lnet/minecraft/world/entity/Entity;FFJ)V"))
    public void playSeededSound$1(Args args, @Cancellable CallbackInfo callback) {
        Preconditions.checkArgument(args.get(0) instanceof Holder<?>, "sound event is wrong type");
        EventResult eventResult = FabricEventImplHelper.onPlaySound((MutableValue<Holder<SoundEvent>> soundEvent, MutableValue<SoundSource> soundSource, MutableFloat soundVolume, MutableFloat soundPitch) -> {
                    return FabricLevelEvents.PLAY_SOUND_AT_ENTITY.invoker()
                            .onPlaySoundAtEntity(this, args.get(2), soundEvent, soundSource, soundVolume, soundPitch);
                },
                args,
                MutableValue.fromEvent((Holder<SoundEvent> holder) -> args.set(0, holder), () -> args.get(0)),
                1,
                3,
                4);
        if (eventResult.isInterrupt()) callback.cancel();
    }
}
