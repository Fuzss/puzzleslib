package fuzs.puzzleslib.fabric.mixin;

import fuzs.puzzleslib.fabric.impl.core.context.DataPackReloadListenersContextFabricImpl;
import fuzs.puzzleslib.fabric.impl.event.FabricEventInvokers;
import net.minecraft.server.ReloadableServerResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Run after Fabric Api for wrapping {@link ReloadableServerResources#updateComponentsAndStaticRegistryTags()}.
 */
@Mixin(value = ReloadableServerResources.class, priority = 1500)
abstract class ReloadableServerResourcesFabricMixin {

    @ModifyVariable(method = "lambda$loadResources$2(Lnet/minecraft/server/ReloadableServerRegistries$LoadResult;Lnet/minecraft/world/flag/FeatureFlagSet;Lnet/minecraft/commands/Commands$CommandSelection;Ljava/util/List;Lnet/minecraft/server/permissions/PermissionSet;Lnet/minecraft/server/packs/resources/ResourceManager;Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;Ljava/util/List;)Ljava/util/concurrent/CompletionStage;",
                    at = @At(value = "STORE", ordinal = 0))
    private static ReloadableServerResources loadResources(ReloadableServerResources result) {
        DataPackReloadListenersContextFabricImpl.setServerResources(result);
        return result;
    }

    @Inject(method = "updateComponentsAndStaticRegistryTags", at = @At("HEAD"))
    public void updateComponentsAndStaticRegistryTags$0(CallbackInfo callback) {
        FabricEventInvokers.SERVER_RESOURCES.set(ReloadableServerResources.class.cast(this));
    }

    @Inject(method = "updateComponentsAndStaticRegistryTags", at = @At("TAIL"))
    public void updateComponentsAndStaticRegistryTags$1(CallbackInfo callback) {
        FabricEventInvokers.SERVER_RESOURCES.remove();
    }
}
