package fuzs.puzzleslib.fabric.impl.data;

import fuzs.puzzleslib.common.api.data.v2.tags.AbstractTagAppender;
import net.fabricmc.fabric.impl.datagen.TagBuilderHooks;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagEntry;

import java.util.ArrayList;
import java.util.List;

public final class FabricTagAppender<T> extends AbstractTagAppender<T> {

    public FabricTagAppender(TagBuilder tagBuilder) {
        super(tagBuilder);
    }

    @SuppressWarnings("UnstableApiUsage")
    private List<TagEntry> getRemoveEntries() {
        return ((TagBuilderHooks) this.tagBuilder).fabric_getRemove();
    }

    @Override
    public AbstractTagAppender<T> remove(Identifier id) {
        this.getRemoveEntries().add(TagEntry.element(id));
        return this;
    }

    @Override
    public AbstractTagAppender<T> removeOptional(Identifier id) {
        this.getRemoveEntries().add(TagEntry.optionalElement(id));
        return this;
    }

    @Override
    public AbstractTagAppender<T> removeTag(Identifier id) {
        this.getRemoveEntries().add(TagEntry.tag(id));
        return this;
    }

    @Override
    public AbstractTagAppender<T> removeOptionalTag(Identifier id) {
        this.getRemoveEntries().add(TagEntry.optionalTag(id));
        return this;
    }

    @Override
    public List<String> asStringList() {
        List<String> list = new ArrayList<>();
        for (TagEntry tagEntry : this.tagBuilder.build()) {
            list.add(this.elementOrTag(tagEntry));
        }

        for (TagEntry tagEntry : this.getRemoveEntries()) {
            list.add("!" + this.elementOrTag(tagEntry));
        }

        return list;
    }
}
