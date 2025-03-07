package fuzs.puzzleslib.impl.data;

import fuzs.puzzleslib.api.data.v2.tags.AbstractTagAppender;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagEntry;
import net.minecraft.util.ExtraCodecs;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class ForgeTagAppender<T> extends AbstractTagAppender<T> {

    public ForgeTagAppender(TagBuilder tagBuilder, @Nullable Function<T, ResourceKey<T>> keyExtractor) {
        super(tagBuilder, keyExtractor);
    }

    @Override
    public AbstractTagAppender<T> setReplace(boolean replace) {
        this.tagBuilder.replace(replace);
        return this;
    }

    @Override
    public AbstractTagAppender<T> remove(ResourceLocation resourceLocation) {
        this.tagBuilder.removeElement(resourceLocation, "minecraft");
        return this;
    }

    @Override
    public AbstractTagAppender<T> removeTag(ResourceLocation resourceLocation) {
        this.tagBuilder.removeTag(resourceLocation, "minecraft");
        return this;
    }

    @Override
    public List<String> asStringList() {
        List<String> list = new ArrayList<>();
        for (TagEntry tagEntry : this.tagBuilder.build()) {
            list.add(new ExtraCodecs.TagOrElementLocation(tagEntry.getId(), tagEntry.isTag()).toString());
        }
        for (TagEntry tagEntry : this.tagBuilder.getRemoveEntries().toList()) {
            list.add("!" + new ExtraCodecs.TagOrElementLocation(tagEntry.getId(), tagEntry.isTag()));
        }
        return list;
    }
}
