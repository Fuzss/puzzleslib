package fuzs.puzzleslib.api.data.v2;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.RandomSequence;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public final class AbstractLootProviderV2 {

    public static LootTableProvider createProvider(PackOutput packOutput, LootTableSubProvider provider, LootContextParamSet paramSet) {
        return new LootTableProvider(packOutput, Set.of(), List.of(new LootTableProvider.SubProviderEntry(() -> provider, paramSet)));
    }

    public static abstract class Blocks extends BlockLootSubProvider implements LootTableDataProvider {
        private final Set<ResourceLocation> skipped = Sets.newHashSet();
        private final PackOutput.PathProvider pathProvider;
        private final String modId;

        public Blocks(DataProviderContext context) {
            this(context.getModId(), context.getPackOutput());
        }

        public Blocks(String modId, PackOutput packOutput) {
            super(Collections.emptySet(), FeatureFlags.REGISTRY.allFlags());
            this.pathProvider = packOutput.createPathProvider(PackOutput.Target.DATA_PACK, "loot_tables");
            this.modId = modId;
        }

        @Override
        public String getName() {
            return "Block Loot Tables";
        }

        @Override
        public final void generate() {
            this.addLootTables();
        }

        public abstract void addLootTables();

        @Override
        public void generate(BiConsumer<ResourceLocation, LootTable.Builder> consumer) {
            this.generate();
            Set<ResourceLocation> set = Sets.newHashSet();
            for (Map.Entry<ResourceKey<Block>, Block> entry : BuiltInRegistries.BLOCK.entrySet()) {
                ResourceKey<Block> resourceKey = entry.getKey();
                Block block = entry.getValue();
                if (resourceKey.location().getNamespace().equals(this.modId) && block.isEnabled(this.enabledFeatures)) {
                    ResourceLocation resourceLocation = block.getLootTable();
                    if (resourceLocation != BuiltInLootTables.EMPTY && resourceLocation.getNamespace().equals(this.modId) && set.add(resourceLocation)) {
                        LootTable.Builder builder = this.map.remove(resourceLocation);
                        if (builder == null) {
                            throw new IllegalStateException("Missing loot table '%s' for '%s'".formatted(resourceLocation, resourceKey.location()));
                        } else {
                            consumer.accept(resourceLocation, builder);
                        }
                    }
                }
            }
            if (!this.map.isEmpty()) {
                throw new IllegalStateException("Created block loot tables for non-blocks: " + this.map.keySet());
            }
        }

        @Override
        public PackOutput.PathProvider pathProvider() {
            return this.pathProvider;
        }

        @Override
        public LootContextParamSet paramSet() {
            return LootContextParamSets.BLOCK;
        }

        @Override
        public void skipValidation(ResourceLocation resourceLocation) {
            this.skipped.add(resourceLocation);
        }

        public void skipValidation(Block block) {
            this.skipValidation(BuiltInRegistries.BLOCK.getKey(block));
        }

        @Override
        public void validate(ResourceLocation resourceLocation, LootTable lootTable, ValidationContext validationContext) {
            if (!this.skipped.contains(resourceLocation)) {
                LootTableDataProvider.super.validate(resourceLocation, lootTable, validationContext);
            }
        }

        protected void dropNothing(Block block) {
            this.add(block, noDrop());
        }
    }

    public abstract static class EntityTypes extends EntityLootSubProvider implements LootTableDataProvider {
        private final Set<ResourceLocation> skipped = Sets.newHashSet();
        private final PackOutput.PathProvider pathProvider;
        private final String modId;

        public EntityTypes(DataProviderContext context) {
            this(context.getModId(), context.getPackOutput());
        }

        public EntityTypes(String modId, PackOutput packOutput) {
            super(FeatureFlags.REGISTRY.allFlags());
            this.pathProvider = packOutput.createPathProvider(PackOutput.Target.DATA_PACK, "loot_tables");
            this.modId = modId;
        }

        @Override
        public String getName() {
            return "Entity Type Loot Tables";
        }

        @Override
        public final void generate() {
            this.addLootTables();
        }

        public abstract void addLootTables();

        @Override
        public void generate(BiConsumer<ResourceLocation, LootTable.Builder> consumer) {
            this.generate();
            Set<ResourceLocation> set = Sets.newHashSet();
            for (Map.Entry<ResourceKey<EntityType<?>>, EntityType<?>> entry : BuiltInRegistries.ENTITY_TYPE.entrySet()) {
                ResourceKey<EntityType<?>> resourceKey = entry.getKey();
                EntityType<?> entityType = entry.getValue();
                if (resourceKey.location().getNamespace().equals(this.modId)) {
                    Map<ResourceLocation, LootTable.Builder> map = this.map.remove(entityType);
                    if (this.canHaveLootTable(entityType)) {
                        ResourceLocation resourceLocation = entityType.getDefaultLootTable();
                        if (!resourceLocation.equals(BuiltInLootTables.EMPTY) && (map == null || !map.containsKey(resourceLocation))) {
                            throw new IllegalStateException(String.format(Locale.ROOT, "Missing loot table '%s' for '%s'", resourceLocation, resourceKey.location()));
                        }
                        if (map != null) {
                            map.forEach((resourceLocationx, builder) -> {
                                if (!set.add(resourceLocationx)) {
                                    throw new IllegalStateException(String.format(Locale.ROOT, "Duplicate loot table '%s' for '%s'", resourceLocationx, resourceKey.location()));
                                } else {
                                    consumer.accept(resourceLocationx, builder);
                                }
                            });
                        }
                    } else if (map != null) {
                        throw new IllegalStateException(String.format(Locale.ROOT, "Weird loot table(s) '%s' for '%s', not a LivingEntity so should not have loot", map.keySet().stream().map(ResourceLocation::toString).collect(Collectors.joining(",")), resourceKey.location()));
                    }
                }
            }
            if (!this.map.isEmpty()) {
                throw new IllegalStateException("Created loot tables for entities not supported by data pack: " + this.map.keySet());
            }
        }

        @Override
        public void skipValidation(ResourceLocation resourceLocation) {
            this.skipped.add(resourceLocation);
        }

        @Override
        public void validate(ResourceLocation resourceLocation, LootTable lootTable, ValidationContext validationContext) {
            if (!this.skipped.contains(resourceLocation)) {
                LootTableDataProvider.super.validate(resourceLocation, lootTable, validationContext);
            }
        }

        @Override
        public PackOutput.PathProvider pathProvider() {
            return this.pathProvider;
        }

        @Override
        public LootContextParamSet paramSet() {
            return LootContextParamSets.ENTITY;
        }

        protected boolean canHaveLootTable(EntityType<?> entityType) {
            return entityType.getCategory() != MobCategory.MISC;
        }

        public void skipValidation(EntityType<?> entityType) {
            this.skipValidation(BuiltInRegistries.ENTITY_TYPE.getKey(entityType));
        }
    }

    public static abstract class Simple implements LootTableSubProvider, LootTableDataProvider {
        private final Map<ResourceLocation, LootTable.Builder> tables = Maps.newHashMap();
        private final Set<ResourceLocation> skipped = Sets.newHashSet();
        private final LootContextParamSet paramSet;
        private final PackOutput.PathProvider pathProvider;

        public Simple(LootContextParamSet paramSet, DataProviderContext context) {
            this(paramSet, context.getPackOutput());
        }

        public Simple(LootContextParamSet paramSet, PackOutput packOutput) {
            this.pathProvider = packOutput.createPathProvider(PackOutput.Target.DATA_PACK, "loot_tables");
            this.paramSet = paramSet;
        }

        @Override
        public String getName() {
            // multiple data providers cannot have the same name, so handle this like so
            return String.join(" ", StringUtils.splitByCharacterTypeCamelCase(this.getClass().getSimpleName()));
        }

        @Override
        public void generate(BiConsumer<ResourceLocation, LootTable.Builder> exporter) {
            this.addLootTables();
            this.tables.forEach(exporter);
        }

        @Override
        public PackOutput.PathProvider pathProvider() {
            return this.pathProvider;
        }

        @Override
        public LootContextParamSet paramSet() {
            return this.paramSet;
        }

        @Override
        public void skipValidation(ResourceLocation resourceLocation) {
            this.skipped.add(resourceLocation);
        }

        protected void add(ResourceLocation table, LootTable.Builder builder) {
            this.tables.put(table, builder);
        }

        public abstract void addLootTables();

        @Override
        public void validate(ResourceLocation resourceLocation, LootTable lootTable, ValidationContext validationContext) {
            if (!this.skipped.contains(resourceLocation)) {
                LootTableDataProvider.super.validate(resourceLocation, lootTable, validationContext);
            }
        }
    }

    public interface LootTableDataProvider extends DataProvider {

        void generate(BiConsumer<ResourceLocation, LootTable.Builder> exporter);

        PackOutput.PathProvider pathProvider();

        LootContextParamSet paramSet();

        void skipValidation(ResourceLocation resourceLocation);

        default CompletableFuture<?> run(CachedOutput output) {
            final Map<ResourceLocation, LootTable> tables = Maps.newHashMap();
            Map<RandomSupport.Seed128bit, ResourceLocation> seeds = new Object2ObjectOpenHashMap<>();
            this.generate((resourceLocation, builder) -> {
                ResourceLocation resourceLocation2 = seeds.put(RandomSequence.seedForKey(resourceLocation), resourceLocation);
                if (resourceLocation2 != null) {
                    LOGGER.error("Loot table random sequence seed collision on " + resourceLocation2 + " and " + resourceLocation);
                }

                builder.setRandomSequence(resourceLocation);
                if (tables.put(resourceLocation, builder.setParamSet(this.paramSet()).build()) != null) {
                    throw new IllegalStateException("Duplicate loot table " + resourceLocation);
                }
            });
            this.validate(tables);
            return CompletableFuture.allOf(tables.entrySet().stream().map((entry) -> {
                ResourceLocation resourceLocation = entry.getKey();
                LootTable lootTable = entry.getValue();
                Path path = this.pathProvider().json(resourceLocation);
                return DataProvider.saveStable(output, LootTable.CODEC, lootTable, path);
            }).toArray(CompletableFuture[]::new));
        }

        private void validate(Map<ResourceLocation, LootTable> tables) {
            ProblemReporter.Collector collector = new ProblemReporter.Collector();
            ValidationContext validationContext = new ValidationContext(collector, LootContextParamSets.ALL_PARAMS, new LootDataResolver() {
                @Nullable
                public <T> T getElement(LootDataId<T> lootDataId) {
                    return lootDataId.type() == LootDataType.TABLE ? (T) tables.get(lootDataId.location()) : null;
                }
            });

            tables.forEach((ResourceLocation resourceLocation, LootTable lootTable) -> {
                this.validate(resourceLocation, lootTable, validationContext);
            });
            Multimap<String, String> multimap = collector.get();
            if (!multimap.isEmpty()) {
                multimap.forEach((string, string2) -> {
                    LOGGER.warn("Found validation problem in {}: {}", string, string2);
                });
                throw new IllegalStateException("Failed to validate loot tables, see logs");
            }
        }

        @MustBeInvokedByOverriders
        default void validate(ResourceLocation resourceLocation, LootTable lootTable, ValidationContext validationContext) {
            lootTable.validate(validationContext.setParams(lootTable.getParamSet()).enterElement("{" +
                    resourceLocation + "}", new LootDataId<>(LootDataType.TABLE, resourceLocation)));
        }
    }
}
