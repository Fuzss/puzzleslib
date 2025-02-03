package fuzs.puzzleslib.neoforge.mixin.client;

import com.mojang.logging.LogUtils;
import net.minecraft.client.main.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Deprecated
@Mixin(Main.class)
abstract class ClientMainNeoForgeMixin {

    @Inject(method = "main", at = @At("HEAD"), remap = false)
    private static void main(String[] strings, CallbackInfo callback) {
        Set<String> set = new HashSet<>(Arrays.asList(strings));
        if (set.contains("--all") && set.contains("--mod")) {
            try {
                net.minecraft.client.data.Main.main(strings);
            } catch (Throwable throwable) {
                LogUtils.getLogger().error("Data generation failed", throwable);
            } finally {
                System.exit(0);
            }
        }
    }
}
