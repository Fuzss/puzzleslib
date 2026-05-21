package fuzs.puzzleslib.api.data.v3.tags;

import net.minecraft.core.Holder;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public abstract class AbstractTagAppender<T> extends TagsProvider.TagAppender<T> {
    protected final TagBuilder tagBuilder;
    @Nullable
    private final Function<T, ResourceKey<T>> keyExtractor;

    public AbstractTagAppender(TagBuilder tagBuilder, @Nullable Function<T, ResourceKey<T>> keyExtractor) {
        super(tagBuilder);
        this.tagBuilder = tagBuilder;
        this.keyExtractor = keyExtractor;
    }

    public AbstractTagAppender<T> add(ResourceLocation id) {
        this.tagBuilder.addElement(id);
        return this;
    }

    public AbstractTagAppender<T> add(ResourceLocation... ids) {
        for (ResourceLocation id : ids) {
            this.add(id);
        }

        return this;
    }

    public AbstractTagAppender<T> addKey(ResourceKey<? extends T> key) {
        return this.add(key.location());
    }

    @SafeVarargs
    public final AbstractTagAppender<T> addKey(ResourceKey<? extends T>... keys) {
        for (ResourceKey<? extends T> key : keys) {
            this.addKey(key);
        }

        return this;
    }

    public AbstractTagAppender<T> add(T value) {
        return this.addKey(this.keyExtractor().apply(value));
    }

    @SafeVarargs
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

    public AbstractTagAppender<T> addOptional(String id) {
        return this.addOptional(ResourceLocation.parse(id));
    }

    public AbstractTagAppender<T> addOptional(String... ids) {
        for (String id : ids) {
            this.addOptional(id);
        }

        return this;
    }

    public AbstractTagAppender<T> addOptional(ResourceLocation id) {
        this.tagBuilder.addOptionalElement(id);
        return this;
    }

    public AbstractTagAppender<T> addOptional(ResourceLocation... ids) {
        for (ResourceLocation id : ids) {
            this.add(id);
        }

        return this;
    }

    public AbstractTagAppender<T> addOptionalKey(ResourceKey<? extends T> key) {
        return this.addOptional(key.location());
    }

    @SafeVarargs
    public final AbstractTagAppender<T> addOptionalKey(ResourceKey<? extends T>... keys) {
        for (ResourceKey<? extends T> key : keys) {
            this.addOptionalKey(key);
        }

        return this;
    }

    public AbstractTagAppender<T> addOptional(T value) {
        return this.addOptionalKey(this.keyExtractor().apply(value));
    }

    @SafeVarargs
    public final AbstractTagAppender<T> addOptional(T... values) {
        for (T value : values) {
            this.addOptional(value);
        }

        return this;
    }

    public AbstractTagAppender<T> addOptional(Holder.Reference<? extends T> holder) {
        return this.addOptionalKey(holder.key());
    }

    @SafeVarargs
    public final AbstractTagAppender<T> addOptional(Holder.Reference<? extends T>... holders) {
        for (Holder.Reference<? extends T> holder : holders) {
            this.addOptional(holder);
        }

        return this;
    }

    public AbstractTagAppender<T> addTag(ResourceLocation id) {
        this.tagBuilder.addTag(id);
        return this;
    }

    public AbstractTagAppender<T> addTag(ResourceLocation... ids) {
        for (ResourceLocation id : ids) {
            this.addTag(id);
        }

        return this;
    }

    @Override
    public AbstractTagAppender<T> addTag(TagKey<T> tag) {
        return this.addTag(tag.location());
    }

    @SafeVarargs
    public final AbstractTagAppender<T> addTag(TagKey<T>... tags) {
        for (TagKey<T> tag : tags) {
            this.addTag(tag);
        }

        return this;
    }

    public AbstractTagAppender<T> addOptionalTag(String id) {
        return this.addOptionalTag(ResourceLocation.parse(id));
    }

    public AbstractTagAppender<T> addOptionalTag(String... ids) {
        for (String id : ids) {
            this.addOptionalTag(id);
        }

        return this;
    }

    public AbstractTagAppender<T> addOptionalTag(ResourceLocation id) {
        this.tagBuilder.addOptionalTag(id);
        return this;
    }

    public AbstractTagAppender<T> addOptionalTag(ResourceLocation... ids) {
        for (ResourceLocation id : ids) {
            this.addOptionalTag(id);
        }

        return this;
    }

    public AbstractTagAppender<T> addOptionalTag(TagKey<T> tag) {
        return this.addOptionalTag(tag.location());
    }

    @SafeVarargs
    public final AbstractTagAppender<T> addOptionalTag(TagKey<T>... tags) {
        for (TagKey<T> tag : tags) {
            this.addOptionalTag(tag);
        }

        return this;
    }

    public abstract AbstractTagAppender<T> remove(ResourceLocation id);

    public AbstractTagAppender<T> remove(ResourceLocation... ids) {
        for (ResourceLocation id : ids) {
            this.remove(id);
        }

        return this;
    }

    public AbstractTagAppender<T> removeKey(ResourceKey<? extends T> key) {
        return this.remove(key.location());
    }

    @SafeVarargs
    public final AbstractTagAppender<T> removeKey(ResourceKey<? extends T>... keys) {
        for (ResourceKey<? extends T> key : keys) {
            this.removeKey(key);
        }

        return this;
    }

    public AbstractTagAppender<T> remove(T value) {
        return this.removeKey(this.keyExtractor().apply(value));
    }

    @SafeVarargs
    public final AbstractTagAppender<T> remove(T... values) {
        for (T value : values) {
            this.remove(value);
        }

        return this;
    }

    public AbstractTagAppender<T> remove(Holder.Reference<? extends T> holder) {
        return this.removeKey(holder.key());
    }

    @SafeVarargs
    public final AbstractTagAppender<T> remove(Holder.Reference<? extends T>... holders) {
        for (Holder.Reference<? extends T> holder : holders) {
            this.remove(holder);
        }

        return this;
    }

    public AbstractTagAppender<T> removeOptional(String id) {
        return this.removeOptional(ResourceLocation.parse(id));
    }

    public AbstractTagAppender<T> removeOptional(String... ids) {
        for (String id : ids) {
            this.removeOptional(id);
        }

        return this;
    }

    public abstract AbstractTagAppender<T> removeOptional(ResourceLocation id);

    public AbstractTagAppender<T> removeOptional(ResourceLocation... ids) {
        for (ResourceLocation id : ids) {
            this.removeOptional(id);
        }

        return this;
    }

    public AbstractTagAppender<T> removeOptionalKey(ResourceKey<? extends T> key) {
        return this.removeOptional(key.location());
    }

    @SafeVarargs
    public final AbstractTagAppender<T> removeOptionalKey(ResourceKey<? extends T>... keys) {
        for (ResourceKey<? extends T> key : keys) {
            this.removeOptionalKey(key);
        }

        return this;
    }

    public AbstractTagAppender<T> removeOptional(T value) {
        return this.removeOptionalKey(this.keyExtractor().apply(value));
    }

    @SafeVarargs
    public final AbstractTagAppender<T> removeOptional(T... values) {
        for (T value : values) {
            this.removeOptional(value);
        }

        return this;
    }

    public AbstractTagAppender<T> removeOptional(Holder.Reference<? extends T> holder) {
        return this.removeOptionalKey(holder.key());
    }

    @SafeVarargs
    public final AbstractTagAppender<T> removeOptional(Holder.Reference<? extends T>... holders) {
        for (Holder.Reference<? extends T> holder : holders) {
            this.removeOptional(holder);
        }

        return this;
    }

    public abstract AbstractTagAppender<T> removeTag(ResourceLocation id);

    public AbstractTagAppender<T> removeTag(ResourceLocation... ids) {
        for (ResourceLocation id : ids) {
            this.removeTag(id);
        }

        return this;
    }

    public AbstractTagAppender<T> removeTag(TagKey<T> tag) {
        return this.removeTag(tag.location());
    }

    @SafeVarargs
    public final AbstractTagAppender<T> removeTag(TagKey<T>... tags) {
        for (TagKey<T> tag : tags) {
            this.removeTag(tag);
        }

        return this;
    }

    public AbstractTagAppender<T> removeOptionalTag(String id) {
        return this.removeOptionalTag(ResourceLocation.parse(id));
    }

    public AbstractTagAppender<T> removeOptionalTag(String... ids) {
        for (String id : ids) {
            this.removeOptionalTag(id);
        }

        return this;
    }

    public abstract AbstractTagAppender<T> removeOptionalTag(ResourceLocation id);

    public AbstractTagAppender<T> removeOptionalTag(ResourceLocation... ids) {
        for (ResourceLocation id : ids) {
            this.removeOptionalTag(id);
        }

        return this;
    }

    public AbstractTagAppender<T> removeOptionalTag(TagKey<T> tag) {
        return this.removeOptionalTag(tag.location());
    }

    @SafeVarargs
    public final AbstractTagAppender<T> removeOptionalTag(TagKey<T>... tags) {
        for (TagKey<T> tag : tags) {
            this.removeOptionalTag(tag);
        }

        return this;
    }

    private Function<T, ResourceKey<T>> keyExtractor() {
        Objects.requireNonNull(this.keyExtractor, "key extractor is null");
        return this.keyExtractor;
    }

    public abstract List<String> asStringList();

    /**
     * Do not use the vanilla method, there is an issue with the ModernFix mod overwriting it.
     *
     * @see TagEntry#elementOrTag()
     */
    protected final String elementOrTag(TagEntry entry) {
        return new ExtraCodecs.TagOrElementLocation(entry.id, entry.tag).toString();
    }
}
