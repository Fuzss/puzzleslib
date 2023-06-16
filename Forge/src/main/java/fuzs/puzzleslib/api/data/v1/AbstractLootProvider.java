package fuzs.puzzleslib.api.data.v1;

import com.google.common.collect.Maps;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class AbstractLootProvider {

    public static LootTableProvider createProvider(PackOutput packOutput, LootTableSubProvider provider, LootContextParamSet paramSet) {
        return new LootTableProvider(packOutput, Set.of(), List.of(new LootTableProvider.SubProviderEntry(() -> provider, paramSet)));
    }

    public static abstract class Blocks extends BlockLootSubProvider implements DataProvider {
        private final LootTableProvider provider;
        private final String modId;

        public Blocks(GatherDataEvent evt, String modId) {
            this(evt.getGenerator().getPackOutput(), modId);
        }

        public Blocks(PackOutput packOutput, String modId) {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags());
            this.provider = createProvider(packOutput, this, LootContextParamSets.BLOCK);
            this.modId = modId;
        }

        @Override
        public final CompletableFuture<?> run(CachedOutput output) {
            return this.provider.run(output);
        }

        @Override
        public String getName() {
            return "Block Loot Tables";
        }

        @Override
        public abstract void generate();

        protected void dropNothing(Block block) {
            this.add(block, noDrop());
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return ForgeRegistries.BLOCKS.getEntries().stream()
                    .filter(entry -> entry.getKey().location().getNamespace().equals(this.modId))
                    .map(Map.Entry::getValue)
                    .collect(Collectors.toSet());
        }
    }

    public static abstract class EntityTypes extends EntityLootSubProvider implements DataProvider {
        private final LootTableProvider provider;
        private final String modId;

        public EntityTypes(GatherDataEvent evt, String modId) {
            this(evt.getGenerator().getPackOutput(), modId);
        }

        public EntityTypes(PackOutput packOutput, String modId) {
            super(FeatureFlags.REGISTRY.allFlags());
            this.provider = createProvider(packOutput, this, LootContextParamSets.ENTITY);
            this.modId = modId;
        }
        @Override
        public final CompletableFuture<?> run(CachedOutput output) {
            return this.provider.run(output);
        }

        @Override
        public String getName() {
            return "Entity Type Loot Tables";
        }

        @Override
        public abstract void generate();

        @Override
        protected Stream<EntityType<?>> getKnownEntityTypes() {
            return ForgeRegistries.ENTITY_TYPES.getEntries().stream()
                    .filter(entry -> entry.getKey().location().getNamespace().equals(this.modId))
                    .map(Map.Entry::getValue);
        }

        @Override
        protected boolean canHaveLootTable(EntityType<?> pEntityType) {
            return true;
        }
    }

    public static abstract class Simple implements LootTableSubProvider, DataProvider {
        private final LootTableProvider provider;
        private final Map<ResourceLocation, LootTable.Builder> values = Maps.newHashMap();

        @Deprecated(forRemoval = true)
        public Simple(PackOutput packOutput, LootContextParamSet paramSet) {
            this(paramSet, packOutput, "");
        }

        public Simple(LootContextParamSet paramSet, GatherDataEvent evt, String modId) {
            this(paramSet, evt.getGenerator().getPackOutput(), modId);
        }

        public Simple(LootContextParamSet paramSet, PackOutput packOutput, String modId) {
            this.provider = createProvider(packOutput, this, paramSet);
        }
        @Override
        public final CompletableFuture<?> run(CachedOutput output) {
            return this.provider.run(output);
        }

        @Override
        public String getName() {
            // multiple data providers cannot have the same name, so just add this to be safe while staying recognizable
            return this.provider.getName() + "@" + Integer.toHexString(this.hashCode());
        }

        @Override
        public void generate(BiConsumer<ResourceLocation, LootTable.Builder> exporter) {
            this.generate();
            this.values.forEach(exporter);
        }

        protected void add(ResourceLocation table, LootTable.Builder builder) {
            this.values.put(table, builder);
        }

        public abstract void generate();
    }
}
