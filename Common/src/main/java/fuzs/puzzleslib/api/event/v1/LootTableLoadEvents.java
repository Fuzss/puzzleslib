package fuzs.puzzleslib.api.event.v1;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.data.MutableValue;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;

import java.util.function.Consumer;
import java.util.function.IntPredicate;

public final class LootTableLoadEvents {
    public static final EventInvoker<Replace> REPLACE = EventInvoker.lookup(Replace.class);
    public static final EventInvoker<Modify> MODIFY = EventInvoker.lookup(Modify.class);

    private LootTableLoadEvents() {

    }

    @FunctionalInterface
    public interface Replace {

        /**
         * Allows for replacing built-in {@link LootTable}s on loading.
         * <p>To remove a loot table completely pass {@link LootTable#EMPTY} to <code>lootTable</code>.
         * <p>The event does <b>NOT</b> fire for data pack-provided loot tables.
         *
         * @param lootManager access to all loot tables
         * @param identifier  the loot table id
         * @param lootTable   the loot table that can be replaced
         */
        void onReplaceLootTable(LootTables lootManager, ResourceLocation identifier, MutableValue<LootTable> lootTable);
    }

    @FunctionalInterface
    public interface Modify {

        /**
         * Allows changing of {@link LootPool}s in a {@link LootTable}.
         * <p>The event does <b>NOT</b> fire for data pack-provided loot tables.
         *
         * @param lootManager access to all loot tables
         * @param identifier  the loot table id
         * @param addPool     add a {@link LootPool}
         * @param removePool  removes a pool at a given index, pools are indexed starting from 0;
         *                    returns if removing the pool was successful, which is the case when a pool at <code>index</code> has been found
         */
        void onModifyLootTable(LootTables lootManager, ResourceLocation identifier, Consumer<LootPool> addPool, IntPredicate removePool);
    }
}
