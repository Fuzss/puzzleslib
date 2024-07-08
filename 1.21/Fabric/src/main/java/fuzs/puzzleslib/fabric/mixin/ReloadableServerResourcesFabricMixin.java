package fuzs.puzzleslib.fabric.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import fuzs.puzzleslib.fabric.api.core.v1.resources.FabricReloadListener;
import fuzs.puzzleslib.fabric.api.event.v1.FabricLifecycleEvents;
import fuzs.puzzleslib.impl.event.CopyOnWriteForwardingList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(ReloadableServerResources.class)
abstract class ReloadableServerResourcesFabricMixin {

    @WrapOperation(
            method = "lambda$loadResources$2", at = @At(
            value = "INVOKE", target = "Lnet/minecraft/server/ReloadableServerResources;listeners()Ljava/util/List;"
    )
    )
    private static List<PreparableReloadListener> loadResources(ReloadableServerResources serverResources, Operation<List<PreparableReloadListener>> operation) {
        List<PreparableReloadListener> listeners = new CopyOnWriteForwardingList<>(operation.call(serverResources));
        FabricLifecycleEvents.ADD_DATA_PACK_RELOAD_LISTENERS.invoker()
                .onAddDataPackReloadListeners(serverResources.registryLookup,
                        (ResourceLocation resourceLocation, PreparableReloadListener reloadListener) -> {
                            listeners.add(new FabricReloadListener(resourceLocation, reloadListener));
                        }
                );
        return listeners;
    }
}
