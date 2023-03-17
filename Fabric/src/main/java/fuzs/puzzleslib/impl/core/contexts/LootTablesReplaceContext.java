package fuzs.puzzleslib.impl.core.contexts;

import fuzs.puzzleslib.api.core.v1.contexts.LootTablesContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import org.apache.commons.lang3.mutable.MutableObject;

public final class LootTablesReplaceContext extends LootTablesContext.Replace {
    private final MutableObject<LootTable> replacement;

    public LootTablesReplaceContext(LootTables lootManager, ResourceLocation id, LootTable original, MutableObject<LootTable> replacement) {
        super(lootManager, id, original);
        this.replacement = replacement;
    }

    @Override
    public void setLootTable(LootTable table) {
        this.replacement.setValue(table);
    }
}
