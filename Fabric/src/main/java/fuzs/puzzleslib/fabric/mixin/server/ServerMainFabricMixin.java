package fuzs.puzzleslib.fabric.mixin.server;

import fuzs.puzzleslib.fabric.api.event.v1.FabricLifecycleEvents;
import net.minecraft.server.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Main.class)
abstract class ServerMainFabricMixin {

    @Inject(method = "main", at = @At(value = "NEW", target = "net/minecraft/server/dedicated/DedicatedServerSettings"))
    private static void main(String[] args, CallbackInfo callback) {
        // Run after Fabric Data Generation Api to mirror NeoForge where load complete does not run
        // during data generation.
        FabricLifecycleEvents.LOAD_COMPLETE.invoker().onLoadComplete();
    }
}
