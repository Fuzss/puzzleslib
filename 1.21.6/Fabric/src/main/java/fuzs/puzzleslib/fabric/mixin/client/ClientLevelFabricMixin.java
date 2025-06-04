package fuzs.puzzleslib.fabric.mixin.client;

import com.google.common.base.Preconditions;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.MutableFloat;
import fuzs.puzzleslib.api.event.v1.data.MutableValue;
import fuzs.puzzleslib.fabric.api.client.event.v1.FabricClientEntityEvents;
import fuzs.puzzleslib.fabric.api.client.event.v1.FabricClientLevelEvents;
import fuzs.puzzleslib.fabric.api.event.v1.FabricEntityEvents;
import fuzs.puzzleslib.fabric.api.event.v1.FabricLevelEvents;
import fuzs.puzzleslib.fabric.impl.event.FabricEventImplHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ClientLevel.class)
abstract class ClientLevelFabricMixin extends Level {

    protected ClientLevelFabricMixin(WritableLevelData writableLevelData, ResourceKey<Level> resourceKey, RegistryAccess registryAccess, Holder<DimensionType> holder, boolean bl, boolean bl2, long l, int i) {
        super(writableLevelData, resourceKey, registryAccess, holder, bl, bl2, l, i);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(ClientPacketListener clientPacketListener, ClientLevel.ClientLevelData clientLevelData, ResourceKey<Level> resourceKey, Holder<DimensionType> holder, int i, int j, LevelRenderer levelRenderer, boolean bl, long l, int k, CallbackInfo callback) {
        FabricClientLevelEvents.LOAD_LEVEL.invoker().onLevelLoad(Minecraft.getInstance(), ClientLevel.class.cast(this));
    }

    @WrapWithCondition(
            method = "tickNonPassenger",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;tick()V")
    )
    public boolean tickNonPassenger(Entity entity, @Share("isEntityTickCancelled") LocalBooleanRef isEntityTickCancelled) {
        // avoid using @WrapOperation, so we are not blamed for any overhead from running the entity tick
        EventResult eventResult = FabricEntityEvents.ENTITY_TICK_START.invoker().onStartEntityTick(entity);
        isEntityTickCancelled.set(eventResult.isInterrupt());
        return eventResult.isPass();
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

    @Inject(method = "addEntity", at = @At("HEAD"), cancellable = true)
    private void addEntity(Entity entityToSpawn, CallbackInfo callback) {
        if (FabricClientEntityEvents.ENTITY_LOAD.invoker()
                .onEntityLoad(entityToSpawn, ClientLevel.class.cast(this))
                .isInterrupt()) {
            if (entityToSpawn instanceof Player) {
                // we do not support players as it isn't as straight-forward to implement for the server event on Fabric
                throw new UnsupportedOperationException("Cannot prevent player from spawning in!");
            } else {
                callback.cancel();
            }
        }
    }

    @ModifyArgs(
            method = "playSeededSound(Lnet/minecraft/world/entity/Entity;DDDLnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;FFJ)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/ClientLevel;playSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZJ)V"
            )
    )
    public void playSeededSound$0(Args args, @Cancellable CallbackInfo callback) {
        Preconditions.checkArgument(args.get(3) instanceof SoundEvent, "sound event is wrong type");
        EventResult eventResult = FabricEventImplHelper.onPlaySound((MutableValue<Holder<SoundEvent>> soundEvent, MutableValue<SoundSource> soundSource, MutableFloat soundVolume, MutableFloat soundPitch) -> {
                    return FabricLevelEvents.PLAY_SOUND_AT_POSITION.invoker()
                            .onPlaySoundAtPosition(this,
                                    new Vec3(args.get(0), args.get(1), args.get(2)),
                                    soundEvent,
                                    soundSource,
                                    soundVolume,
                                    soundPitch);
                },
                args,
                MutableValue.fromEvent((Holder<SoundEvent> holder) -> args.set(3, holder.value()),
                        () -> BuiltInRegistries.SOUND_EVENT.wrapAsHolder(args.get(3))),
                4,
                5,
                6);
        if (eventResult.isInterrupt()) callback.cancel();
    }

    @ModifyArgs(
            method = "playSeededSound(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;FFJ)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/resources/sounds/EntityBoundSoundInstance;<init>(Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFLnet/minecraft/world/entity/Entity;J)V"
            )
    )
    public void playSeededSound$1(Args args, @Cancellable CallbackInfo callback) {
        Preconditions.checkArgument(args.get(0) instanceof SoundEvent, "sound event is wrong type");
        EventResult eventResult = FabricEventImplHelper.onPlaySound((MutableValue<Holder<SoundEvent>> soundEvent, MutableValue<SoundSource> soundSource, MutableFloat soundVolume, MutableFloat soundPitch) -> {
                    return FabricLevelEvents.PLAY_SOUND_AT_ENTITY.invoker()
                            .onPlaySoundAtEntity(this, args.get(4), soundEvent, soundSource, soundVolume, soundPitch);
                },
                args,
                MutableValue.fromEvent((Holder<SoundEvent> holder) -> args.set(0, holder.value()),
                        () -> BuiltInRegistries.SOUND_EVENT.wrapAsHolder(args.get(0))),
                1,
                2,
                3);
        if (eventResult.isInterrupt()) callback.cancel();
    }
}
