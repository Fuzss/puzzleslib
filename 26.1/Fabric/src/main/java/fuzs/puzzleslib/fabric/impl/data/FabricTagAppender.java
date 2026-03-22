package fuzs.puzzleslib.fabric.impl.data;

import fuzs.puzzleslib.api.data.v2.tags.AbstractTagAppender;
import net.fabricmc.fabric.impl.datagen.FabricTagBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagEntry;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class FabricTagAppender<T> extends AbstractTagAppender<T> {
    /**
     * Only fully supported on NeoForge.
     */
    private final List<TagEntry> removeEntries = new ArrayList<>();

    public FabricTagAppender(TagBuilder tagBuilder, @Nullable Function<T, ResourceKey<T>> keyExtractor) {
        super(tagBuilder, keyExtractor);
    }

    @Override
    public AbstractTagAppender<T> setReplace(boolean replace) {
        ((FabricTagBuilder) this.tagBuilder).fabric_setReplace(replace);
        return this;
    }

    @Override
    public AbstractTagAppender<T> remove(Identifier identifier) {
        this.removeEntries.add(TagEntry.element(identifier));
        return this;
    }

    @Override
    public AbstractTagAppender<T> removeOptional(Identifier identifier) {
        this.removeEntries.add(TagEntry.optionalElement(identifier));
        return this;
    }

    @Override
    public AbstractTagAppender<T> removeTag(Identifier identifier) {
        this.removeEntries.add(TagEntry.tag(identifier));
        return this;
    }

    @Override
    public AbstractTagAppender<T> removeOptionalTag(Identifier identifier) {
        this.removeEntries.add(TagEntry.optionalTag(identifier));
        return this;
    }

    @Override
    public List<String> asStringList() {
        List<String> list = new ArrayList<>();
        for (TagEntry tagEntry : this.tagBuilder.build()) {
            list.add(tagEntry.toString());
        }

        for (TagEntry tagEntry : this.removeEntries) {
            list.add("!" + tagEntry);
        }

        return list;
    }
}
