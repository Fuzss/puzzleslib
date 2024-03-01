package fuzs.puzzleslib.neoforge.impl.data;

import fuzs.puzzleslib.api.data.v2.tags.AbstractTagAppender;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

@SuppressWarnings("unchecked")
public final class NeoForgeTagAppender<T> extends AbstractTagAppender<T> {
    private final String modId;

    public NeoForgeTagAppender(TagBuilder tagBuilder, String modId, @Nullable Function<T, ResourceKey<T>> keyExtractor) {
        super(tagBuilder, keyExtractor);
        this.modId = modId;
    }

    @Override
    public AbstractTagAppender<T> setReplace(boolean replace) {
        this.tagBuilder.replace(replace);
        return this;
    }

    @Override
    public AbstractTagAppender<T> remove(String... strings) {
        for (String string : strings) {
            this.tagBuilder.removeElement(new ResourceLocation(string), this.modId);
        }
        return this;
    }

    @Override
    public AbstractTagAppender<T> remove(ResourceLocation... resourceLocations) {
        for (ResourceLocation resourceLocation : resourceLocations) {
            this.tagBuilder.removeElement(resourceLocation, this.modId);
        }
        return this;
    }

    @Override
    public AbstractTagAppender<T> remove(ResourceKey<T>... resourceKeys) {
        for (ResourceKey<T> resourceKey : resourceKeys) {
            this.tagBuilder.removeElement(resourceKey.location(), this.modId);
        }
        return this;
    }

    @Override
    public AbstractTagAppender<T> removeTag(String... strings) {
        for (String string : strings) {
            this.tagBuilder.removeTag(new ResourceLocation(string), this.modId);
        }
        return this;
    }

    @Override
    public AbstractTagAppender<T> removeTag(ResourceLocation... resourceLocations) {
        for (ResourceLocation resourceLocation : resourceLocations) {
            this.tagBuilder.removeTag(resourceLocation, this.modId);
        }
        return this;
    }

    @Override
    public AbstractTagAppender<T> removeTag(TagKey<T>... tagKeys) {
        for (TagKey<T> tagKey : tagKeys) {
            this.tagBuilder.removeTag(tagKey.location(), this.modId);
        }
        return this;
    }
}
