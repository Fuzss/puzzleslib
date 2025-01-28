package fuzs.puzzleslib.api.event.v1.server;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.data.MutableValue;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.function.Consumer;
import java.util.function.IntPredicate;

@Deprecated(forRemoval = true)
public final class LootTableLoadEvents {
    public static final EventInvoker<Replace> REPLACE = EventInvoker.lookup(Replace.class);
    public static final EventInvoker<Modify> MODIFY = EventInvoker.lookup(Modify.class);

    private LootTableLoadEvents() {
        // NO-OP
    }

    @FunctionalInterface
    public interface Replace {

        /**
         * Allows for replacing {@link LootTable LootTables} on loading.
         * <p>To remove a loot table completely pass in {@link LootTable#EMPTY}.
         *
         * @param resourceLocation the loot table id
         * @param lootTable        the loot table that can be replaced
         */
        void onReplaceLootTable(ResourceLocation resourceLocation, MutableValue<LootTable> lootTable);
    }

    @FunctionalInterface
    public interface Modify {

        /**
         * Allows changing of {@link LootPool LootPools} in a {@link LootTable}.
         *
         * @param resourceLocation the loot table id
         * @param addLootPool      add a {@link LootPool}
         * @param removeLootPool   removes a pool at a given index, pools are indexed starting from 0, succeeds if the
         *                         pool was removed at the given index, also note that indices are consistent and will
         *                         not update when other indices are removed
         */
        void onModifyLootTable(ResourceLocation resourceLocation, Consumer<LootPool> addLootPool, IntPredicate removeLootPool);
    }
}
