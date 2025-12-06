package fuzs.puzzleslib.neoforge.impl.event;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;

import java.util.ArrayList;
import java.util.Optional;

public final class ForwardingLootTableBuilder extends LootTable.Builder {
    private final LootTable lootTable;

    public ForwardingLootTableBuilder(LootTable lootTable) {
        this.lootTable = lootTable;
    }

    @Override
    public LootTable.Builder withPool(LootPool.Builder lootPool) {
        if (!(this.lootTable.pools instanceof ArrayList<LootPool>)) {
            this.lootTable.pools = new ArrayList<>(this.lootTable.pools);
        }
        this.lootTable.pools.add(lootPool.build());
        return this;
    }

    @Override
    public LootTable.Builder setParamSet(LootContextParamSet paramSet) {
        this.lootTable.paramSet = paramSet;
        return this;
    }

    @Override
    public LootTable.Builder setRandomSequence(ResourceLocation randomSequence) {
        this.lootTable.randomSequence = Optional.of(randomSequence);
        return this;
    }

    @Override
    public LootTable.Builder apply(LootItemFunction.Builder functionBuilder) {
        if (!(this.lootTable.functions instanceof ArrayList<LootItemFunction>)) {
            this.lootTable.functions = new ArrayList<>(this.lootTable.functions);
        }
        this.lootTable.functions.add(functionBuilder.build());
        return this;
    }

    @Override
    public LootTable build() {
        return this.lootTable;
    }
}
