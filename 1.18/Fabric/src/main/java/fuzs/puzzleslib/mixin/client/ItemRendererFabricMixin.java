package fuzs.puzzleslib.mixin.client;

import fuzs.puzzleslib.impl.client.event.ItemDecoratorRegistryImpl;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
abstract class ItemRendererFabricMixin {
    @Shadow
    public float blitOffset;

    @Inject(method = "renderGuiItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V", at = @At("TAIL"))
    public void renderGuiItemDecorations(Font font, ItemStack stack, int xPosition, int yPosition, @Nullable String text, CallbackInfo callback) {
        if (!stack.isEmpty()) ItemDecoratorRegistryImpl.render(font, stack, xPosition, yPosition, this.blitOffset);
    }
}
