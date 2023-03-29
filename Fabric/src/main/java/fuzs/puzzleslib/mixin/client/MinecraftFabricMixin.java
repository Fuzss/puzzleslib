package fuzs.puzzleslib.mixin.client;

import fuzs.puzzleslib.api.client.event.v1.FabricScreenEvents;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.DefaultedValue;
import fuzs.puzzleslib.api.event.v1.data.MutableValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(Minecraft.class)
public abstract class MinecraftFabricMixin {
    @Shadow
    @Nullable
    public ClientLevel level;
    @Shadow
    @Nullable
    public LocalPlayer player;
    @Shadow
    @Nullable
    public Screen screen;
    @Unique
    private MutableValue<Screen> stylisheffects$newScreen;

    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    public void setScreen$0(@Nullable Screen newScreen, CallbackInfo callback) {
        // we handle this callback at head to avoid having to block Screen#remove
        this.stylisheffects$newScreen = this.puzzleslib$handleEmptyScreen(newScreen);
        EventResult result = FabricScreenEvents.SCREEN_OPENING.invoker().onScreenOpening(this.screen, this.stylisheffects$newScreen);
        if (result.isInterrupt()) callback.cancel();
    }

    @Unique
    private MutableValue<Screen> puzzleslib$handleEmptyScreen(@Nullable Screen newScreen) {
        // replicate this vanilla functionality, it runs after the screen is removed, but we need it before that to potentially block the removal
        if (newScreen == null && this.level == null) {
            return MutableValue.fromValue(new TitleScreen());
        } else if (newScreen == null && this.player.isDeadOrDying()) {
            if (this.player.shouldShowDeathScreen()) {
                return MutableValue.fromValue(new DeathScreen(null, this.level.getLevelData().isHardcore()));
            } else {
                this.player.respawn();
            }
        }
        return DefaultedValue.fromValue(newScreen);
    }

    @ModifyVariable(method = "setScreen", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;screen:Lnet/minecraft/client/gui/screens/Screen;", shift = At.Shift.BEFORE), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;respawn()V")), ordinal = 0)
    public Screen setScreen$1(@Nullable Screen newScreen) {
        Objects.requireNonNull(this.stylisheffects$newScreen, "new screen is null");
        if (this.stylisheffects$newScreen instanceof DefaultedValue<Screen> defaultedValue) {
            newScreen = defaultedValue.getAsOptional().orElse(newScreen);
        } else {
            // since we are forced to create our own title / death screen instances make sure those are definitely set,
            // in case some event listeners depends on the exact instance reference
            newScreen = this.stylisheffects$newScreen.get();
        }
        this.stylisheffects$newScreen = null;
        return newScreen;
    }

//    @ModifyVariable(method = "setScreen", at = @At("LOAD"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;respawn()V"), to = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/BufferUploader;reset()V")), ordinal = 0)
//    public Screen setScreen$1(@Nullable Screen newScreen) {
//        Objects.requireNonNull(this.stylisheffects$newScreen, "new screen is null");
//        if (this.stylisheffects$newScreen instanceof DefaultedValue<Screen> defaultedValue) {
//            newScreen = defaultedValue.getAsOptional().orElse(newScreen);
//        } else {
//            // since we are forced to create our own title / death screen instances make sure those are definitely set,
//            // in case something event listeners depend on the exact instance reference
//            newScreen = this.stylisheffects$newScreen.get();
//        }
//        this.stylisheffects$newScreen = null;
//        return newScreen;
//    }
}
