package fuzs.puzzleslib.mixin.accessor;

import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.BuiltInPackSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BuiltInPackSource.class)
public interface BuiltInPackSourceAccessor {

    @Accessor("packType")
    PackType puzzleslib$getPackType();
}
