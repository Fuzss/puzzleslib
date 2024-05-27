package fuzs.puzzleslib.api.data.v2;

import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.data.loot.LootTableSubProvider;
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
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class AbstractLootProviderV2 {

    private AbstractLootProviderV2() {
        // NO-OP
    }

    public static abstract class Blocks extends BlockLootSubProvider implements LootTableDataProvider {
        private final Set<ResourceLocation> skipValidation = Sets.newHashSet();
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
            Set<ResourceLocation> set = new HashSet<>();
            this.getRegistryEntries().forEach((Holder.Reference<Block> holder) -> {
                ResourceLocation lootTableLocation = holder.value().getLootTable();
                if (lootTableLocation != BuiltInLootTables.EMPTY && set.add(lootTableLocation)) {
                    LootTable.Builder builder = this.map.remove(lootTableLocation);
                    if (builder == null) {
                        throw new IllegalStateException("Missing loot table '%s' for '%s'".formatted(lootTableLocation,
                                holder.key().location()
                        ));
                    } else {
                        consumer.accept(lootTableLocation, builder);
                    }
                }
            });
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
        public boolean skipValidationFor(ResourceLocation resourceLocation) {
            return this.skipValidation.contains(resourceLocation);
        }

        public void skipValidation(ResourceLocation resourceLocation) {
            this.skipValidation.add(resourceLocation);
        }

        public void skipValidation(Block block) {
            this.skipValidation(block.getLootTable());
        }

        public void dropNothing(Block block) {
            this.add(block, noDrop());
        }

        public void dropNameable(Block block) {
            this.add(block, this::createNameableBlockEntityTable);
        }

        protected Stream<Holder.Reference<Block>> getRegistryEntries() {
            return BuiltInRegistries.BLOCK.holders()
                    .filter(holder -> holder.key().location().getNamespace().equals(this.modId));
        }
    }

    public abstract static class EntityTypes extends EntityLootSubProvider implements LootTableDataProvider {
        private final Set<ResourceLocation> skipValidation = Sets.newHashSet();
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
            Set<ResourceLocation> set = new HashSet<>();
            this.getRegistryEntries().forEach((Holder.Reference<EntityType<?>> holder) -> {
                EntityType<?> entityType = holder.value();
                Map<ResourceLocation, LootTable.Builder> map = this.map.remove(entityType);
                if (this.canHaveLootTable(entityType)) {
                    ResourceLocation lootTableLocation = entityType.getDefaultLootTable();
                    if (!lootTableLocation.equals(BuiltInLootTables.EMPTY) &&
                            (map == null || !map.containsKey(lootTableLocation))) {
                        throw new IllegalStateException(String.format(Locale.ROOT,
                                "Missing loot table '%s' for '%s'",
                                lootTableLocation,
                                holder.key().location()
                        ));
                    }
                    if (map != null) {
                        map.forEach((resourceLocation, builder) -> {
                            if (!set.add(resourceLocation)) {
                                throw new IllegalStateException(String.format(Locale.ROOT,
                                        "Duplicate loot table '%s' for '%s'",
                                        resourceLocation,
                                        holder.key().location()
                                ));
                            } else {
                                consumer.accept(resourceLocation, builder);
                            }
                        });
                    }
                } else if (map != null) {
                    throw new IllegalStateException(String.format(Locale.ROOT,
                            "Weird loot table(s) '%s' for '%s', not a LivingEntity so should not have loot",
                            map.keySet().stream().map(ResourceLocation::toString).collect(Collectors.joining(",")),
                            holder.key().location()
                    ));
                }
            });
            if (!this.map.isEmpty()) {
                throw new IllegalStateException(
                        "Created loot tables for entities not supported by data pack: " + this.map.keySet());
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

        @Override
        public boolean skipValidationFor(ResourceLocation resourceLocation) {
            return this.skipValidation.contains(resourceLocation);
        }

        public void skipValidation(ResourceLocation resourceLocation) {
            this.skipValidation.add(resourceLocation);
        }

        public void skipValidation(EntityType<?> entityType) {
            this.skipValidation(entityType.getDefaultLootTable());
        }

        protected boolean canHaveLootTable(EntityType<?> entityType) {
            return entityType.getCategory() != MobCategory.MISC;
        }

        protected Stream<Holder.Reference<EntityType<?>>> getRegistryEntries() {
            return BuiltInRegistries.ENTITY_TYPE.holders()
                    .filter(holder -> holder.key().location().getNamespace().equals(this.modId));
        }
    }

    public static abstract class Simple implements LootTableSubProvider, LootTableDataProvider {
        private final Map<ResourceLocation, LootTable.Builder> tables = new HashMap<>();
        private final Set<ResourceLocation> skipValidation = Sets.newHashSet();
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
        public boolean skipValidationFor(ResourceLocation resourceLocation) {
            return this.skipValidation.contains(resourceLocation);
        }

        public void skipValidation(ResourceLocation resourceLocation) {
            this.skipValidation.add(resourceLocation);
        }

        protected void add(ResourceLocation table, LootTable.Builder builder) {
            this.tables.put(table, builder);
        }

        public abstract void addLootTables();
    }

    public interface LootTableDataProvider extends DataProvider {

        void generate(BiConsumer<ResourceLocation, LootTable.Builder> exporter);

        PackOutput.PathProvider pathProvider();

        LootContextParamSet paramSet();

        boolean skipValidationFor(ResourceLocation resourceLocation);

        default CompletableFuture<?> run(CachedOutput output) {
            final Map<ResourceLocation, LootTable> lootTables = new HashMap<>();
            Map<RandomSupport.Seed128bit, ResourceLocation> seeds = new Object2ObjectOpenHashMap<>();
            this.generate((resourceLocation, builder) -> {
                ResourceLocation resourceLocation2 = seeds.put(RandomSequence.seedForKey(resourceLocation),
                        resourceLocation
                );
                if (resourceLocation2 != null) {
                    LOGGER.error("Loot table random sequence seed collision on " + resourceLocation2 + " and " +
                            resourceLocation);
                }

                builder.setRandomSequence(resourceLocation);
                if (lootTables.put(resourceLocation, builder.setParamSet(this.paramSet()).build()) != null) {
                    throw new IllegalStateException("Duplicate loot table " + resourceLocation);
                }
            });

            this.validate(lootTables);
            return CompletableFuture.allOf(lootTables.entrySet().stream().map((entry) -> {
                ResourceLocation resourceLocation = entry.getKey();
                LootTable lootTable = entry.getValue();
                Path path = this.pathProvider().json(resourceLocation);
                return DataProvider.saveStable(output, LootTable.CODEC, lootTable, path);
            }).toArray(CompletableFuture[]::new));
        }

        default void validate(Map<ResourceLocation, LootTable> tables) {
            ProblemReporter.Collector collector = new ProblemReporter.Collector();
            ValidationContext validationContext = new ValidationContext(collector,
                    LootContextParamSets.ALL_PARAMS,
                    new LootDataResolver() {
                        @Nullable
                        public <T> T getElement(LootDataId<T> lootDataId) {
                            if (lootDataId.type() == LootDataType.TABLE) {
                                LootTable lootTable;
                                // allows for referencing vanilla loot tables
                                if (LootTableDataProvider.this.skipValidationFor(lootDataId.location())) {
                                    lootTable = LootTable.lootTable().build();
                                } else {
                                    lootTable = tables.get(lootDataId.location());
                                }
                                return (T) lootTable;
                            } else {
                                return null;
                            }
                        }
                    }
            );

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

        default void validate(ResourceLocation resourceLocation, LootTable lootTable, ValidationContext validationContext) {
            if (!this.skipValidationFor(resourceLocation)) {
                lootTable.validate(validationContext.setParams(lootTable.getParamSet())
                        .enterElement("{" + resourceLocation + "}",
                                new LootDataId<>(LootDataType.TABLE, resourceLocation)
                        ));
            }
        }
    }
}
