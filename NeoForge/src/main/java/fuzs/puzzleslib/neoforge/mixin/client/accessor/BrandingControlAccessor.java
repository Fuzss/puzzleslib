package fuzs.puzzleslib.neoforge.mixin.client.accessor;

import net.neoforged.neoforge.internal.BrandingControl;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(BrandingControl.class)
public interface BrandingControlAccessor {
    @Accessor("brandings")
    static void puzzleslib$setBrandings(@Nullable List<String> brandings) {
        throw new RuntimeException();
    }

    @Accessor("overCopyrightBrandings")
    static void puzzleslib$setOverCopyrightBrandings(@Nullable List<String> overCopyrightBrandings) {
        throw new RuntimeException();
    }
}
