package fuzs.puzzleslib.forge.impl.data;

import fuzs.puzzleslib.api.data.v2.tags.AbstractTagAppender;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public final class ForgeTagAppender<T> extends AbstractTagAppender<T> {
    private final String modId;

    public ForgeTagAppender(TagBuilder tagBuilder, String modId, @Nullable Function<T, ResourceKey<T>> keyExtractor) {
        super(tagBuilder, keyExtractor);
        this.modId = modId;
    }

    @Override
    public AbstractTagAppender<T> setReplace(boolean replace) {
        this.tagBuilder.replace(replace);
        return this;
    }

    @Override
    public AbstractTagAppender<T> remove(ResourceLocation resourceLocation) {
        this.tagBuilder.removeElement(resourceLocation, this.modId);
        return this;
    }

    @Override
    public AbstractTagAppender<T> removeTag(ResourceLocation resourceLocation) {
        this.tagBuilder.removeTag(resourceLocation, this.modId);
        return this;
    }
}
