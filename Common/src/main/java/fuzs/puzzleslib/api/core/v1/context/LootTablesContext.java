package fuzs.puzzleslib.api.core.v1.context;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;

/**
 * basic context for loot tables
 */
public abstract class LootTablesContext {
    /**
     * access to all loot tables
     */
    private final LootTables lootManager;
    /**
     * the loot table id
     */
    private final ResourceLocation id;

    /**
     * @param lootManager access to all loot tables
     * @param id          the loot table id
     */
    protected LootTablesContext(LootTables lootManager, ResourceLocation id) {
        this.lootManager = lootManager;
        this.id = id;
    }

    /**
     * @return access to all loot tables
     */
    public final LootTables getLootManager() {
        return this.lootManager;
    }

    /**
     * @return the loot table id
     */
    public final ResourceLocation getId() {
        return this.id;
    }

    /**
     * allows for replacing built-in {@link LootTable}s on loading
     */
    public abstract static class Replace extends LootTablesContext {
        /**
         * the original loot table that will be replaced
         */
        private final LootTable original;

        /**
         * @param lootManager access to all loot tables
         * @param id          the loot table id
         * @param original    the original loot table that will be replaced
         */
        public Replace(LootTables lootManager, ResourceLocation id, LootTable original) {
            super(lootManager, id);
            this.original = original;
        }

        /**
         * @return the original loot table that will be replaced
         */
        public LootTable getLootTable() {
            return this.original;
        }

        /**
         * @param table replacement for <code>original</code>
         */
        public abstract void setLootTable(LootTable table);
    }

    /**
     * allows changing of {@link LootPool}s in a {@link LootTable}
     */
    public abstract static class Modify extends LootTablesContext {

        /**
         * @param lootManager access to all loot tables
         * @param id          the loot table id
         */
        public Modify(LootTables lootManager, ResourceLocation id) {
            super(lootManager, id);
        }

        /**
         * @param pool add a {@link LootPool}
         */
        public abstract void addLootPool(LootPool pool);

        /**
         * @param index pool to remove at index, pools are indexed starting from 0
         * @return was removing the pool successful (has a pool at <code>index</code> been found)
         */
        public abstract boolean removeLootPool(int index);
    }
}
