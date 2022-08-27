package fuzs.puzzleslib.mixin.client.accessor;

import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Item.class)
public interface ItemAccessor {

    @Accessor
    Object getRenderProperties();

    @Accessor
    void setRenderProperties(Object renderProperties);
}
