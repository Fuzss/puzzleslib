package fuzs.puzzleslib.mixin.accessor;

import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ForgeRegistry.class)
public interface ForgeRegistryForgeAccessor<V> {

    @Accessor(value = "add", remap = false)
    IForgeRegistry.AddCallback<V> puzzleslib$getAdd();

    @Accessor(value = "add", remap = false)
    @Mutable
    void puzzleslib$setAdd(IForgeRegistry.AddCallback<V> add);
}
