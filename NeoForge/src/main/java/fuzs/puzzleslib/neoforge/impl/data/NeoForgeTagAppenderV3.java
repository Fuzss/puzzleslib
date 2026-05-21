package fuzs.puzzleslib.neoforge.impl.data;

import fuzs.puzzleslib.api.data.v3.tags.AbstractTagAppender;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class NeoForgeTagAppenderV3<T> extends AbstractTagAppender<T> {

    public NeoForgeTagAppenderV3(TagBuilder tagBuilder, @Nullable Function<T, ResourceKey<T>> keyExtractor) {
        super(tagBuilder, keyExtractor);
    }

    @Override
    public AbstractTagAppender<T> remove(ResourceLocation id) {
        this.tagBuilder.remove(TagEntry.element(id));
        return this;
    }

    @Override
    public AbstractTagAppender<T> removeOptional(ResourceLocation id) {
        this.tagBuilder.remove(TagEntry.optionalElement(id));
        return this;
    }

    @Override
    public AbstractTagAppender<T> removeTag(ResourceLocation id) {
        this.tagBuilder.remove(TagEntry.tag(id));
        return this;
    }

    @Override
    public AbstractTagAppender<T> removeOptionalTag(ResourceLocation id) {
        this.tagBuilder.remove(TagEntry.optionalTag(id));
        return this;
    }

    @Override
    public List<String> asStringList() {
        List<String> list = new ArrayList<>();
        for (TagEntry tagEntry : this.tagBuilder.build()) {
            list.add(this.elementOrTag(tagEntry));
        }

        for (TagEntry tagEntry : this.tagBuilder.getRemoveEntries().toList()) {
            list.add("!" + this.elementOrTag(tagEntry));
        }

        return list;
    }

    @Override
    public AbstractTagAppender<T> add(TagEntry tagEntry) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AbstractTagAppender<T> replace(boolean replace) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AbstractTagAppender<T> remove(TagKey<T> tag) {
        throw new UnsupportedOperationException();
    }
}
