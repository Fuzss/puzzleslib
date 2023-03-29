package fuzs.puzzleslib.mixin.client;

import fuzs.stylisheffects.api.client.event.ExtraScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(Minecraft.class)
public abstract class MinecraftFabricMixin {
    @Shadow
    @Nullable
    public Screen screen;
    @Unique
    private Screen stylisheffects$oldScreen;
    @Unique
    private boolean stylisheffects$keepOldScreen;

    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    public void setScreen(@Nullable Screen screen, CallbackInfo callback) {

    }

    @Inject(method = "setScreen", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;screen:Lnet/minecraft/client/gui/screens/Screen;", ordinal = 0))
    public void stylisheffects$setScreen$0(@Nullable Screen screen, CallbackInfo callback) {
        // we need to save the old screen so vanilla can mostly just run through, and we still have all instances we need
        this.stylisheffects$oldScreen = this.screen;
        // set screen to null so Screen::removed is not called (we will manually call it later if necessary)
        this.screen = null;
    }

    @Inject(method = "setScreen", at = @At(value = "JUMP", opcode = Opcodes.IFNONNULL, ordinal = 0))
    public void stylisheffects$setScreen$1(@Nullable Screen screen, CallbackInfo callback) {
        // return screen to the original value which we had to set to null to prevent Screen::removed from being called
        // this is not necessary in vanilla as everything that happens after this being set to the new screen, but maybe other mixins assume this to still correctly contain the old screen
        this.screen = this.stylisheffects$oldScreen;
    }

    @ModifyVariable(method = "setScreen", at = @At("LOAD"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;respawn()V"), to = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/BufferUploader;reset()V")), ordinal = 0)
    public Screen stylisheffects$setScreen$2(@Nullable Screen screen) {
        this.stylisheffects$keepOldScreen = false;
        Screen oldScreen = this.screen;
        Screen newScreen = screen;
        // at this point the local screen variable has already been set to Minecraft#screen (meaning the new screen is already set)
        if (screen != null) {
            Optional<Screen> result = ExtraScreenEvents.OPENING.invoker().onScreenOpening(oldScreen, screen);
            if (result.isPresent()) {
                newScreen = result.get();
                // only run this when we have set the oldScreen again, other mods might be interfering with their mixins (looking at you FancyMenu lol)
                if (oldScreen == newScreen) {
                    // the old screen has been returned, meaning opening the new screen has been cancelled,
                    // so we return from the method to prevent any setup on the screen
                    this.stylisheffects$keepOldScreen = true;
                    return oldScreen;
                }
            }
        }
        // reaching this point means a new screen has been set successfully, just check now if there was an old screen to begin with
        if (oldScreen != null) {
            ExtraScreenEvents.CLOSING.invoker().onScreenClosing(oldScreen);
            // this is basically the vanilla code we prevented from running earlier by setting Minecraft#screen to null
            oldScreen.removed();
            // this call is skipped earlier by us setting Minecraft#screen to null, so we insert it here as this is where the screen is actually closed now
            ScreenEvents.remove(oldScreen).invoker().onRemove(oldScreen);
        }
        // a new screen has been set, this may or may not already be set to Minecraft#screen
        return newScreen;
    }

    @Inject(method = "setScreen", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;screen:Lnet/minecraft/client/gui/screens/Screen;"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;respawn()V"), to = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/BufferUploader;reset()V")), cancellable = true)
    public void stylisheffects$setScreen$3(@Nullable Screen screen, CallbackInfo callback) {
        // the old screen has been set again, probably by us, so we cancel the rest of the method which is meant to set up the new screen
        if (this.stylisheffects$keepOldScreen) callback.cancel();
    }
}
