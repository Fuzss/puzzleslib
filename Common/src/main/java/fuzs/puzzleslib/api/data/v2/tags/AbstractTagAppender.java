package fuzs.puzzleslib.api.data.v2.tags;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

@SuppressWarnings("unchecked")
public abstract class AbstractTagAppender<T> {
    protected final TagBuilder tagBuilder;
    @Nullable
    private final Function<T, ResourceKey<T>> keyExtractor;

    protected AbstractTagAppender(TagBuilder tagBuilder, @Nullable Function<T, ResourceKey<T>> keyExtractor) {
        this.tagBuilder = tagBuilder;
        this.keyExtractor = keyExtractor;
    }

    public AbstractTagAppender<T> add(String... strings) {
        for (String string : strings) {
            this.tagBuilder.addElement(new ResourceLocation(string));
        }
        return this;
    }

    public AbstractTagAppender<T> add(ResourceLocation... resourceLocations) {
        for (ResourceLocation resourceLocation : resourceLocations) {
            this.tagBuilder.addElement(resourceLocation);
        }
        return this;
    }

    public AbstractTagAppender<T> add(ResourceKey<T>... resourceKeys) {
        for (ResourceKey<T> resourceKey : resourceKeys) {
            this.tagBuilder.addElement(resourceKey.location());
        }
        return this;
    }

    public AbstractTagAppender<T> add(T... values) {
        if (this.keyExtractor == null) {
            throw new UnsupportedOperationException("key extractor is null");
        } else {
            for (T value : values) {
                this.tagBuilder.addElement(this.keyExtractor.apply(value).location());
            }
            return this;
        }
    }

    public AbstractTagAppender<T> addOptional(String... strings) {
        for (String string : strings) {
            this.tagBuilder.addOptionalElement(new ResourceLocation(string));
        }
        return this;
    }

    public AbstractTagAppender<T> addOptional(ResourceLocation... resourceLocations) {
        for (ResourceLocation resourceLocation : resourceLocations) {
            this.tagBuilder.addOptionalElement(resourceLocation);
        }
        return this;
    }

    public AbstractTagAppender<T> addOptional(ResourceKey<T>... resourceKeys) {
        for (ResourceKey<T> resourceKey : resourceKeys) {
            this.tagBuilder.addOptionalElement(resourceKey.location());
        }
        return this;
    }

    public AbstractTagAppender<T> addTag(String... strings) {
        for (String string : strings) {
            this.tagBuilder.addTag(new ResourceLocation(string));
        }
        return this;
    }

    public AbstractTagAppender<T> addTag(ResourceLocation... resourceLocations) {
        for (ResourceLocation resourceLocation : resourceLocations) {
            this.tagBuilder.addTag(resourceLocation);
        }
        return this;
    }

    public AbstractTagAppender<T> addTag(TagKey<T>... tagKeys) {
        for (TagKey<T> tagKey : tagKeys) {
            this.tagBuilder.addTag(tagKey.location());
        }
        return this;
    }

    public AbstractTagAppender<T> addOptionalTag(String... strings) {
        for (String string : strings) {
            this.tagBuilder.addOptionalTag(new ResourceLocation(string));
        }
        return this;
    }

    public AbstractTagAppender<T> addOptionalTag(ResourceLocation... resourceLocations) {
        for (ResourceLocation resourceLocation : resourceLocations) {
            this.tagBuilder.addOptionalTag(resourceLocation);
        }
        return this;
    }

    public AbstractTagAppender<T> addOptionalTag(TagKey<T>... tagKeys) {
        for (TagKey<T> tagKey : tagKeys) {
            this.tagBuilder.addOptionalTag(tagKey.location());
        }
        return this;
    }

    public abstract AbstractTagAppender<T> setReplace(boolean replace);

    public AbstractTagAppender<T> setReplace() {
        return this.setReplace(true);
    }

    public AbstractTagAppender<T> remove(String... strings) {
        // only supported on Forge & NeoForge
        return this;
    }

    public AbstractTagAppender<T> remove(ResourceLocation... resourceLocations) {
        // only supported on Forge & NeoForge
        return this;
    }

    public AbstractTagAppender<T> remove(ResourceKey<T>... resourceKeys) {
        // only supported on Forge & NeoForge
        return this;
    }

    public AbstractTagAppender<T> removeTag(String... strings) {
        // only supported on Forge & NeoForge
        return this;
    }

    public AbstractTagAppender<T> removeTag(ResourceLocation... resourceLocations) {
        // only supported on Forge & NeoForge
        return this;
    }

    public AbstractTagAppender<T> removeTag(TagKey<T>... tagKeys) {
        // only supported on Forge & NeoForge
        return this;
    }
}
