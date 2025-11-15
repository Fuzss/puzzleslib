package fuzs.puzzleslib.fabric.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.impl.core.ModContext;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.impl.attachment.sync.AttachmentChange;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(AttachmentChange.class)
abstract class AttachmentChangeFabricMixin {
    @Shadow
    @Final
    private AttachmentType<?> type;

    @Inject(method = "tryApply",
            at = @At(value = "NEW", target = "net/fabricmc/fabric/impl/attachment/sync/AttachmentSyncException"),
            cancellable = true,
            require = 0)
    public void tryApply(CallbackInfo callback, @Local MutableComponent errorMessageText) {
        // Prevent clients from disconnecting for failed attachment syncs.
        // This is generally not so severe that it warrants a disconnect.
        if (ModContext.getModContexts().containsKey(this.type.identifier().getPath())) {
            PuzzlesLib.LOGGER.error("Error accepting attachment changes", new Exception(errorMessageText.getString()));
            callback.cancel();
        }
    }
}
