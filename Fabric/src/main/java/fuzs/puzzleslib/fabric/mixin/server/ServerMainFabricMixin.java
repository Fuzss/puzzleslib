package fuzs.puzzleslib.fabric.mixin.server;

import fuzs.puzzleslib.fabric.api.event.v1.FabricLifecycleEvents;
import net.minecraft.server.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Main.class)
abstract class ServerMainFabricMixin {

    @Inject(
            method = "main",
            at = @At(
                    value = "NEW",
                    target = "net/minecraft/server/dedicated/DedicatedServerSettings",
                    shift = At.Shift.AFTER
            )
    )
    private static void main(String[] strings, CallbackInfo callback) {
        // run after Fabric Data Generation Api for same behavior as Forge where load complete does not run
        // during data generation (not that we use Fabric's data generation, but ¯\_(ツ)_/¯)
        FabricLifecycleEvents.LOAD_COMPLETE.invoker().onLoadComplete();
    }
}
