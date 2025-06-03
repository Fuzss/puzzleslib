package fuzs.puzzleslib.api.core.v1.context;

import net.minecraft.core.Holder;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import org.apache.commons.lang3.math.Fraction;

/**
 * Register content to various gameplay registries.
 */
public interface GameplayContentContext {

    /**
     * Register items as furnace fuel.
     *
     * @param fuelItem  the fuel item
     * @param fuelValue the burn time in ticks
     */
    void registerFuel(Holder<? extends ItemLike> fuelItem, Fraction fuelValue);

    /**
     * Register blocks that fire can spread to.
     *
     * @param flammableBlock the flammable block
     * @param encouragement  a value determining how fast this block will spread fire to other nearby flammable blocks
     * @param flammability   a value determining how easily this block catches on fire from nearby fires
     */
    void registerFlammable(Holder<Block> flammableBlock, int encouragement, int flammability);

    /**
     * Register items for usage with the composter.
     *
     * @param compostableItem  the compostable item
     * @param compostingChance the chance the compost level will increase, allowed values range from {@code 0.0} to
     *                         {@code 1.0} inclusive
     */
    void registerCompostable(Holder<? extends ItemLike> compostableItem, float compostingChance);

    /**
     * Register blocks to be converted from interacting with an axe.
     *
     * @param unstrippedBlock the block before stripping
     * @param strippedBlock   the block after stripping
     */
    void registerStrippable(Holder<Block> unstrippedBlock, Holder<Block> strippedBlock);

    /**
     * Register blocks to be converted from interacting with a shovel.
     *
     * @param unflattenedBlock the block before flattening
     * @param flattenedBlock   the block after flattening
     */
    void registerFlattenable(Holder<Block> unflattenedBlock, Holder<Block> flattenedBlock);

    /**
     * Register blocks to be converted from interacting with a hoe.
     *
     * @param untilledBlock the block before tilling
     * @param tilledBlock   the block after tilling
     */
    void registerTillable(Holder<Block> untilledBlock, Holder<Block> tilledBlock);

    /**
     * Register blocks to be converted from oxidizing over time.
     *
     * @param unoxidizedBlock the block before oxidizing
     * @param oxidizedBlock   the block after oxidizing
     */
    void registerOxidizable(Holder<Block> unoxidizedBlock, Holder<Block> oxidizedBlock);

    /**
     * Register blocks to be converted from interacting with a honeycomb.
     *
     * @param unwaxedBlock the block before waxing
     * @param waxedBlock   the block after waxing
     */
    void registerWaxable(Holder<Block> unwaxedBlock, Holder<Block> waxedBlock);
}
