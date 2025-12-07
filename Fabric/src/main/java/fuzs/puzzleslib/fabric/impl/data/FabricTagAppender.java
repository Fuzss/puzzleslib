package fuzs.puzzleslib.fabric.impl.data;

import fuzs.puzzleslib.api.data.v2.tags.AbstractTagAppender;
import net.fabricmc.fabric.impl.datagen.FabricTagBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagEntry;
import org.jetbrains.annotations.Nullable;

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

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public AbstractTagAppender<T> setReplace(boolean replace) {
        ((FabricTagBuilder) this.tagBuilder).fabric_setReplace(replace);
        return this;
    }

    @Override
    public AbstractTagAppender<T> remove(ResourceLocation resourceLocation) {
        this.removeEntries.add(TagEntry.element(resourceLocation));
        return this;
    }

    @Override
    public AbstractTagAppender<T> removeOptional(ResourceLocation resourceLocation) {
        this.removeEntries.add(TagEntry.optionalElement(resourceLocation));
        return this;
    }

    @Override
    public AbstractTagAppender<T> removeTag(ResourceLocation resourceLocation) {
        this.removeEntries.add(TagEntry.tag(resourceLocation));
        return this;
    }

    @Override
    public AbstractTagAppender<T> removeOptionalTag(ResourceLocation resourceLocation) {
        this.removeEntries.add(TagEntry.optionalTag(resourceLocation));
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
