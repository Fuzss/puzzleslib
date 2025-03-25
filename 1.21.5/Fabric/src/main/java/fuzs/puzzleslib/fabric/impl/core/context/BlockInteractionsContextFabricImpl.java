package fuzs.puzzleslib.fabric.impl.core.context;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.core.v1.context.BlockInteractionsContext;
import net.fabricmc.fabric.api.registry.FlattenableBlockRegistry;
import net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry;
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import net.fabricmc.fabric.api.registry.TillableBlockRegistry;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class BlockInteractionsContextFabricImpl implements BlockInteractionsContext {

    @Override
    public void registerStrippable(Block strippedBlock, Block... unstrippedBlocks) {
        Objects.requireNonNull(strippedBlock, "stripped block is null");
        Objects.requireNonNull(unstrippedBlocks, "unstripped blocks is null");
        Preconditions.checkState(unstrippedBlocks.length > 0, "unstripped blocks is empty");
        for (Block unstrippedBlock : unstrippedBlocks) {
            Objects.requireNonNull(unstrippedBlock, "unstripped block is null");
            StrippableBlockRegistry.register(unstrippedBlock, strippedBlock);
        }
    }

    @Override
    public void registerScrapeable(Block scrapedBlock, Block... unscrapedBlocks) {
        Objects.requireNonNull(scrapedBlock, "scraped block is null");
        Objects.requireNonNull(unscrapedBlocks, "unscraped blocks is null");
        Preconditions.checkState(unscrapedBlocks.length > 0, "unscraped blocks is empty");
        for (Block unscrapedBlock : unscrapedBlocks) {
            Objects.requireNonNull(unscrapedBlock, "unscraped block is null");
            OxidizableBlocksRegistry.registerOxidizableBlockPair(scrapedBlock, unscrapedBlock);
        }
    }

    @Override
    public void registerWaxable(Block unwaxedBlock, Block... waxedBlocks) {
        Objects.requireNonNull(unwaxedBlock, "unwaxed block is null");
        Objects.requireNonNull(waxedBlocks, "waxed blocks is null");
        Preconditions.checkState(waxedBlocks.length > 0, "waxed blocks is empty");
        for (Block waxedBlock : waxedBlocks) {
            Objects.requireNonNull(waxedBlock, "waxed block is null");
            OxidizableBlocksRegistry.registerWaxableBlockPair(unwaxedBlock, waxedBlock);
        }
    }

    @Override
    public void registerFlattenable(BlockState flattenedBlock, Block... unflattenedBlocks) {
        Objects.requireNonNull(flattenedBlock, "flattened block is null");
        Objects.requireNonNull(unflattenedBlocks, "unflattened blocks is null");
        Preconditions.checkState(unflattenedBlocks.length > 0, "unflattened blocks is empty");
        for (Block unflattenedBlock : unflattenedBlocks) {
            Objects.requireNonNull(unflattenedBlock, "unflattened block is null");
            FlattenableBlockRegistry.register(unflattenedBlock, flattenedBlock);
        }
    }

    @Override
    public void registerTillable(BlockState tilledBlock, @Nullable ItemLike droppedItem, boolean requireAirAbove, Block... untilledBlocks) {
        Objects.requireNonNull(tilledBlock, "tilled block is null");
        Objects.requireNonNull(untilledBlocks, "untilled blocks is null");
        Preconditions.checkState(untilledBlocks.length > 0, "untilled blocks is empty");
        Predicate<UseOnContext> usagePredicate = requireAirAbove ? HoeItem::onlyIfAirAbove : $ -> true;
        Consumer<UseOnContext> tillingAction = droppedItem != null ? HoeItem.changeIntoStateAndDropItem(tilledBlock, droppedItem) : HoeItem.changeIntoState(tilledBlock);
        for (Block untilledBlock : untilledBlocks) {
            Objects.requireNonNull(untilledBlock, "untilled block is null");
            TillableBlockRegistry.register(untilledBlock, usagePredicate, tillingAction);
        }
    }
}
