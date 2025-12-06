package fuzs.puzzleslib.api.core.v1.context;

import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.function.Consumer;

/**
 * Register new villager trades.
 */
public interface VillagerTradesContext {

    /**
     * Add new trades to a specific villager profession.
     * <p>
     * Supports both adding new and removing existing trades.
     *
     * @param profession the villager profession
     * @param level      the villager level
     * @param factories  the trade list for the specified level
     */
    void registerVillagerTrades(VillagerProfession profession, VillagerLevel level, Consumer<List<VillagerTrades.ItemListing>> factories);

    /**
     * Add new trades to the wandering trader.
     * <p>
     * Only supports adding new trades.
     *
     * @param pool         the trade pool to add to, how many trades are picked for each trader is hardcoded:
     *                     <ul>
     *                     <li>{@link WanderingTradesPool#PURCHASES PURCHASES}: 2</li>
     *                     <li>{@link WanderingTradesPool#COMMON_SALES COMMON_SALES}: 5</li>
     *                     <li>{@link WanderingTradesPool#SPECIAL_SALES SPECIAL_SALES}: 2</li>
     *                     </ul>
     * @param itemListings the new trades to add
     */
    void registerWanderingTrades(WanderingTradesPool pool, VillagerTrades.ItemListing... itemListings);

    enum VillagerLevel {
        NOVICE(1),
        APPRENTICE(2),
        JOURNEYMAN(3),
        EXPERT(4),
        MASTER(5);

        private final int level;

        VillagerLevel(int level) {
            this.level = level;
        }

        @ApiStatus.Internal
        public int getLevel() {
            return this.level;
        }
    }

    enum WanderingTradesPool {
        PURCHASES,
        COMMON_SALES,
        SPECIAL_SALES
    }
}
