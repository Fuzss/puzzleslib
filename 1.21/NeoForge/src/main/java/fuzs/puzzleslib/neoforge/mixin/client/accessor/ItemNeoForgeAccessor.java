package fuzs.puzzleslib.neoforge.mixin.client.accessor;

import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Item.class)
public interface ItemNeoForgeAccessor {

    @Accessor(value = "renderProperties", remap = false)
    Object puzzleslib$getRenderProperties();

    @Accessor(value = "renderProperties", remap = false)
    void puzzleslib$setRenderProperties(Object renderProperties);
}
