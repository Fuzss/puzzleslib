package fuzs.puzzleslib.fabric.impl.core.context;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.core.v1.context.GameplayContentContext;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.fabricmc.fabric.api.registry.*;
import net.minecraft.core.Holder;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.FuelValues;
import org.apache.commons.lang3.math.Fraction;

import java.util.Objects;

public final class GameplayContentContextFabricImpl implements GameplayContentContext {
    private final Object2ObjectMap<Holder<? extends ItemLike>, Fraction> furnaceFuels = new Object2ObjectArrayMap<>();

    @Override
    public void registerFuel(Holder<? extends ItemLike> fuelItem, Fraction fuelValue) {
        Objects.requireNonNull(fuelItem, "fuel item is null");
        Objects.requireNonNull(fuelValue, "fuel value is null");
        if (this.furnaceFuels.isEmpty()) {
            FuelRegistryEvents.BUILD.register((FuelValues.Builder builder, FuelRegistryEvents.Context context) -> {
                Fraction fuelBaseValue = Fraction.getFraction(context.baseSmeltTime(), 1);
                this.furnaceFuels.forEach((Holder<? extends ItemLike> holder, Fraction fraction) -> {
                    builder.add(holder.value(), fraction.multiplyBy(fuelBaseValue).intValue());
                });
            });
        }
        this.furnaceFuels.put(fuelItem, fuelValue);
    }

    @Override
    public void registerFlammable(Holder<Block> flammableBlock, int encouragement, int flammability) {
        Preconditions.checkArgument(encouragement > 0, "encouragement is non-positive");
        Preconditions.checkArgument(flammability > 0, "flammability is non-positive");
        Objects.requireNonNull(flammableBlock, "flammable block is null");
        // flammability == burn, encouragement == spread
        FlammableBlockRegistry.getDefaultInstance().add(flammableBlock.value(), flammability, encouragement);
    }

    @Override
    public void registerCompostable(Holder<? extends ItemLike> compostableItem, float compostingChance) {
        Preconditions.checkArgument(compostingChance >= 0.0F && compostingChance <= 1.0F,
                "Value " + compostingChance + " outside of range 0.0 -> 1.0");
        Objects.requireNonNull(compostableItem, "compostable item is null");
        CompostingChanceRegistry.INSTANCE.add(compostableItem.value(), compostingChance);
    }

    @Override
    public void registerStrippable(Holder<Block> unstrippedBlock, Holder<Block> strippedBlock) {
        Objects.requireNonNull(unstrippedBlock, "unstripped block is null");
        Objects.requireNonNull(strippedBlock, "stripped block is null");
        StrippableBlockRegistry.registerCopyState(unstrippedBlock.value(), strippedBlock.value());
    }

    @Override
    public void registerFlattenable(Holder<Block> unflattenedBlock, Holder<Block> flattenedBlock) {
        Objects.requireNonNull(unflattenedBlock, "unflattened block is null");
        Objects.requireNonNull(flattenedBlock, "flattened block is null");
        FlattenableBlockRegistry.register(unflattenedBlock.value(), flattenedBlock.value().defaultBlockState());
    }

    @Override
    public void registerTillable(Holder<Block> untilledBlock, Holder<Block> tilledBlock) {
        Objects.requireNonNull(untilledBlock, "untilled block is null");
        Objects.requireNonNull(tilledBlock, "tilled block is null");
        TillableBlockRegistry.register(untilledBlock.value(),
                HoeItem::onlyIfAirAbove,
                tilledBlock.value().defaultBlockState());
    }

    @Override
    public void registerOxidizable(Holder<Block> unoxidizedBlock, Holder<Block> oxidizedBlock) {
        Objects.requireNonNull(unoxidizedBlock, "unoxidized block is null");
        Objects.requireNonNull(oxidizedBlock, "oxidized block is null");
        OxidizableBlocksRegistry.registerOxidizableBlockPair(unoxidizedBlock.value(), oxidizedBlock.value());
    }

    @Override
    public void registerWaxable(Holder<Block> unwaxedBlock, Holder<Block> waxedBlock) {
        Objects.requireNonNull(unwaxedBlock, "unwaxed block is null");
        Objects.requireNonNull(waxedBlock, "waxed block is null");
        OxidizableBlocksRegistry.registerWaxableBlockPair(unwaxedBlock.value(), waxedBlock.value());
    }
}
