package fuzs.puzzleslib.neoforge.mixin;

import fuzs.puzzleslib.neoforge.impl.data.ExistingFileHelperHolder;
import net.minecraft.data.tags.TagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TagsProvider.class)
abstract class TagsProviderNeoForgeMixin<T> implements ExistingFileHelperHolder {
    @Shadow(remap = false)
    @Final
    @Mutable
    private ExistingFileHelper existingFileHelper;

    @Override
    public void puzzleslib$setExistingFileHelper(ExistingFileHelper fileHelper) {
        this.existingFileHelper = fileHelper;
    }
}
