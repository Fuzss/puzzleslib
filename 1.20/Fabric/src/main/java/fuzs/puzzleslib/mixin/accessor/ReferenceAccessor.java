package fuzs.puzzleslib.mixin.accessor;

import net.minecraft.core.Holder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Holder.Reference.class)
public interface ReferenceAccessor<T> {

    @Invoker("bindValue")
    void puzzleslib$callBindValue(T value);
}
