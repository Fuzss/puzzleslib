package fuzs.puzzleslib.impl.core.contexts;

import fuzs.puzzleslib.api.core.v1.contexts.LootTablesContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;

public final class LootTablesModifyContext extends LootTablesContext.Modify {
    private final LootTable.Builder tableBuilder;

    public LootTablesModifyContext(LootTables lootManager, ResourceLocation id, LootTable.Builder tableBuilder) {
        super(lootManager, id);
        this.tableBuilder = tableBuilder;
    }

    @Override
    public void addLootPool(LootPool pool) {
        this.tableBuilder.pool(pool);
    }

    @Override
    public boolean removeLootPool(int index) {
        MutableInt counter = new MutableInt();
        MutableBoolean result = new MutableBoolean();
        this.tableBuilder.modifyPools(builder -> {
            if (index == counter.getAndIncrement()) {
                // there is no way in Fabric Api to remove loot pools, but this seems to work
                builder.setRolls(ConstantValue.exactly(0.0F));
                builder.setBonusRolls(ConstantValue.exactly(0.0F));
                result.setTrue();
            }
        });
        return result.booleanValue();
    }
}
