package fuzs.puzzleslib.fabric.impl.data;

import fuzs.puzzleslib.api.data.v2.tags.AbstractTagAppender;
import net.fabricmc.fabric.impl.datagen.FabricTagBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public final class FabricTagAppender<T> extends AbstractTagAppender<T> {

    public FabricTagAppender(TagBuilder tagBuilder, @Nullable Function<T, ResourceKey<T>> keyExtractor) {
        super(tagBuilder, keyExtractor);
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public AbstractTagAppender<T> setReplace(boolean replace) {
        ((FabricTagBuilder) this.tagBuilder).fabric_setReplace(replace);
        return this;
    }

    @Override
    public AbstractTagAppender<T> remove(ResourceLocation resourceLocation) {
        // only supported on Forge & NeoForge
        return this;
    }

    @Override
    public AbstractTagAppender<T> removeTag(ResourceLocation resourceLocation) {
        // only supported on Forge & NeoForge
        return this;
    }
}
