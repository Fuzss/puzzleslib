package fuzs.puzzleslib.forge.mixin;

import fuzs.puzzleslib.forge.impl.data.ExistingFileHelperHolder;
import net.minecraft.data.tags.TagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TagsProvider.class)
abstract class TagsProviderForgeMixin<T> implements ExistingFileHelperHolder {
    @Shadow(remap = false)
    @Final
    @Mutable
    private ExistingFileHelper existingFileHelper;

    @Override
    public void puzzleslib$setExistingFileHelper(ExistingFileHelper fileHelper) {
        this.existingFileHelper = fileHelper;
    }
}
