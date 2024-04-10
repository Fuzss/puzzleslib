package fuzs.puzzleslib.forge.impl.core.context;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import fuzs.puzzleslib.api.core.v1.context.BlockInteractionsContext;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.level.BlockEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public final class BlockInteractionsContextForgeImpl implements BlockInteractionsContext {
    private final Map<ToolAction, Map<Block, BlockInteraction>> blockInteractions = Maps.newIdentityHashMap();

    @Override
    public void registerStrippable(Block strippedBlock, Block... unstrippedBlocks) {
        Objects.requireNonNull(strippedBlock, "stripped block is null");
        Objects.requireNonNull(unstrippedBlocks, "unstripped blocks is null");
        Preconditions.checkState(unstrippedBlocks.length > 0, "unstripped blocks is empty");
        BlockInteraction interaction = new BlockInteraction(strippedBlock);
        Map<Block, BlockInteraction> interactions = this.getToolActionMap(ToolActions.AXE_STRIP);
        for (Block unstrippedBlock : unstrippedBlocks) {
            Objects.requireNonNull(unstrippedBlock, "unstripped block is null");
            interactions.put(unstrippedBlock, interaction);
        }
    }

    @Override
    public void registerScrapeable(Block scrapedBlock, Block... unscrapedBlocks) {
        Objects.requireNonNull(scrapedBlock, "scraped block is null");
        Objects.requireNonNull(unscrapedBlocks, "unscraped blocks is null");
        Preconditions.checkState(unscrapedBlocks.length > 0, "unscraped blocks is empty");
        BlockInteraction interaction = new BlockInteraction(scrapedBlock);
        Map<Block, BlockInteraction> interactions = this.getToolActionMap(ToolActions.AXE_SCRAPE);
        for (Block unscrapedBlock : unscrapedBlocks) {
            Objects.requireNonNull(unscrapedBlock, "unscraped block is null");
            interactions.put(unscrapedBlock, interaction);
        }
    }

    @Override
    public void registerWaxable(Block unwaxedBlock, Block... waxedBlocks) {
        Objects.requireNonNull(unwaxedBlock, "unwaxed block is null");
        Objects.requireNonNull(waxedBlocks, "waxed blocks is null");
        Preconditions.checkState(waxedBlocks.length > 0, "waxed blocks is empty");
        BlockInteraction interaction = new BlockInteraction(unwaxedBlock);
        Map<Block, BlockInteraction> interactions = this.getToolActionMap(ToolActions.AXE_WAX_OFF);
        for (Block waxedBlock : waxedBlocks) {
            Objects.requireNonNull(waxedBlock, "waxed block is null");
            interactions.put(waxedBlock, interaction);
        }
    }

    @Override
    public void registerFlattenable(BlockState flattenedBlock, Block... unflattenedBlocks) {
        Objects.requireNonNull(flattenedBlock, "flattened block is null");
        Objects.requireNonNull(unflattenedBlocks, "unflattened blocks is null");
        Preconditions.checkState(unflattenedBlocks.length > 0, "unflattened blocks is empty");
        BlockInteraction interaction = new BlockInteraction($ -> flattenedBlock);
        Map<Block, BlockInteraction> interactions = this.getToolActionMap(ToolActions.SHOVEL_FLATTEN);
        for (Block unflattenedBlock : unflattenedBlocks) {
            Objects.requireNonNull(unflattenedBlock, "unflattened block is null");
            interactions.put(unflattenedBlock, interaction);
        }
    }

    @Override
    public void registerTillable(BlockState tilledBlock, @Nullable ItemLike droppedItem, boolean onlyIfAirAbove, Block... untilledBlocks) {
        Objects.requireNonNull(tilledBlock, "tilled block is null");
        Objects.requireNonNull(untilledBlocks, "untilled blocks is null");
        Preconditions.checkState(untilledBlocks.length > 0, "untilled blocks is empty");
        Predicate<UseOnContext> usagePredicate = onlyIfAirAbove ? HoeItem::onlyIfAirAbove : $ -> true;
        Consumer<UseOnContext> tillingAction = droppedItem != null ? HoeItem.changeIntoStateAndDropItem(tilledBlock, droppedItem) : HoeItem.changeIntoState(tilledBlock);
        BlockInteraction interaction = new BlockInteraction(usagePredicate, tillingAction, $ -> tilledBlock);
        Map<Block, BlockInteraction> interactions = this.getToolActionMap(ToolActions.HOE_TILL);
        for (Block untilledBlock : untilledBlocks) {
            Objects.requireNonNull(untilledBlock, "untilled block is null");
            interactions.put(untilledBlock, interaction);
        }
    }

    private Map<Block, BlockInteraction> getToolActionMap(ToolAction toolAction) {
        if (this.blockInteractions.isEmpty()) MinecraftForge.EVENT_BUS.addListener(this::onBlockToolModification);
        return this.blockInteractions.computeIfAbsent(toolAction, $ -> Maps.newIdentityHashMap());
    }

    private void onBlockToolModification(final BlockEvent.BlockToolModificationEvent evt) {
        Map<Block, BlockInteraction> interactions = this.blockInteractions.get(evt.getToolAction());
        if (interactions != null) {
            BlockState state = evt.getState();
            BlockInteraction interaction = interactions.get(state.getBlock());
            if (interaction != null && interaction.predicate().test(evt.getContext())) {
                // this makes hoe tilling set the block twice which cannot really be avoided if we want to keep utilizing the static helper methods in HoeItem
                interaction.consumer().accept(evt.getContext());
                evt.setFinalState(interaction.operator().apply(state));
            }
        }
    }

    private record BlockInteraction(Predicate<UseOnContext> predicate, Consumer<UseOnContext> consumer,
                                    UnaryOperator<BlockState> operator) {

        public BlockInteraction(Block block) {
            this(block::withPropertiesOf);
        }

        public BlockInteraction(UnaryOperator<BlockState> operator) {
            this($ -> true, $ -> {
            }, operator);
        }
    }
}
