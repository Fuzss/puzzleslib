package fuzs.puzzleslib.neoforge.mixin.client.accessor;

import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.resources.model.ModelBakery;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ModelBakery.class)
public interface ModelBakeryAccessor {

    @Accessor("ITEM_MODEL_GENERATOR")
    static ItemModelGenerator puzzleslib$getItemModelGenerator() {
        throw new RuntimeException();
    }
}
