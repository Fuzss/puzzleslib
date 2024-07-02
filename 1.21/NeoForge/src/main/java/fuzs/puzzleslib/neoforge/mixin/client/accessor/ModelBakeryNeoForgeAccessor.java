package fuzs.puzzleslib.neoforge.mixin.client.accessor;

import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ModelBakery.class)
public interface ModelBakeryNeoForgeAccessor {

    @Accessor("ITEM_MODEL_GENERATOR")
    static ItemModelGenerator puzzleslib$getItemModelGenerator() {
        throw new RuntimeException();
    }

    @Invoker("getModel")
    UnbakedModel puzzleslib$callGetModel(ResourceLocation modelLocation);
}
