package fuzs.puzzleslib.fabric.mixin.client;

import com.mojang.blaze3d.platform.Window;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Approach is based on that used by <a href="https://github.com/juliand665/retiNO">RetiNO</a> by Julian Dunskus, originally licensed under MIT.
 */
@Mixin(Window.class)
abstract class WindowFabricMixin {
    @Shadow
    private int framebufferWidth;
    @Shadow
    private int framebufferHeight;

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwDefaultWindowHints()V"), remap = false)
    public void onDefaultWindowHints() {
        GLFW.glfwDefaultWindowHints();
        if (!Minecraft.ON_OSX || !FabricLoader.getInstance().isDevelopmentEnvironment()) return;
        GLFW.glfwWindowHint(GLFW.GLFW_COCOA_RETINA_FRAMEBUFFER, GLFW.GLFW_FALSE);
    }

    @Inject(method = "refreshFramebufferSize", at = @At(value = "TAIL"))
    private void refreshFramebufferSize(CallbackInfo callback) {
        if (!Minecraft.ON_OSX || !FabricLoader.getInstance().isDevelopmentEnvironment()) return;
        this.framebufferWidth /= 2;
        this.framebufferHeight /= 2;
    }
}
