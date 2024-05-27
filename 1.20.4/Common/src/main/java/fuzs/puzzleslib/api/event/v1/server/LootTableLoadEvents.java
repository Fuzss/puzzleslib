package fuzs.puzzleslib.api.event.v1.server;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.data.MutableValue;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootDataManager;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.IntPredicate;

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
         * @param identifier the loot table id
         * @param table      the loot table that can be replaced
         */
        void onReplaceLootTable(ResourceLocation identifier, MutableValue<LootTable> table);
    }

    @FunctionalInterface
    public interface Modify {

        /**
         * Allows changing of {@link LootPool LootPools} in a {@link LootTable}.
         * <p>
         * TODO remove LootDataManager parameter as we do not have access on Forge / NeoForge and it's not useful anyway
         *
         * @param lootDataManager access to all loot tables
         * @param identifier      the loot table id
         * @param addPool         add a {@link LootPool}
         * @param removePool      removes a pool at a given index, pools are indexed starting from 0, succeeds if the
         *                        pool was removed at the given index, also note that indices are consistent and will
         *                        not update when other indices are removed
         */
        void onModifyLootTable(@Nullable LootDataManager lootDataManager, ResourceLocation identifier, Consumer<LootPool> addPool, IntPredicate removePool);
    }
}
