package fuzs.puzzleslib.fabric.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import fuzs.puzzleslib.fabric.impl.client.core.context.BuiltInBlockModelsContextFabricImpl;
import net.minecraft.client.renderer.block.BuiltInBlockModels;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(BuiltInBlockModels.class)
abstract class BuiltInBlockModelsFabricMixin {

    @Inject(method = "createBlockModels",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/client/renderer/block/BuiltInBlockModels$Builder;build()Ljava/util/Map;"))
    private static void createBlockModels(CallbackInfoReturnable<Map<BlockState, BlockModel.Unbaked>> callback, @Local BuiltInBlockModels.Builder builder) {
        BuiltInBlockModelsContextFabricImpl.createBlockModels(builder);
    }
}
