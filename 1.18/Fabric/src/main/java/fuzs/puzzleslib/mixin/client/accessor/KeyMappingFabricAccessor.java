package fuzs.puzzleslib.mixin.client.accessor;

import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(KeyMapping.class)
public interface KeyMappingFabricAccessor {

    @Accessor("clickCount")
    int puzzleslib$getClickCount();
}
