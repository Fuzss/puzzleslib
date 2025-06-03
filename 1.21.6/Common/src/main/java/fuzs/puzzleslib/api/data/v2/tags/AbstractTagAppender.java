package fuzs.puzzleslib.api.data.v2.tags;

import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import net.minecraft.core.Holder;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public abstract class AbstractTagAppender<T> implements TagAppender<T, T> {
    protected final TagBuilder tagBuilder;
    @Nullable
    private final Function<T, ResourceKey<T>> keyExtractor;

    protected AbstractTagAppender(TagBuilder tagBuilder, @Nullable Function<T, ResourceKey<T>> keyExtractor) {
        this.tagBuilder = tagBuilder;
        this.keyExtractor = keyExtractor;
    }

    public abstract AbstractTagAppender<T> setReplace(boolean replace);

    public AbstractTagAppender<T> setReplace() {
        return this.setReplace(true);
    }

    public AbstractTagAppender<T> add(String string) {
        return this.add(ResourceLocationHelper.parse(string));
    }

    public AbstractTagAppender<T> add(String... strings) {
        for (String string : strings) {
            this.add(string);
        }
        return this;
    }

    public AbstractTagAppender<T> add(ResourceLocation resourceLocation) {
        this.tagBuilder.addElement(resourceLocation);
        return this;
    }

    public AbstractTagAppender<T> add(ResourceLocation... resourceLocations) {
        for (ResourceLocation resourceLocation : resourceLocations) {
            this.add(resourceLocation);
        }
        return this;
    }

    public AbstractTagAppender<T> addKey(ResourceKey<? extends T> resourceKey) {
        return this.add(resourceKey.location());
    }

    @SafeVarargs
    public final AbstractTagAppender<T> addKey(ResourceKey<? extends T>... resourceKeys) {
        for (ResourceKey<? extends T> resourceKey : resourceKeys) {
            this.addKey(resourceKey);
        }
        return this;
    }

    @Override
    public AbstractTagAppender<T> add(T value) {
        return this.addKey(this.keyExtractor().apply(value));
    }

    @SafeVarargs
    @Override
    public final AbstractTagAppender<T> add(T... values) {
        for (T value : values) {
            this.add(value);
        }
        return this;
    }

    public AbstractTagAppender<T> add(Holder.Reference<? extends T> holder) {
        return this.addKey(holder.key());
    }

    @SafeVarargs
    public final AbstractTagAppender<T> add(Holder.Reference<? extends T>... holders) {
        for (Holder.Reference<? extends T> holder : holders) {
            this.add(holder);
        }
        return this;
    }

    public AbstractTagAppender<T> addOptional(String string) {
        return this.addOptional(ResourceLocationHelper.parse(string));
    }

    public AbstractTagAppender<T> addOptional(String... strings) {
        for (String string : strings) {
            this.addOptional(string);
        }
        return this;
    }

    public AbstractTagAppender<T> addOptional(ResourceLocation resourceLocation) {
        this.tagBuilder.addOptionalElement(resourceLocation);
        return this;
    }

    public AbstractTagAppender<T> addOptional(ResourceLocation... resourceLocations) {
        for (ResourceLocation resourceLocation : resourceLocations) {
            this.add(resourceLocation);
        }
        return this;
    }

    public AbstractTagAppender<T> addOptional(ResourceKey<? extends T> resourceKey) {
        return this.addOptional(resourceKey.location());
    }

    @SafeVarargs
    public final AbstractTagAppender<T> addOptional(ResourceKey<? extends T>... resourceKeys) {
        for (ResourceKey<? extends T> resourceKey : resourceKeys) {
            this.addOptional(resourceKey);
        }
        return this;
    }

    @Override
    public AbstractTagAppender<T> addOptional(T value) {
        return this.addOptional(this.keyExtractor().apply(value));
    }

    @SafeVarargs
    public final AbstractTagAppender<T> addOptional(T... values) {
        for (T value : values) {
            this.addOptional(value);
        }
        return this;
    }

    public AbstractTagAppender<T> addTag(String string) {
        return this.addTag(ResourceLocationHelper.parse(string));
    }

    public AbstractTagAppender<T> addTag(String... strings) {
        for (String string : strings) {
            this.addTag(string);
        }
        return this;
    }

    public AbstractTagAppender<T> addTag(ResourceLocation resourceLocation) {
        this.tagBuilder.addTag(resourceLocation);
        return this;
    }

    public AbstractTagAppender<T> addTag(ResourceLocation... resourceLocations) {
        for (ResourceLocation resourceLocation : resourceLocations) {
            this.addTag(resourceLocation);
        }
        return this;
    }

    @Override
    public AbstractTagAppender<T> addTag(TagKey<T> tagKey) {
        return this.addTag(tagKey.location());
    }

    @SafeVarargs
    public final AbstractTagAppender<T> addTag(TagKey<T>... tagKeys) {
        for (TagKey<T> tagKey : tagKeys) {
            this.addTag(tagKey);
        }
        return this;
    }

    public AbstractTagAppender<T> addOptionalTag(String string) {
        return this.addOptionalTag(ResourceLocationHelper.parse(string));
    }

    public AbstractTagAppender<T> addOptionalTag(String... strings) {
        for (String string : strings) {
            this.addOptionalTag(string);
        }
        return this;
    }

    public AbstractTagAppender<T> addOptionalTag(ResourceLocation resourceLocation) {
        this.tagBuilder.addOptionalTag(resourceLocation);
        return this;
    }

    public AbstractTagAppender<T> addOptionalTag(ResourceLocation... resourceLocations) {
        for (ResourceLocation resourceLocation : resourceLocations) {
            this.addOptionalTag(resourceLocation);
        }
        return this;
    }

    @Override
    public AbstractTagAppender<T> addOptionalTag(TagKey<T> tagKey) {
        return this.addOptionalTag(tagKey.location());
    }

    @SafeVarargs
    public final AbstractTagAppender<T> addOptionalTag(TagKey<T>... tagKeys) {
        for (TagKey<T> tagKey : tagKeys) {
            this.addOptionalTag(tagKey);
        }
        return this;
    }

    public AbstractTagAppender<T> remove(String string) {
        return this.remove(ResourceLocationHelper.parse(string));
    }

    public AbstractTagAppender<T> remove(String... strings) {
        for (String string : strings) {
            this.remove(string);
        }
        return this;
    }

    public abstract AbstractTagAppender<T> remove(ResourceLocation resourceLocation);

    public AbstractTagAppender<T> remove(ResourceLocation... resourceLocations) {
        for (ResourceLocation resourceLocation : resourceLocations) {
            this.remove(resourceLocation);
        }
        return this;
    }

    public AbstractTagAppender<T> remove(ResourceKey<T> resourceKey) {
        return this.remove(resourceKey.location());
    }

    @SafeVarargs
    public final AbstractTagAppender<T> remove(ResourceKey<T>... resourceKeys) {
        for (ResourceKey<T> resourceKey : resourceKeys) {
            this.remove(resourceKey);
        }
        return this;
    }

    public AbstractTagAppender<T> remove(T value) {
        return this.remove(this.keyExtractor().apply(value));
    }

    @SafeVarargs
    public final AbstractTagAppender<T> remove(T... values) {
        for (T value : values) {
            this.remove(value);
        }
        return this;
    }

    public AbstractTagAppender<T> removeTag(String string) {
        return this.removeTag(ResourceLocationHelper.parse(string));
    }

    public AbstractTagAppender<T> removeTag(String... strings) {
        for (String string : strings) {
            this.removeTag(string);
        }
        return this;
    }

    public abstract AbstractTagAppender<T> removeTag(ResourceLocation resourceLocation);

    public AbstractTagAppender<T> removeTag(ResourceLocation... resourceLocations) {
        for (ResourceLocation resourceLocation : resourceLocations) {
            this.removeTag(resourceLocation);
        }
        return this;
    }

    public AbstractTagAppender<T> removeTag(TagKey<T> tagKey) {
        return this.removeTag(tagKey.location());
    }

    @SafeVarargs
    public final AbstractTagAppender<T> removeTag(TagKey<T>... tagKeys) {
        for (TagKey<T> tagKey : tagKeys) {
            this.removeTag(tagKey);
        }
        return this;
    }

    private Function<T, ResourceKey<T>> keyExtractor() {
        Objects.requireNonNull(this.keyExtractor, "key extractor is null");
        return this.keyExtractor;
    }

    public abstract List<String> asStringList();
}
