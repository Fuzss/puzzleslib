package fuzs.puzzleslib.fabric.impl.data;

import fuzs.puzzleslib.api.data.v3.tags.AbstractTagAppender;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagEntry;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class FabricTagAppenderV3<T> extends AbstractTagAppender<T> {
    /**
     * Only fully supported on NeoForge.
     */
    private final List<TagEntry> removeEntries = new ArrayList<>();

    public FabricTagAppenderV3(TagBuilder tagBuilder, @Nullable Function<T, ResourceKey<T>> keyExtractor) {
        super(tagBuilder, keyExtractor);
    }

    @Override
    public AbstractTagAppender<T> remove(ResourceLocation identifier) {
        this.removeEntries.add(TagEntry.element(identifier));
        return this;
    }

    @Override
    public AbstractTagAppender<T> removeOptional(ResourceLocation identifier) {
        this.removeEntries.add(TagEntry.optionalElement(identifier));
        return this;
    }

    @Override
    public AbstractTagAppender<T> removeTag(ResourceLocation identifier) {
        this.removeEntries.add(TagEntry.tag(identifier));
        return this;
    }

    @Override
    public AbstractTagAppender<T> removeOptionalTag(ResourceLocation identifier) {
        this.removeEntries.add(TagEntry.optionalTag(identifier));
        return this;
    }

    @Override
    public List<String> asStringList() {
        List<String> list = new ArrayList<>();
        for (TagEntry tagEntry : this.tagBuilder.build()) {
            list.add(this.elementOrTag(tagEntry));
        }

        for (TagEntry tagEntry : this.removeEntries) {
            list.add("!" + this.elementOrTag(tagEntry));
        }

        return list;
    }
}
