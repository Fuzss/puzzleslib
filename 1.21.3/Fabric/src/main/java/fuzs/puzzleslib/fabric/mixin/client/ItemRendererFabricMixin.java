package fuzs.puzzleslib.fabric.mixin.client;

import fuzs.puzzleslib.api.client.init.v1.ItemModelDisplayOverrides;
import fuzs.puzzleslib.fabric.impl.client.init.FabricItemDisplayOverrides;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemRenderer.class)
abstract class ItemRendererFabricMixin {

    @ModifyVariable(method = "render", at = @At(value = "LOAD", ordinal = 0), ordinal = 0, argsOnly = true)
    public BakedModel render(BakedModel bakedModel, ItemStack stack, ItemDisplayContext itemDisplayContext) {
        return ((FabricItemDisplayOverrides) ItemModelDisplayOverrides.INSTANCE).getItemModelDisplayOverride(bakedModel,
                itemDisplayContext
        );
    }
}
