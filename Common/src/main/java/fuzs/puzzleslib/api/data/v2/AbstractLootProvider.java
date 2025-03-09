package fuzs.puzzleslib.api.data.v2;

import com.google.common.base.Preconditions;
import com.google.common.collect.Multimap;
import com.mojang.serialization.Lifecycle;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.Util;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.RandomSequence;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class AbstractLootProvider {

    private AbstractLootProvider() {
        // NO-OP
    }

    public static abstract class Blocks extends BlockLootSubProvider implements LootTableDataProvider {
        private final Set<ResourceKey<LootTable>> skipValidation = new HashSet<>();
        private final PackOutput.PathProvider pathProvider;
        private final CompletableFuture<HolderLookup.Provider> registries;
        private final String modId;

        public Blocks(DataProviderContext context) {
            this(context.getModId(), context.getPackOutput(), context.getRegistries());
        }

        public Blocks(String modId, PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
            // we set the correct lookup provider just before we run data generation, it is not available here yet
            super(Collections.emptySet(), FeatureFlags.REGISTRY.allFlags(), RegistryAccess.EMPTY);
            this.pathProvider = packOutput.createRegistryElementsPathProvider(Registries.LOOT_TABLE);
            this.registries = registries;
            this.modId = modId;
        }

        @Override
        public CompletableFuture<?> run(CachedOutput output) {
            return this.registries.thenApply((HolderLookup.Provider registries) -> {
                return super.registries = registries;
            }).thenCompose((HolderLookup.Provider registries) -> {
                return this.run(output, registries);
            }).thenRun(() -> {
                super.registries = RegistryAccess.EMPTY;
            });
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
        public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {
            this.generate();
            Set<ResourceKey<LootTable>> lootTables = new HashSet<>();
            this.getRegistryEntries().forEach((Holder.Reference<Block> holder) -> {
                Optional<ResourceKey<LootTable>> optional = holder.value().getLootTable();
                if (optional.isPresent()) {
                    ResourceKey<LootTable> resourceKey = optional.get();
                    if (lootTables.add(resourceKey)) {
                        LootTable.Builder builder = this.map.remove(resourceKey);
                        if (builder != null) {
                            consumer.accept(resourceKey, builder);
                        } else if (!this.skipValidationFor(resourceKey)) {
                            throw new IllegalStateException("Missing loot table '%s' for '%s'".formatted(optional,
                                    holder.key().location()));
                        }
                    }
                }
            });
            if (!this.map.isEmpty()) {
                throw new IllegalStateException("Created block loot tables for non-blocks: " + this.map.keySet());
            }
        }

        @Override
        public HolderLookup.Provider registries() {
            Preconditions.checkState(super.registries != RegistryAccess.EMPTY, "registry access is empty");
            return super.registries;
        }

        @Override
        public PackOutput.PathProvider pathProvider() {
            return this.pathProvider;
        }

        @Override
        public ContextKeySet paramSet() {
            return LootContextParamSets.BLOCK;
        }

        @Override
        public boolean skipValidationFor(ResourceKey<LootTable> resourceKey) {
            return this.skipValidation.contains(resourceKey);
        }

        public void skipValidation(ResourceLocation resourceLocation) {
            this.skipValidation(ResourceKey.create(Registries.LOOT_TABLE, resourceLocation));
        }

        public void skipValidation(ResourceKey<LootTable> resourceKey) {
            this.skipValidation.add(resourceKey);
        }

        public void skipValidation(Block block) {
            block.getLootTable().ifPresent(this::skipValidation);
        }

        public void dropNothing(Block block) {
            this.add(block, noDrop());
        }

        public void dropNameable(Block block) {
            this.add(block, this::createNameableBlockEntityTable);
        }

        protected Stream<Holder.Reference<Block>> getRegistryEntries() {
            return BuiltInRegistries.BLOCK.listElements()
                    .filter((Holder.Reference<Block> holder) -> holder.key()
                            .location()
                            .getNamespace()
                            .equals(this.modId));
        }
    }

    public abstract static class EntityTypes extends EntityLootSubProvider implements LootTableDataProvider {
        private final Set<ResourceKey<LootTable>> skipValidation = new HashSet<>();
        private final PackOutput.PathProvider pathProvider;
        private final CompletableFuture<HolderLookup.Provider> registries;
        private final String modId;

        public EntityTypes(DataProviderContext context) {
            this(context.getModId(), context.getPackOutput(), context.getRegistries());
        }

        public EntityTypes(String modId, PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
            // we set the correct lookup provider just before we run data generation, it is not available here yet
            super(FeatureFlags.REGISTRY.allFlags(), RegistryAccess.EMPTY);
            this.pathProvider = packOutput.createRegistryElementsPathProvider(Registries.LOOT_TABLE);
            this.registries = registries;
            this.modId = modId;
        }

        @Override
        public CompletableFuture<?> run(CachedOutput output) {
            return this.registries.thenCompose((HolderLookup.Provider registries) -> {
                super.registries = registries;
                return this.run(output, registries).thenRun(() -> {
                    super.registries = RegistryAccess.EMPTY;
                });
            });
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
        public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {
            this.generate();
            Set<ResourceKey<LootTable>> lootTables = new HashSet<>();
            this.getRegistryEntries().forEach((Holder.Reference<EntityType<?>> holder) -> {
                EntityType<?> entityType = holder.value();
                Map<ResourceKey<LootTable>, LootTable.Builder> map = this.map.remove(entityType);
                if (this.canHaveLootTable(entityType)) {
                    Optional<ResourceKey<LootTable>> optional = entityType.getDefaultLootTable();
                    if (optional.isPresent()) {
                        ResourceKey<LootTable> resourceKey = optional.get();
                        if (!this.skipValidationFor(resourceKey) && (map == null || !map.containsKey(resourceKey))) {
                            throw new IllegalStateException(String.format(Locale.ROOT,
                                    "Missing loot table '%s' for '%s'",
                                    resourceKey,
                                    holder.key().location()));
                        }
                    }
                    if (map != null) {
                        map.forEach((resourceLocation, builder) -> {
                            if (!lootTables.add(resourceLocation)) {
                                throw new IllegalStateException(String.format(Locale.ROOT,
                                        "Duplicate loot table '%s' for '%s'",
                                        resourceLocation,
                                        holder.key().location()));
                            } else {
                                consumer.accept(resourceLocation, builder);
                            }
                        });
                    }
                } else if (map != null) {
                    throw new IllegalStateException(String.format(Locale.ROOT,
                            "Weird loot table(s) '%s' for '%s', not a LivingEntity so should not have loot",
                            map.keySet()
                                    .stream()
                                    .map(ResourceKey::location)
                                    .map(ResourceLocation::toString)
                                    .collect(Collectors.joining(",")),
                            holder.key().location()));
                }
            });
            if (!this.map.isEmpty()) {
                throw new IllegalStateException(
                        "Created loot tables for entities not supported by data pack: " + this.map.keySet());
            }
        }

        @Override
        public HolderLookup.Provider registries() {
            Preconditions.checkState(super.registries != RegistryAccess.EMPTY, "registry access is empty");
            return super.registries;
        }

        @Override
        public PackOutput.PathProvider pathProvider() {
            return this.pathProvider;
        }

        @Override
        public ContextKeySet paramSet() {
            return LootContextParamSets.ENTITY;
        }

        @Override
        public boolean skipValidationFor(ResourceKey<LootTable> resourceKey) {
            return this.skipValidation.contains(resourceKey);
        }

        public void skipValidation(ResourceLocation resourceLocation) {
            this.skipValidation(ResourceKey.create(Registries.LOOT_TABLE, resourceLocation));
        }

        public void skipValidation(ResourceKey<LootTable> resourceKey) {
            this.skipValidation.add(resourceKey);
        }

        public void skipValidation(EntityType<?> entityType) {
            entityType.getDefaultLootTable().ifPresent(this::skipValidation);
        }

        protected boolean canHaveLootTable(EntityType<?> entityType) {
            return entityType.getCategory() != MobCategory.MISC;
        }

        protected Stream<Holder.Reference<EntityType<?>>> getRegistryEntries() {
            return BuiltInRegistries.ENTITY_TYPE.listElements()
                    .filter((Holder.Reference<EntityType<?>> holder) -> holder.key()
                            .location()
                            .getNamespace()
                            .equals(this.modId));
        }
    }

    public static abstract class Simple implements LootTableDataProvider {
        private final Map<ResourceKey<LootTable>, LootTable.Builder> tables = new HashMap<>();
        private final Set<ResourceKey<LootTable>> skipValidation = new HashSet<>();
        private final ContextKeySet paramSet;
        private final PackOutput.PathProvider pathProvider;
        private final CompletableFuture<HolderLookup.Provider> registries;
        private HolderLookup.Provider registryAccess;

        public Simple(ContextKeySet paramSet, DataProviderContext context) {
            this(paramSet, context.getPackOutput(), context.getRegistries());
        }

        public Simple(ContextKeySet paramSet, PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
            this.paramSet = paramSet;
            this.pathProvider = packOutput.createRegistryElementsPathProvider(Registries.LOOT_TABLE);
            this.registries = registries;
            this.registryAccess = RegistryAccess.EMPTY;
        }

        @Override
        public CompletableFuture<?> run(CachedOutput output) {
            return this.registries.thenCompose((HolderLookup.Provider registries) -> {
                this.registryAccess = registries;
                return this.run(output, registries).thenRun(() -> {
                    this.registryAccess = RegistryAccess.EMPTY;
                });
            });
        }

        @Override
        public String getName() {
            // multiple data providers cannot have the same name, so handle this like so
            return String.join(" ", StringUtils.splitByCharacterTypeCamelCase(this.getClass().getSimpleName()));
        }

        @Override
        public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> exporter) {
            this.addLootTables();
            this.tables.forEach(exporter);
        }

        @Override
        public HolderLookup.Provider registries() {
            Preconditions.checkState(this.registryAccess != RegistryAccess.EMPTY, "registry access is empty");
            return this.registryAccess;
        }

        @Override
        public PackOutput.PathProvider pathProvider() {
            return this.pathProvider;
        }

        @Override
        public ContextKeySet paramSet() {
            return this.paramSet;
        }

        @Override
        public boolean skipValidationFor(ResourceKey<LootTable> resourceKey) {
            return this.skipValidation.contains(resourceKey);
        }

        public void skipValidation(ResourceLocation resourceLocation) {
            this.skipValidation(ResourceKey.create(Registries.LOOT_TABLE, resourceLocation));
        }

        public void skipValidation(ResourceKey<LootTable> resourceKey) {
            this.skipValidation.add(resourceKey);
        }

        protected void add(ResourceKey<LootTable> table, LootTable.Builder builder) {
            this.tables.put(table, builder);
        }

        public abstract void addLootTables();
    }

    public interface LootTableDataProvider extends DataProvider, LootTableSubProvider {

        HolderLookup.Provider registries();

        PackOutput.PathProvider pathProvider();

        ContextKeySet paramSet();

        boolean skipValidationFor(ResourceKey<LootTable> resourceKey);

        default CompletableFuture<?> run(CachedOutput output, HolderLookup.Provider registries) {
            DefaultedMappedRegistry<LootTable> registry = new DefaultedMappedRegistry<>("empty",
                    Registries.LOOT_TABLE,
                    Lifecycle.experimental(),
                    false);
            ResourceKey<LootTable> defaultKey = ResourceKey.create(Registries.LOOT_TABLE, registry.getDefaultKey());
            registry.register(defaultKey, LootTable.EMPTY, RegistrationInfo.BUILT_IN);
            Map<RandomSupport.Seed128bit, ResourceLocation> seeds = new Object2ObjectOpenHashMap<>();
            this.generate((ResourceKey<LootTable> resourceKey, LootTable.Builder builder) -> {
                ResourceLocation resourceLocation = resourceKey.location();
                ResourceLocation oldResourceLocation = seeds.put(RandomSequence.seedForKey(resourceLocation),
                        resourceLocation);
                if (oldResourceLocation != null) {
                    Util.logAndPauseIfInIde(
                            "Loot table random sequence seed collision on " + oldResourceLocation + " and " +
                                    resourceKey);
                }

                builder.setRandomSequence(resourceLocation);
                LootTable lootTable = builder.setParamSet(this.paramSet()).build();
                registry.register(resourceKey, lootTable, RegistrationInfo.BUILT_IN);
            });

            registry.freeze();
            this.validate(registry);

            return CompletableFuture.allOf(registry.entrySet()
                    .stream()
                    .filter((Map.Entry<ResourceKey<LootTable>, LootTable> entry) -> {
                        return entry.getKey() != defaultKey;
                    })
                    .map((Map.Entry<ResourceKey<LootTable>, LootTable> entry) -> {
                        ResourceKey<LootTable> resourceKey = entry.getKey();
                        LootTable lootTable = entry.getValue();
                        Path path = this.pathProvider().json(resourceKey.location());
                        return DataProvider.saveStable(output, registries, LootTable.DIRECT_CODEC, lootTable, path);
                    })
                    .toArray(CompletableFuture[]::new));
        }

        default void validate(Registry<LootTable> registry) {
            ProblemReporter.Collector collector = new ProblemReporter.Collector();
            HolderGetter.Provider registries = new RegistryAccess.ImmutableRegistryAccess(List.of(registry)).freeze();
            ValidationContext validationContext = new ValidationContext(collector,
                    LootContextParamSets.ALL_PARAMS,
                    registries);

            registry.listElements().forEach((Holder.Reference<LootTable> holder) -> {
                this.validate(holder, validationContext);
            });

            Multimap<String, String> multimap = collector.get();
            if (!multimap.isEmpty()) {
                multimap.forEach((string, string2) -> {
                    LOGGER.warn("Found validation problem in {}: {}", string, string2);
                });
                throw new IllegalStateException("Failed to validate loot tables, see logs");
            }
        }

        default void validate(Holder.Reference<LootTable> holder, ValidationContext validationContext) {
            if (!this.skipValidationFor(holder.key())) {
                holder.value()
                        .validate(validationContext.setContextKeySet(holder.value().getParamSet())
                                .enterElement("{" + holder.key().location() + "}", holder.key()));
            }
        }
    }
}
