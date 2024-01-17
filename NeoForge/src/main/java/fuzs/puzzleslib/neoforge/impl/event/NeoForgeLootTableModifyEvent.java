package fuzs.puzzleslib.neoforge.impl.event;

import fuzs.puzzleslib.neoforge.mixin.accessor.LootTableNeoForgeAccessor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootDataManager;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.bus.api.Event;

import java.util.List;

/**
 * A custom event very similar to Forge's {@link net.neoforged.neoforge.event.LootTableLoadEvent}.
 * <p>Opposed to the Forge event, as this event is fired directly within {@link LootDataManager} it is able to include the relevant instance.
 * <p>The event also makes additions and removals in regard to the current {@link LootTable} more convenient, as Forge has removed the built-in methods in 1.20.
 * <p>And finally this event purposefully fires for all loot tables, even when loaded from a data pack, to allow for their modification.
 * Normally, Forge itself doesn't provide a way to modify custom loot tables, which is necessary in some scenarios though and
 * should rather be optional in the mod's config instead of not having the option at all.
 */
public class NeoForgeLootTableModifyEvent extends Event {
    private final LootDataManager lootDataManager;
    private final ResourceLocation identifier;
    private final LootTable lootTable;

    public NeoForgeLootTableModifyEvent(LootDataManager lootDataManager, ResourceLocation identifier, LootTable lootTable) {
        this.lootDataManager = lootDataManager;
        this.identifier = identifier;
        this.lootTable = lootTable;
    }

    public LootDataManager getLootDataManager() {
        return this.lootDataManager;
    }

    public ResourceLocation getIdentifier() {
        return this.identifier;
    }

    public void addPool(LootPool pool) {
        ((LootTableNeoForgeAccessor) this.lootTable).puzzleslib$getPools().add(pool);
    }

    public boolean removePool(int index) {
        List<LootPool> pools = ((LootTableNeoForgeAccessor) this.lootTable).puzzleslib$getPools();
        if (index >= 0 && index < pools.size()) {
            pools.remove(index);
            return true;
        }
        return false;
    }
}
