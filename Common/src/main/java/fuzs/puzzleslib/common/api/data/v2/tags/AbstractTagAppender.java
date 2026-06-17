package fuzs.puzzleslib.common.api.data.v2.tags;

import net.minecraft.core.Holder;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;

import java.util.List;

public abstract class AbstractTagAppender<T> implements TagAppender<T> {
    protected final TagBuilder tagBuilder;

    public AbstractTagAppender(TagBuilder tagBuilder) {
        this.tagBuilder = tagBuilder;
    }

    public AbstractTagAppender<T> add(Identifier id) {
        this.tagBuilder.addElement(id);
        return this;
    }

    public AbstractTagAppender<T> add(Identifier... ids) {
        for (Identifier id : ids) {
            this.add(id);
        }

        return this;
    }

    @Override
    public AbstractTagAppender<T> add(ResourceKey<T> key) {
        return this.add(key.identifier());
    }

    @Override
    @SafeVarargs
    public final AbstractTagAppender<T> add(ResourceKey<T>... keys) {
        for (ResourceKey<T> key : keys) {
            this.add(key);
        }

        return this;
    }

    public AbstractTagAppender<T> add(Holder.Reference<? extends T> holder) {
        return this.add((ResourceKey<T>) holder.key());
    }

    @SafeVarargs
    public final AbstractTagAppender<T> add(Holder.Reference<? extends T>... holders) {
        for (Holder.Reference<? extends T> holder : holders) {
            this.add(holder);
        }

        return this;
    }

    public AbstractTagAppender<T> addOptional(String id) {
        return this.addOptional(Identifier.parse(id));
    }

    public AbstractTagAppender<T> addOptional(String... ids) {
        for (String id : ids) {
            this.addOptional(id);
        }

        return this;
    }

    public AbstractTagAppender<T> addOptional(Identifier id) {
        this.tagBuilder.addOptionalElement(id);
        return this;
    }

    public AbstractTagAppender<T> addOptional(Identifier... ids) {
        for (Identifier id : ids) {
            this.add(id);
        }

        return this;
    }

    @Override
    public AbstractTagAppender<T> addOptional(ResourceKey<T> key) {
        return this.addOptional(key.identifier());
    }

    @SafeVarargs
    public final AbstractTagAppender<T> addOptional(ResourceKey<T>... keys) {
        for (ResourceKey<T> key : keys) {
            this.addOptional(key);
        }

        return this;
    }

    public AbstractTagAppender<T> addOptional(Holder.Reference<? extends T> holder) {
        return this.addOptional((ResourceKey<T>) holder.key());
    }

    @SafeVarargs
    public final AbstractTagAppender<T> addOptional(Holder.Reference<? extends T>... holders) {
        for (Holder.Reference<? extends T> holder : holders) {
            this.addOptional(holder);
        }

        return this;
    }

    public AbstractTagAppender<T> addTag(Identifier id) {
        this.tagBuilder.addTag(id);
        return this;
    }

    public AbstractTagAppender<T> addTag(Identifier... ids) {
        for (Identifier id : ids) {
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
        return this.addOptionalTag(Identifier.parse(id));
    }

    public AbstractTagAppender<T> addOptionalTag(String... ids) {
        for (String id : ids) {
            this.addOptionalTag(id);
        }

        return this;
    }

    public AbstractTagAppender<T> addOptionalTag(Identifier id) {
        this.tagBuilder.addOptionalTag(id);
        return this;
    }

    public AbstractTagAppender<T> addOptionalTag(Identifier... ids) {
        for (Identifier id : ids) {
            this.addOptionalTag(id);
        }

        return this;
    }

    @Override
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

    public abstract AbstractTagAppender<T> remove(Identifier id);

    public AbstractTagAppender<T> remove(Identifier... ids) {
        for (Identifier id : ids) {
            this.remove(id);
        }

        return this;
    }

    public AbstractTagAppender<T> remove(ResourceKey<T> key) {
        return this.remove(key.identifier());
    }

    @SafeVarargs
    public final AbstractTagAppender<T> remove(ResourceKey<T>... keys) {
        for (ResourceKey<T> key : keys) {
            this.remove(key);
        }

        return this;
    }

    public AbstractTagAppender<T> remove(Holder.Reference<? extends T> holder) {
        return this.remove((ResourceKey<T>) holder.key());
    }

    @SafeVarargs
    public final AbstractTagAppender<T> remove(Holder.Reference<? extends T>... holders) {
        for (Holder.Reference<? extends T> holder : holders) {
            this.remove(holder);
        }

        return this;
    }

    public AbstractTagAppender<T> removeOptional(String id) {
        return this.removeOptional(Identifier.parse(id));
    }

    public AbstractTagAppender<T> removeOptional(String... ids) {
        for (String id : ids) {
            this.removeOptional(id);
        }

        return this;
    }

    public abstract AbstractTagAppender<T> removeOptional(Identifier id);

    public AbstractTagAppender<T> removeOptional(Identifier... ids) {
        for (Identifier id : ids) {
            this.removeOptional(id);
        }

        return this;
    }

    public AbstractTagAppender<T> removeOptional(ResourceKey<T> key) {
        return this.removeOptional(key.identifier());
    }

    @SafeVarargs
    public final AbstractTagAppender<T> removeOptional(ResourceKey<T>... keys) {
        for (ResourceKey<T> key : keys) {
            this.removeOptional(key);
        }

        return this;
    }

    public AbstractTagAppender<T> removeOptional(Holder.Reference<? extends T> holder) {
        return this.removeOptional((ResourceKey<T>) holder.key());
    }

    @SafeVarargs
    public final AbstractTagAppender<T> removeOptional(Holder.Reference<? extends T>... holders) {
        for (Holder.Reference<? extends T> holder : holders) {
            this.removeOptional(holder);
        }

        return this;
    }

    public abstract AbstractTagAppender<T> removeTag(Identifier id);

    public AbstractTagAppender<T> removeTag(Identifier... ids) {
        for (Identifier id : ids) {
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
        return this.removeOptionalTag(Identifier.parse(id));
    }

    public AbstractTagAppender<T> removeOptionalTag(String... ids) {
        for (String id : ids) {
            this.removeOptionalTag(id);
        }

        return this;
    }

    public abstract AbstractTagAppender<T> removeOptionalTag(Identifier id);

    public AbstractTagAppender<T> removeOptionalTag(Identifier... ids) {
        for (Identifier id : ids) {
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
