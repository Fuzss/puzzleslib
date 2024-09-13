package fuzs.puzzleslib.neoforge.impl.data;

import fuzs.puzzleslib.api.data.v2.tags.AbstractTagAppender;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public final class NeoForgeTagAppender<T> extends AbstractTagAppender<T> {

    public NeoForgeTagAppender(TagBuilder tagBuilder, @Nullable Function<T, ResourceKey<T>> keyExtractor) {
        super(tagBuilder, keyExtractor);
    }

    @Override
    public AbstractTagAppender<T> setReplace(boolean replace) {
        this.tagBuilder.replace(replace);
        return this;
    }

    @Override
    public AbstractTagAppender<T> remove(ResourceLocation resourceLocation) {
        this.tagBuilder.removeElement(resourceLocation);
        return this;
    }

    @Override
    public AbstractTagAppender<T> removeTag(ResourceLocation resourceLocation) {
        this.tagBuilder.removeTag(resourceLocation);
        return this;
    }
}
