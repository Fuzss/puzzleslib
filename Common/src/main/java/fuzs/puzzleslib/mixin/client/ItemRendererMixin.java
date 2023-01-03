package fuzs.puzzleslib.mixin.client;

import fuzs.puzzleslib.impl.client.renderer.item.ItemModelOverridesImpl;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(ItemRenderer.class)
abstract class ItemRendererMixin {
    @Final
    @Shadow
    private ItemModelShaper itemModelShaper;

    @ModifyVariable(method = "render", at = @At("HEAD"), argsOnly = true)
    public BakedModel puzzleslib$render(BakedModel bakedModel, ItemStack stack, ItemTransforms.TransformType transformType) {
        return ItemModelOverridesImpl.getModelByType(stack, this.itemModelShaper, transformType).orElse(bakedModel);
    }

    @ModifyVariable(method = "getModel", at = @At("STORE"), ordinal = 0, slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemModelShaper;getItemModel(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/client/resources/model/BakedModel;")))
    public BakedModel puzzleslib$getModel(BakedModel bakedModel, ItemStack stack) {
        return ItemModelOverridesImpl.getModelByType(stack, this.itemModelShaper, null).orElse(bakedModel);
    }
}
