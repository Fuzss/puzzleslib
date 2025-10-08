package fuzs.puzzleslib.fabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import fuzs.puzzleslib.fabric.api.event.v1.FabricLifecycleEvents;
import fuzs.puzzleslib.fabric.impl.event.FabricEventImplHelper;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(ReloadableServerResources.class)
abstract class ReloadableServerResourcesFabricMixin {

    @ModifyExpressionValue(method = "lambda$loadResources$1(Lnet/minecraft/world/flag/FeatureFlagSet;Lnet/minecraft/commands/Commands$CommandSelection;Ljava/util/List;ILnet/minecraft/server/packs/resources/ResourceManager;Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;Lnet/minecraft/server/ReloadableServerRegistries$LoadResult;)Ljava/util/concurrent/CompletionStage;",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/server/ReloadableServerResources;listeners()Ljava/util/List;"))
    private static List<PreparableReloadListener> loadResources(List<PreparableReloadListener> reloadListeners, @Local(
            argsOnly = true) ReloadableServerRegistries.LoadResult loadResult, @Local ReloadableServerResources reloadableServerResources) {
        MutableObject<List<PreparableReloadListener>> mutableObject = new MutableObject<>(reloadListeners);
        FabricLifecycleEvents.ADD_DATA_PACK_RELOAD_LISTENERS.invoker()
                .onAddDataPackReloadListeners(reloadableServerResources,
                        loadResult.lookupWithUpdatedTags(),
                        FabricEventImplHelper.getReloadListenerConsumer(mutableObject));
        return mutableObject.getValue();
    }
}
