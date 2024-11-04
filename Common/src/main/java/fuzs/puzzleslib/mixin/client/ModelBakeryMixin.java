package fuzs.puzzleslib.mixin.client;

import fuzs.puzzleslib.impl.client.event.ModelLoadingHelper;
import net.minecraft.client.resources.model.BlockStateModelLoader;
import net.minecraft.client.resources.model.ModelBakery;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ModelBakery.class)
abstract class ModelBakeryMixin {

    @ModifyVariable(method = "<init>", at = @At(value = "STORE", ordinal = 0))
    public BlockStateModelLoader init(BlockStateModelLoader blockStateModelLoader) {
        ModelLoadingHelper.setBlockStateModelLoader(blockStateModelLoader);
        return blockStateModelLoader;
    }
}
