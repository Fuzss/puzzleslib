package fuzs.puzzleslib.neoforge.impl.event;

import net.minecraft.util.Util;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

import java.util.ArrayList;

public final class ForwardingLootPoolBuilder extends LootPool.Builder {
    private final LootPool lootPool;

    public ForwardingLootPoolBuilder(LootPool lootPool) {
        this.lootPool = lootPool;
    }

    @Override
    public LootPool.Builder setRolls(NumberProvider rolls) {
        this.lootPool.rolls = rolls;
        return this;
    }

    @Override
    public LootPool.Builder setBonusRolls(NumberProvider bonusRolls) {
        this.lootPool.bonusRolls = bonusRolls;
        return this;
    }

    @Override
    public LootPool.Builder add(LootPoolEntryContainer.Builder<?> entriesBuilder) {
        if (!(this.lootPool.entries instanceof ArrayList<LootPoolEntryContainer>)) {
            this.lootPool.entries = new ArrayList<>(this.lootPool.entries);
        }
        this.lootPool.entries.add(entriesBuilder.build());
        return this;
    }

    @Override
    public LootPool.Builder when(LootItemCondition.Builder conditionBuilder) {
        if (!(this.lootPool.conditions instanceof ArrayList<LootItemCondition>)) {
            this.lootPool.conditions = new ArrayList<>(this.lootPool.conditions);
        }
        this.lootPool.conditions.add(conditionBuilder.build());
        this.lootPool.compositeCondition = Util.allOf(this.lootPool.conditions);
        return this;
    }

    @Override
    public LootPool.Builder apply(LootItemFunction.Builder functionBuilder) {
        if (!(this.lootPool.functions instanceof ArrayList<LootItemFunction>)) {
            this.lootPool.functions = new ArrayList<>(this.lootPool.functions);
        }

        this.lootPool.functions.add(functionBuilder.build());
        this.lootPool.compositeFunction = LootItemFunctions.compose(this.lootPool.functions);
        return this;
    }

    @Override
    public LootPool build() {
        return this.lootPool;
    }
}
