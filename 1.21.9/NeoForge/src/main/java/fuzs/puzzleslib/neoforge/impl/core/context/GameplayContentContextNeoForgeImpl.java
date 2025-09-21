package fuzs.puzzleslib.neoforge.impl.core.context;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import fuzs.puzzleslib.api.core.v1.context.GameplayContentContext;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import fuzs.puzzleslib.neoforge.api.data.v2.core.NeoForgeDataProviderContext;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.builtin.*;
import org.apache.commons.lang3.math.Fraction;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class GameplayContentContextNeoForgeImpl implements GameplayContentContext {
    private final DataMapBuilder<Holder<? extends ItemLike>, Fraction> furnaceFuels;
    private final Map<Holder<Block>, Flammable> flammables = new LinkedHashMap<>();
    private final DataMapBuilder<Holder<? extends ItemLike>, Float> compostables;
    private final BlockConversionBuilder strippables = new BlockConversionBuilder(ItemAbilities.AXE_STRIP);
    private final BlockConversionBuilder flattenables = new BlockConversionBuilder(ItemAbilities.SHOVEL_FLATTEN);
    private final BlockConversionBuilder tillables = new BlockConversionBuilder(ItemAbilities.HOE_TILL,
            HoeItem::onlyIfAirAbove);
    private final DataMapBuilder<Holder<Block>, Holder<Block>> oxidizables;
    private final DataMapBuilder<Holder<Block>, Holder<Block>> waxables;
    private final IEventBus eventBus;

    public GameplayContentContextNeoForgeImpl(String modId, IEventBus eventBus) {
        this.eventBus = eventBus;
        this.furnaceFuels = new DataMapBuilder<>(modId,
                NeoForgeDataMaps.FURNACE_FUELS,
                (Holder<? extends ItemLike> holder) -> holder.value().asItem().builtInRegistryHolder(),
                (Fraction fraction) -> {
                    Fraction fuelBaseValue = Fraction.getFraction(200, 1);
                    return new FurnaceFuel(fraction.multiplyBy(fuelBaseValue).intValue());
                });
        this.compostables = new DataMapBuilder<>(modId,
                NeoForgeDataMaps.COMPOSTABLES,
                (Holder<? extends ItemLike> holder) -> holder.value().asItem().builtInRegistryHolder(),
                Compostable::new);
        this.oxidizables = new DataMapBuilder<>(modId,
                NeoForgeDataMaps.OXIDIZABLES,
                Function.identity(),
                (Holder<Block> holder) -> new Oxidizable(holder.value()));
        this.waxables = new DataMapBuilder<>(modId,
                NeoForgeDataMaps.WAXABLES,
                Function.identity(),
                (Holder<Block> holder) -> new Waxable(holder.value()));
    }

    @Override
    public void registerFuel(Holder<? extends ItemLike> fuelItem, Fraction fuelValue) {
        Objects.requireNonNull(fuelItem, "fuel item is null");
        Objects.requireNonNull(fuelValue, "fuel value is null");
        this.furnaceFuels.register(fuelItem, fuelValue);
    }

    @Override
    public void registerFlammable(Holder<Block> flammableBlock, int encouragement, int flammability) {
        Preconditions.checkArgument(encouragement > 0, "encouragement is non-positive");
        Preconditions.checkArgument(flammability > 0, "flammability is non-positive");
        Objects.requireNonNull(flammableBlock, "flammable block is null");
        if (this.flammables.isEmpty()) {
            this.eventBus.addListener((final FMLCommonSetupEvent event) -> {
                event.enqueueWork(() -> {
                    this.flammables.forEach((Holder<Block> holder, Flammable flammable) -> {
                        ((FireBlock) Blocks.FIRE).setFlammable(holder.value(),
                                flammable.encouragement(),
                                flammable.flammability());
                    });
                });
            });
        }
        this.flammables.put(flammableBlock, new Flammable(encouragement, flammability));
    }

    @Override
    public void registerCompostable(Holder<? extends ItemLike> compostableItem, float compostingChance) {
        Preconditions.checkArgument(compostingChance >= 0.0F && compostingChance <= 1.0F,
                "Value " + compostingChance + " outside of range 0.0 -> 1.0");
        Objects.requireNonNull(compostableItem, "compostable item is null");
        this.compostables.register(compostableItem, compostingChance);
    }

    @Override
    public void registerStrippable(Holder<Block> unstrippedBlock, Holder<Block> strippedBlock) {
        Objects.requireNonNull(unstrippedBlock, "unstripped block is null");
        Objects.requireNonNull(strippedBlock, "stripped block is null");
        this.strippables.register(unstrippedBlock, strippedBlock);
    }

    @Override
    public void registerFlattenable(Holder<Block> unflattenedBlock, Holder<Block> flattenedBlock) {
        Objects.requireNonNull(unflattenedBlock, "unflattened block is null");
        Objects.requireNonNull(flattenedBlock, "flattened block is null");
        this.flattenables.register(unflattenedBlock, flattenedBlock);
    }

    @Override
    public void registerTillable(Holder<Block> untilledBlock, Holder<Block> tilledBlock) {
        Objects.requireNonNull(untilledBlock, "untilled block is null");
        Objects.requireNonNull(tilledBlock, "tilled block is null");
        this.tillables.register(untilledBlock, tilledBlock);
    }

    @Override
    public void registerOxidizable(Holder<Block> unoxidizedBlock, Holder<Block> oxidizedBlock) {
        Objects.requireNonNull(unoxidizedBlock, "unoxidized block is null");
        Objects.requireNonNull(oxidizedBlock, "oxidized block is null");
        this.oxidizables.register(unoxidizedBlock, oxidizedBlock);
    }

    @Override
    public void registerWaxable(Holder<Block> unwaxedBlock, Holder<Block> waxedBlock) {
        Objects.requireNonNull(unwaxedBlock, "unwaxed block is null");
        Objects.requireNonNull(waxedBlock, "waxed block is null");
        this.waxables.register(unwaxedBlock, waxedBlock);
    }

    private static class DataMapBuilder<K, V> {
        private final Map<K, V> values = new LinkedHashMap<>();
        private final String modId;
        private final NeoForgeDataProviderContext.Factory factory;

        public <R, T> DataMapBuilder(String modId, DataMapType<R, T> dataMapType, Function<K, Holder<R>> keyConverter, Function<V, T> valueConverter) {
            this.modId = modId;
            this.factory = (NeoForgeDataProviderContext context) -> {
                return new DataMapProvider(context.getPackOutput(), context.getRegistries()) {
                    @Override
                    protected void gather(HolderLookup.Provider registries) {
                        Builder<T, R> builder = this.builder(dataMapType);
                        DataMapBuilder.this.values.forEach((K key, V value) -> {
                            builder.add(keyConverter.apply(key), valueConverter.apply(value), false);
                        });
                    }

                    @Override
                    public String getName() {
                        return super.getName() + " for " +
                                ResourceKey.create(dataMapType.registryKey(), dataMapType.id());
                    }
                };
            };
        }

        public void register(K key, V value) {
            if (this.values.isEmpty()) {
                DataProviderHelper.registerDataProviders(this.modId, this.factory);
            }
            this.values.put(key, value);
        }
    }

    private record Flammable(int encouragement, int flammability) {

    }

    private static class BlockConversionBuilder {
        private final Map<Holder<Block>, Holder<Block>> values = new LinkedHashMap<>();
        private final Supplier<Map<Block, Block>> supplier = Suppliers.memoize(() -> {
            ImmutableMap.Builder<Block, Block> builder = ImmutableMap.builder();
            for (Map.Entry<Holder<Block>, Holder<Block>> entry : this.values.entrySet()) {
                builder.put(entry.getKey().value(), entry.getValue().value());
            }
            return builder.build();
        });
        private final ItemAbility itemAbility;
        private final Predicate<UseOnContext> predicate;

        public BlockConversionBuilder(ItemAbility itemAbility) {
            this(itemAbility, Predicates.alwaysTrue());
        }

        public BlockConversionBuilder(ItemAbility itemAbility, Predicate<UseOnContext> predicate) {
            this.itemAbility = itemAbility;
            this.predicate = predicate;
        }

        public void register(Holder<Block> key, Holder<Block> value) {
            if (this.values.isEmpty()) {
                NeoForge.EVENT_BUS.addListener((final BlockEvent.BlockToolModificationEvent event) -> {
                    if (event.getItemAbility() == this.itemAbility && this.predicate.test(event.getContext())) {
                        Block block = this.supplier.get().get(event.getState().getBlock());
                        if (block != null) {
                            event.setFinalState(block.defaultBlockState());
                        }
                    }
                });
            }
            this.values.put(key, value);
        }
    }
}
