package fuzs.puzzleslib.api.data.v2;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public final class AbstractLootProvider {

    private AbstractLootProvider() {
        // NO-OP
    }

    public static LootTableProvider createProvider(PackOutput packOutput, LootTableSubProvider provider, LootContextParamSet paramSet) {
        return new LootTableProvider(packOutput, Set.of(), List.of(new LootTableProvider.SubProviderEntry(() -> provider, paramSet)));
    }

    public static abstract class Blocks extends BlockLootSubProvider implements DataProvider {
        private final LootTableProvider provider;
        protected final String modId;

        public Blocks(DataProviderContext context) {
            this(context.getModId(), context.getPackOutput());
        }

        public Blocks(String modId, PackOutput packOutput) {
            super(Collections.emptySet(), FeatureFlags.REGISTRY.allFlags());
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

        protected void dropNothing(Block block) {
            this.add(block, noDrop());
        }
    }

    public static abstract class EntityTypes extends EntityLootSubProvider implements DataProvider {
        private final LootTableProvider provider;
        protected final String modId;

        public EntityTypes(DataProviderContext context) {
            this(context.getModId(), context.getPackOutput());
        }

        public EntityTypes(String modId, PackOutput packOutput) {
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

        protected boolean canHaveLootTable(EntityType<?> entityType) {
            return entityType.getCategory() != MobCategory.MISC;
        }
    }

    public static abstract class Simple implements LootTableSubProvider, DataProvider {
        private final LootTableProvider provider;
        protected final Map<ResourceLocation, LootTable.Builder> map = Maps.newHashMap();

        public Simple(LootContextParamSet paramSet, DataProviderContext context) {
            this(paramSet, context.getPackOutput());
        }

        public Simple(LootContextParamSet paramSet, PackOutput packOutput) {
            this.provider = createProvider(packOutput, this, paramSet);
        }

        @Override
        public final CompletableFuture<?> run(CachedOutput output) {
            return this.provider.run(output);
        }

        @Override
        public String getName() {
            // multiple data providers cannot have the same name, so handle it like this
            return String.join(" ", StringUtils.splitByCharacterTypeCamelCase(this.getClass().getSimpleName()));
        }

        @Override
        public void generate(BiConsumer<ResourceLocation, LootTable.Builder> exporter) {
            this.addLootTables();
            this.map.forEach(exporter);
        }

        protected void add(ResourceLocation table, LootTable.Builder builder) {
            this.map.put(table, builder);
        }

        public abstract void addLootTables();
    }
}
