package fuzs.puzzleslib.api.event.v1.server;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.Objects;
import java.util.function.Consumer;

@FunctionalInterface
public interface LootTableLoadCallback {
    EventInvoker<LootTableLoadCallback> EVENT = EventInvoker.lookup(LootTableLoadCallback.class);

    /**
     * Runs for every loot table upon loading, allows for modifying the loot table.
     *
     * @param resourceLocation the loot table id
     * @param lootTable        the loot table builder instance
     * @param registries       the registry access
     */
    void onLootTableLoad(ResourceLocation resourceLocation, LootTable.Builder lootTable, HolderLookup.Provider registries);

    /**
     * Allows for modifying each existing loot pool in a loot table.
     * <p>
     * Can only be used inside {@link LootTableLoadCallback}.
     *
     * @param lootTable        the loot table builder instance
     * @param lootPoolConsumer the consumer to apply to each loot pool inside
     */
    static void forEachPool(LootTable.Builder lootTable, Consumer<? super LootPool.Builder> lootPoolConsumer) {
        Objects.requireNonNull(lootTable, "loot table is null");
        Objects.requireNonNull(lootPoolConsumer, "loot pool consumer is null");
        ProxyImpl.get().forEachPool(lootTable, lootPoolConsumer);
    }
}
