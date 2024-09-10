package fuzs.puzzleslib.neoforge.mixin.accessor;

import net.neoforged.neoforge.registries.NewRegistryEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = NewRegistryEvent.class, remap = false)
public interface NewRegistryEventNeoForgeAccessor {

    @Invoker("<init>")
    static NewRegistryEvent puzzleslib$callInit() {
        throw new RuntimeException();
    }

    @Invoker("fill")
    void puzzleslib$callFill();
}
