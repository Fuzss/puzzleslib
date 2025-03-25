package fuzs.puzzleslib.neoforge.impl.core.context;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.google.common.collect.Maps;
import fuzs.puzzleslib.api.core.v1.context.BlockInteractionsContext;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.apache.commons.lang3.function.Consumers;
import org.jetbrains.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public final class BlockInteractionsContextNeoForgeImpl implements BlockInteractionsContext {
    private final Map<ItemAbility, Map<Block, BlockInteraction>> abilities = new IdentityHashMap<>();

    @Override
    public void registerStrippable(Block strippedBlock, Block... unstrippedBlocks) {
        Objects.requireNonNull(strippedBlock, "stripped block is null");
        Objects.requireNonNull(unstrippedBlocks, "unstripped blocks is null");
        Preconditions.checkState(unstrippedBlocks.length > 0, "unstripped blocks is empty");
        BlockInteraction interaction = new BlockInteraction(strippedBlock);
        Map<Block, BlockInteraction> interactions = this.getAbilitiesMap(ItemAbilities.AXE_STRIP);
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
        Map<Block, BlockInteraction> interactions = this.getAbilitiesMap(ItemAbilities.AXE_SCRAPE);
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
        Map<Block, BlockInteraction> interactions = this.getAbilitiesMap(ItemAbilities.AXE_WAX_OFF);
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
        Map<Block, BlockInteraction> interactions = this.getAbilitiesMap(ItemAbilities.SHOVEL_FLATTEN);
        for (Block unflattenedBlock : unflattenedBlocks) {
            Objects.requireNonNull(unflattenedBlock, "unflattened block is null");
            interactions.put(unflattenedBlock, interaction);
        }
    }

    @Override
    public void registerTillable(BlockState tilledBlock, @Nullable ItemLike droppedItem, boolean requireAirAbove, Block... untilledBlocks) {
        Objects.requireNonNull(tilledBlock, "tilled block is null");
        Objects.requireNonNull(untilledBlocks, "untilled blocks is null");
        Preconditions.checkState(untilledBlocks.length > 0, "untilled blocks is empty");
        Predicate<UseOnContext> usagePredicate = requireAirAbove ? HoeItem::onlyIfAirAbove : $ -> true;
        Consumer<UseOnContext> tillingAction = droppedItem != null ? HoeItem.changeIntoStateAndDropItem(tilledBlock, droppedItem) : HoeItem.changeIntoState(tilledBlock);
        BlockInteraction interaction = new BlockInteraction(usagePredicate, tillingAction, $ -> tilledBlock);
        Map<Block, BlockInteraction> interactions = this.getAbilitiesMap(ItemAbilities.HOE_TILL);
        for (Block untilledBlock : untilledBlocks) {
            Objects.requireNonNull(untilledBlock, "untilled block is null");
            interactions.put(untilledBlock, interaction);
        }
    }

    private Map<Block, BlockInteraction> getAbilitiesMap(ItemAbility itemAbility) {
        if (this.abilities.isEmpty()) {
            NeoForge.EVENT_BUS.addListener(this::onBlockToolModification);
        }
        return this.abilities.computeIfAbsent(itemAbility, (ItemAbility itemAbilityX) -> new IdentityHashMap<>());
    }

    private void onBlockToolModification(final BlockEvent.BlockToolModificationEvent evt) {
        Map<Block, BlockInteraction> interactions = this.abilities.get(evt.getItemAbility());
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
            this(Predicates.alwaysTrue(), Consumers.nop(), operator);
        }
    }
}
