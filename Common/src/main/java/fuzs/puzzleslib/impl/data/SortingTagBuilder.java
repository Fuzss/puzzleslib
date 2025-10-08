package fuzs.puzzleslib.impl.data;

import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagEntry;

import java.util.Comparator;
import java.util.List;

/**
 * A {@link TagBuilder} that sorts its entries upon building to yield consistent results when constructed from
 * dynamically added data pack registry entries.
 */
public class SortingTagBuilder extends TagBuilder {
    @Override
    public List<TagEntry> build() {
        // sorting by id only is enough, there are no duplicates allowed
        return super.build().stream().sorted(Comparator.comparing((TagEntry tagEntry) -> tagEntry.id)).toList();
    }
}
