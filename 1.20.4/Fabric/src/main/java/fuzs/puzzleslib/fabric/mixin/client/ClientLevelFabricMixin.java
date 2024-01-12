package fuzs.puzzleslib.fabric.mixin.client;

import fuzs.puzzleslib.fabric.api.client.event.v1.FabricClientEvents;
import fuzs.puzzleslib.fabric.api.event.v1.FabricLevelEvents;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.DefaultedFloat;
import fuzs.puzzleslib.api.event.v1.data.DefaultedValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
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
    private final ThreadLocal<DefaultedValue<Holder<SoundEvent>>> puzzleslib$sound = new ThreadLocal<>();
    @Unique
    private final ThreadLocal<DefaultedValue<SoundSource>> puzzleslib$source = new ThreadLocal<>();
    @Unique
    private final ThreadLocal<DefaultedFloat> puzzleslib$volume = new ThreadLocal<>();
    @Unique
    private final ThreadLocal<DefaultedFloat> puzzleslib$pitch = new ThreadLocal<>();

    protected ClientLevelFabricMixin(WritableLevelData writableLevelData, ResourceKey<Level> resourceKey, RegistryAccess registryAccess, Holder<DimensionType> holder, Supplier<ProfilerFiller> supplier, boolean bl, boolean bl2, long l, int i) {
        super(writableLevelData, resourceKey, registryAccess, holder, supplier, bl, bl2, l, i);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(ClientPacketListener clientPacketListener, ClientLevel.ClientLevelData clientLevelData, ResourceKey<Level> resourceKey, Holder<DimensionType> holder, int i, int j, Supplier<ProfilerFiller> supplier, LevelRenderer levelRenderer, boolean bl, long l, CallbackInfo callback) {
        FabricClientEvents.LOAD_LEVEL.invoker().onLevelLoad(Minecraft.getInstance(), ClientLevel.class.cast(this));
    }

    @Inject(method = "addEntity", at = @At("HEAD"), cancellable = true)
    private void addEntity(Entity entityToSpawn, CallbackInfo callback) {
        if (FabricClientEvents.ENTITY_LOAD.invoker().onEntityLoad(entityToSpawn, ClientLevel.class.cast(this)).isInterrupt()) {
            if (entityToSpawn instanceof Player) {
                // we do not support players as it isn't as straight-forward to implement for the server event on Fabric
                throw new UnsupportedOperationException("Cannot prevent player from spawning in!");
            } else {
                callback.cancel();
            }
        }
    }

    @Inject(method = "playSeededSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;FFJ)V", at = @At("HEAD"), cancellable = true)
    public void playSeededSound$0(@Nullable Player player, double x, double y, double z, Holder<SoundEvent> soundEvent, SoundSource source, float volume, float pitch, long seed, CallbackInfo callback) {
        this.puzzleslib$sound.set(DefaultedValue.fromValue(soundEvent));
        this.puzzleslib$source.set(DefaultedValue.fromValue(source));
        this.puzzleslib$volume.set(DefaultedFloat.fromValue(volume));
        this.puzzleslib$pitch.set(DefaultedFloat.fromValue(pitch));
        EventResult result = FabricLevelEvents.PLAY_LEVEL_SOUND_AT_POSITION.invoker().onPlaySoundAtPosition(this, new Vec3(x, y, z), this.puzzleslib$sound.get(), this.puzzleslib$source.get(), this.puzzleslib$volume.get(), this.puzzleslib$pitch.get());
        if (result.isInterrupt() || this.puzzleslib$sound.get().get() == null) callback.cancel();
    }

    @Inject(method = "playSeededSound(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;FFJ)V", at = @At("HEAD"), cancellable = true)
    public void playSeededSound$0(@Nullable Player player, Entity entity, Holder<SoundEvent> soundEvent, SoundSource source, float volume, float pitch, long seed, CallbackInfo callback) {
        this.puzzleslib$sound.set(DefaultedValue.fromValue(soundEvent));
        this.puzzleslib$source.set(DefaultedValue.fromValue(source));
        this.puzzleslib$volume.set(DefaultedFloat.fromValue(volume));
        this.puzzleslib$pitch.set(DefaultedFloat.fromValue(pitch));
        EventResult result = FabricLevelEvents.PLAY_LEVEL_SOUND_AT_ENTITY.invoker().onPlaySoundAtEntity(this, entity, this.puzzleslib$sound.get(), this.puzzleslib$source.get(), this.puzzleslib$volume.get(), this.puzzleslib$pitch.get());
        if (result.isInterrupt() || this.puzzleslib$sound.get().get() == null) callback.cancel();
    }

    @ModifyVariable(method = {"playSeededSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;FFJ)V", "playSeededSound(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;FFJ)V"}, at = @At("HEAD"), ordinal = 0)
    public Holder<SoundEvent> playSeededSound$1(Holder<SoundEvent> soundEvent) {
        Objects.requireNonNull(this.puzzleslib$sound.get(), "sound is null");
        soundEvent = this.puzzleslib$sound.get().getAsOptional().orElse(soundEvent);
        this.puzzleslib$sound.remove();
        return soundEvent;
    }

    @ModifyVariable(method = {"playSeededSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;FFJ)V", "playSeededSound(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;FFJ)V"}, at = @At("HEAD"), ordinal = 0)
    public SoundSource playSeededSound$2(SoundSource soundSource) {
        Objects.requireNonNull(this.puzzleslib$source.get(), "source is null");
        soundSource = this.puzzleslib$source.get().getAsOptional().orElse(soundSource);
        this.puzzleslib$source.remove();
        return soundSource;
    }

    @ModifyVariable(method = {"playSeededSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;FFJ)V", "playSeededSound(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;FFJ)V"}, at = @At("HEAD"), ordinal = 0)
    public float playSeededSound$3(float volume) {
        Objects.requireNonNull(this.puzzleslib$volume.get(), "sound is null");
        volume = this.puzzleslib$volume.get().getAsOptionalFloat().orElse(volume);
        this.puzzleslib$volume.remove();
        return volume;
    }

    @ModifyVariable(method = {"playSeededSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;FFJ)V", "playSeededSound(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;FFJ)V"}, at = @At("HEAD"), ordinal = 1)
    public float playSeededSound$4(float pitch) {
        Objects.requireNonNull(this.puzzleslib$pitch.get(), "pitch is null");
        pitch = this.puzzleslib$pitch.get().getAsOptionalFloat().orElse(pitch);
        this.puzzleslib$pitch.remove();
        return pitch;
    }
}
