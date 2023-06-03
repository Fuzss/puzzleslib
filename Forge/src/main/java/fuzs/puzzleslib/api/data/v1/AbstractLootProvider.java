package fuzs.puzzleslib.api.data.v1;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.loot.EntityLoot;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class AbstractLootProvider {

    public static LootTableProvider createProvider(DataGenerator packOutput, Consumer<BiConsumer<ResourceLocation, LootTable.Builder>> provider, LootContextParamSet paramSet) {
        return new LootTableProvider(packOutput) {

            @Override
            protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables() {
                return ImmutableList.of(Pair.of(() -> provider, paramSet));
            }

            @Override
            protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationtracker) {

            }
        };
    }

    public static abstract class Blocks extends BlockLoot implements DataProvider {
        private final LootTableProvider provider;
        private final String modId;

        public Blocks(DataGenerator packOutput, String modId) {
            this.provider = createProvider(packOutput, this, LootContextParamSets.BLOCK);
            this.modId = modId;
        }

        @Override
        public final void run(HashCache output) {
            this.provider.run(output);
        }

        @Override
        public String getName() {
            return "Block Loot Tables";
        }

        @Override
        public abstract void addTables();

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return ForgeRegistries.BLOCKS.getEntries().stream()
                    .filter(entry -> entry.getKey().location().getNamespace().equals(this.modId))
                    .map(Map.Entry::getValue)
                    .collect(Collectors.toSet());
        }
    }

    public static abstract class EntityTypes extends EntityLoot implements DataProvider {
        private final LootTableProvider provider;
        private final String modId;

        public EntityTypes(DataGenerator packOutput, String modId) {
            this.provider = createProvider(packOutput, this, LootContextParamSets.ENTITY);
            this.modId = modId;
        }
        @Override
        public final void run(HashCache output) {
            this.provider.run(output);
        }

        @Override
        public String getName() {
            return "Entity Type Loot Tables";
        }

        @Override
        public abstract void addTables();

        @Override
        protected Iterable<EntityType<?>> getKnownEntities() {
            return ForgeRegistries.ENTITIES.getEntries().stream()
                    .filter(entry -> entry.getKey().location().getNamespace().equals(this.modId))
                    .map(Map.Entry::getValue).collect(Collectors.toSet());
        }

        @Override
        protected boolean isNonLiving(EntityType<?> entityType) {
            return false;
        }
    }

    public static abstract class Simple extends LootTableProvider implements DataProvider, Consumer<BiConsumer<ResourceLocation, LootTable.Builder>> {
        private final Map<ResourceLocation, LootTable.Builder> values = Maps.newHashMap();
        private final List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> provider;

        public Simple(DataGenerator packOutput, String name, LootContextParamSet paramSet) {
            super(packOutput);
            this.provider = ImmutableList.of(Pair.of(() -> this, paramSet));
        }

        @Override
        public String getName() {
            return "Loot Tables";
        }

        @Override
        protected void validate(Map<ResourceLocation, LootTable> tables, ValidationContext context) {

        }

        @Override
        protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables() {
            return this.provider;
        }

        @Override
        public void accept(BiConsumer<ResourceLocation, LootTable.Builder> consumer) {
            this.generate();
            this.values.forEach(consumer);
        }

        protected void add(ResourceLocation table, LootTable.Builder builder) {
            this.values.put(table, builder);
        }

        public abstract void generate();
    }
}
