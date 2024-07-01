package fuzs.puzzleslib.fabric.mixin.server;

import fuzs.puzzleslib.fabric.api.event.v1.FabricLifecycleEvents;
import net.minecraft.server.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Run after Fabric Data Generation Api for same behavior as Forge where load complete does not run during data
 * generation (in case we ever decide to use Fabric's data generation api).
 */
@Mixin(value = Main.class, priority = 1200)
abstract class MainFabricMixin {

    @Inject(
            method = "main", at = @At(value = "NEW", target = "net/minecraft/server/dedicated/DedicatedServerSettings")
    )
    private static void main(String[] strings, CallbackInfo callback) {
        FabricLifecycleEvents.SERVER_LOAD_COMPLETE.invoker().onLoadComplete();
    }
}
