package fuzs.puzzleslib.fabric.mixin.client;

import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.fabric.api.client.event.v1.FabricClientLevelEvents;
import fuzs.puzzleslib.fabric.api.client.event.v1.FabricClientPlayerEvents;
import fuzs.puzzleslib.fabric.api.client.event.v1.FabricGuiEvents;
import fuzs.puzzleslib.fabric.api.client.event.v1.FabricRendererEvents;
import fuzs.puzzleslib.fabric.api.event.v1.FabricLifecycleEvents;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.Connection;
import net.minecraft.world.phys.HitResult;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(Minecraft.class)
abstract class MinecraftFabricMixin {
    @Shadow
    @Final
    public GameRenderer gameRenderer;
    @Shadow
    @Final
    private DeltaTracker.Timer deltaTracker;
    @Shadow
    @Nullable public ClientLevel level;
    @Shadow
    @Nullable public LocalPlayer player;
    @Shadow
    @Nullable public MultiPlayerGameMode gameMode;
    @Shadow
    @Nullable public HitResult hitResult;
    @Shadow
    @Nullable public Screen screen;

    @Inject(method = "<init>",
            at = @At(value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/systems/RenderSystem;getBackendDescription()Ljava/lang/String;",
                    shift = At.Shift.AFTER,
                    remap = false))
    public void init(CallbackInfo callback) {
        // run after Fabric Data Generation Api for same behavior as Forge where load complete does not run
        // during data generation (not that we use Fabric's data generation, but ¯\_(ツ)_/¯)
        FabricLifecycleEvents.LOAD_COMPLETE.invoker().onLoadComplete();
    }

    @Inject(method = "runTick",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/GameRenderer;render(Lnet/minecraft/client/DeltaTracker;Z)V"))
    private void runTick$0(boolean renderLevel, CallbackInfo callback) {
        FabricRendererEvents.BEFORE_GAME_RENDER.invoker()
                .onBeforeGameRender(Minecraft.class.cast(this), this.gameRenderer, this.deltaTracker);
    }

    @Inject(method = "runTick",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/GameRenderer;render(Lnet/minecraft/client/DeltaTracker;Z)V",
                    shift = At.Shift.AFTER))
    private void runTick$1(boolean renderLevel, CallbackInfo callback) {
        FabricRendererEvents.AFTER_GAME_RENDER.invoker()
                .onAfterGameRender(Minecraft.class.cast(this), this.gameRenderer, this.deltaTracker);
    }

    @ModifyVariable(method = "setScreen",
            at = @At(value = "LOAD", ordinal = 0),
            slice = @Slice(from = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;respawn()V")),
            ordinal = 0,
            argsOnly = true)
    public Screen setScreen(@Nullable Screen newScreen) {
        // this implementation does not allow for cancelling a new screen being set,
        // due to vanilla's Screen::remove call happening before the new screen is properly computed (in regard to title &amp; death screens),
        // making the implementation difficult
        return FabricGuiEvents.SCREEN_OPENING.invoker()
                .onScreenOpening(this.screen, newScreen)
                .getInterrupt()
                .orElse(newScreen);
    }

    @Inject(method = "setLevel", at = @At("HEAD"))
    public void setLevel(ClientLevel clientLevel, CallbackInfo callback) {
        if (this.level != null) {
            FabricClientLevelEvents.UNLOAD_LEVEL.invoker().onLevelUnload(Minecraft.class.cast(this), this.level);
        }
    }

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screens/Screen;ZZ)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/GameRenderer;resetData()V",
                    shift = At.Shift.AFTER))
    public void disconnect(Screen screen, boolean keepResourcePacks, boolean stopSoundManager, CallbackInfo callback) {
        if (this.player != null && this.gameMode != null) {
            Connection connection = this.player.connection.getConnection();
            Objects.requireNonNull(connection, "connection is null");
            FabricClientPlayerEvents.PLAYER_LEAVE.invoker().onPlayerLeave(this.player, this.gameMode, connection);
        }

        if (this.level != null) {
            FabricClientLevelEvents.UNLOAD_LEVEL.invoker().onLevelUnload(Minecraft.class.cast(this), this.level);
        }
    }

    @Inject(method = "pickBlock", at = @At("HEAD"), cancellable = true)
    private void pickBlock(CallbackInfo callback) {
        if (this.hitResult != null && this.hitResult.getType() != HitResult.Type.MISS) {
            EventResult result = FabricClientPlayerEvents.PICK_INTERACTION_INPUT.invoker()
                    .onPickInteraction(Minecraft.class.cast(this), this.player, this.hitResult);
            if (result.isInterrupt()) {
                callback.cancel();
            }
        }
    }
}
