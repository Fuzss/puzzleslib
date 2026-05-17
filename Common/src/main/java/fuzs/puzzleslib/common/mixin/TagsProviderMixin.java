package fuzs.puzzleslib.common.mixin;

import com.mojang.serialization.Codec;
import fuzs.puzzleslib.common.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.common.api.data.v2.tags.AbstractTagProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.TagFile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(TagsProvider.class)
abstract class TagsProviderMixin<T> {

    @ModifyArg(method = "lambda$run$5",
               at = @At(value = "INVOKE",
                        target = "Lnet/minecraft/data/DataProvider;saveStable(Lnet/minecraft/data/CachedOutput;Lnet/minecraft/core/HolderLookup$Provider;Lcom/mojang/serialization/Codec;Ljava/lang/Object;Ljava/nio/file/Path;)Ljava/util/concurrent/CompletableFuture;"),
               index = 2)
    private Codec<TagFile> addRemove(Codec<TagFile> codec) {
        return ModLoaderEnvironment.INSTANCE.isDataGeneration() ? AbstractTagProvider.TAG_FILE_CODEC : codec;
    }
}
