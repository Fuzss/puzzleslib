package fuzs.puzzleslib.mixin.client;

import fuzs.puzzleslib.api.client.event.v1.FabricClientEvents;
import fuzs.puzzleslib.api.event.v1.FabricLevelEvents;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.DefaultedFloat;
import fuzs.puzzleslib.api.event.v1.data.DefaultedValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
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

import java.util.Objects;
import java.util.function.Supplier;

@Mixin(ClientLevel.class)
abstract class ClientLevelFabricMixin extends Level {
    @Unique
    private final ThreadLocal<DefaultedValue<SoundEvent>> puzzleslib$sound = new ThreadLocal<>();
    @Unique
    private final ThreadLocal<DefaultedValue<SoundSource>> puzzleslib$source = new ThreadLocal<>();
    @Unique
    private final ThreadLocal<DefaultedFloat> puzzleslib$volume = new ThreadLocal<>();
    @Unique
    private final ThreadLocal<DefaultedFloat> puzzleslib$pitch = new ThreadLocal<>();

    protected ClientLevelFabricMixin(WritableLevelData writableLevelData, ResourceKey<Level> resourceKey, Holder<DimensionType> holder, Supplier<ProfilerFiller> supplier, boolean bl, boolean bl2, long l) {
        super(writableLevelData, resourceKey, holder, supplier, bl, bl2, l);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(ClientPacketListener clientPacketListener, ClientLevel.ClientLevelData clientLevelData, ResourceKey<Level> resourceKey, Holder<DimensionType> holder, int i, int j, Supplier<ProfilerFiller> supplier, LevelRenderer levelRenderer, boolean bl, long l, CallbackInfo callback) {
        FabricClientEvents.LOAD_LEVEL.invoker().onLevelLoad(Minecraft.getInstance(), ClientLevel.class.cast(this));
    }

    @Inject(method = "addEntity", at = @At("HEAD"), cancellable = true)
    private void addEntity(int entityId, Entity entityToSpawn, CallbackInfo callback) {
        if (FabricClientEvents.ENTITY_LOAD.invoker().onEntityLoad(entityToSpawn, ClientLevel.class.cast(this)).isInterrupt()) {
            if (entityToSpawn instanceof Player) {
                // we do not support players as it isn't as straight-forward to implement for the server event on Fabric
                throw new UnsupportedOperationException("Cannot prevent player from spawning in!");
            } else {
                callback.cancel();
            }
        }
    }

    @Inject(method = "playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V", at = @At("HEAD"), cancellable = true)
    public void playSound$0(@Nullable Player player, double x, double y, double z, SoundEvent soundEvent, SoundSource source, float volume, float pitch, CallbackInfo callback) {
        this.puzzleslib$sound.set(DefaultedValue.fromValue(soundEvent));
        this.puzzleslib$source.set(DefaultedValue.fromValue(source));
        this.puzzleslib$volume.set(DefaultedFloat.fromValue(volume));
        this.puzzleslib$pitch.set(DefaultedFloat.fromValue(pitch));
        EventResult result = FabricLevelEvents.PLAY_LEVEL_SOUND_AT_POSITION.invoker().onPlaySoundAtPosition(this, new Vec3(x, y, z), this.puzzleslib$sound.get(), this.puzzleslib$source.get(), this.puzzleslib$volume.get(), this.puzzleslib$pitch.get());
        if (result.isInterrupt() || this.puzzleslib$sound.get().get() == null) callback.cancel();
    }

    @Inject(method = "playSound(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V", at = @At("HEAD"), cancellable = true)
    public void playSound$0(@Nullable Player player, Entity entity, SoundEvent soundEvent, SoundSource source, float volume, float pitch, CallbackInfo callback) {
        this.puzzleslib$sound.set(DefaultedValue.fromValue(soundEvent));
        this.puzzleslib$source.set(DefaultedValue.fromValue(source));
        this.puzzleslib$volume.set(DefaultedFloat.fromValue(volume));
        this.puzzleslib$pitch.set(DefaultedFloat.fromValue(pitch));
        EventResult result = FabricLevelEvents.PLAY_LEVEL_SOUND_AT_ENTITY.invoker().onPlaySoundAtEntity(this, entity, this.puzzleslib$sound.get(), this.puzzleslib$source.get(), this.puzzleslib$volume.get(), this.puzzleslib$pitch.get());
        if (result.isInterrupt() || this.puzzleslib$sound.get().get() == null) callback.cancel();
    }

    @ModifyVariable(method = {"playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V", "playSound(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"}, at = @At("HEAD"), ordinal = 0)
    public SoundEvent playSound$1(SoundEvent soundEvent) {
        Objects.requireNonNull(this.puzzleslib$sound.get(), "sound is null");
        soundEvent = this.puzzleslib$sound.get().getAsOptional().orElse(soundEvent);
        this.puzzleslib$sound.remove();
        return soundEvent;
    }

    @ModifyVariable(method = {"playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V", "playSound(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"}, at = @At("HEAD"), ordinal = 0)
    public SoundSource playSound$2(SoundSource soundSource) {
        Objects.requireNonNull(this.puzzleslib$source.get(), "source is null");
        soundSource = this.puzzleslib$source.get().getAsOptional().orElse(soundSource);
        this.puzzleslib$source.remove();
        return soundSource;
    }

    @ModifyVariable(method = {"playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V", "playSound(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"}, at = @At("HEAD"), ordinal = 0)
    public float playSound$3(float volume) {
        Objects.requireNonNull(this.puzzleslib$volume.get(), "sound is null");
        volume = this.puzzleslib$volume.get().getAsOptionalFloat().orElse(volume);
        this.puzzleslib$volume.remove();
        return volume;
    }

    @ModifyVariable(method = {"playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V", "playSound(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"}, at = @At("HEAD"), ordinal = 1)
    public float playSound$4(float pitch) {
        Objects.requireNonNull(this.puzzleslib$pitch.get(), "pitch is null");
        pitch = this.puzzleslib$pitch.get().getAsOptionalFloat().orElse(pitch);
        this.puzzleslib$pitch.remove();
        return pitch;
    }
}
