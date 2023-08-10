package fuzs.puzzleslib.mixin.client;

import fuzs.puzzleslib.api.client.init.v1.ItemModelDisplayOverrides;
import fuzs.puzzleslib.impl.client.event.ItemDecoratorRegistryImpl;
import fuzs.puzzleslib.impl.client.init.FabricItemDisplayOverrides;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
abstract class ItemRendererFabricMixin {
    @Shadow
    public float blitOffset;

    @ModifyVariable(method = "render", at = @At(value = "LOAD", ordinal = 0), ordinal = 0, argsOnly = true)
    public BakedModel render(BakedModel bakedModel, ItemStack stack, ItemTransforms.TransformType itemDisplayContext) {
        return ((FabricItemDisplayOverrides) ItemModelDisplayOverrides.INSTANCE).getItemModelDisplayOverride(bakedModel, itemDisplayContext);
    }

    @Inject(method = "renderGuiItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V", at = @At("TAIL"))
    public void renderGuiItemDecorations(Font font, ItemStack stack, int xPosition, int yPosition, @Nullable String text, CallbackInfo callback) {
        if (!stack.isEmpty()) ItemDecoratorRegistryImpl.render(font, stack, xPosition, yPosition, this.blitOffset);
    }
}
