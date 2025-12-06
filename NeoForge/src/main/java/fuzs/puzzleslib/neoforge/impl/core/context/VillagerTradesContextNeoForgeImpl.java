package fuzs.puzzleslib.neoforge.impl.core.context;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import fuzs.puzzleslib.api.core.v1.context.VillagerTradesContext;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.neoforge.event.village.WandererTradesEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public final class VillagerTradesContextNeoForgeImpl implements VillagerTradesContext {
    private final List<VillagerTrade> villagerTrades = new ArrayList<>();
    private final List<WanderingTrade> wanderingTrades = new ArrayList<>();

    @Override
    public void registerVillagerTrades(VillagerProfession profession, VillagerLevel level, Consumer<List<VillagerTrades.ItemListing>> factories) {
        Objects.requireNonNull(profession, "profession is null");
        Objects.requireNonNull(level, "level is null");
        Objects.requireNonNull(factories, "factories is null");
        if (this.villagerTrades.isEmpty()) {
            NeoForge.EVENT_BUS.addListener((final VillagerTradesEvent event) -> {
                this.villagerTrades.forEach((VillagerTrade villagerTrade) -> villagerTrade.registerTrades(event));
            });
        }

        this.villagerTrades.add(new VillagerTrade(profession, level, factories));
    }

    @Override
    public void registerWanderingTrades(WanderingTradesPool pool, VillagerTrades.ItemListing... itemListings) {
        Objects.requireNonNull(pool, "pool is null");
        Objects.requireNonNull(itemListings, "item listings is null");
        Preconditions.checkArgument(itemListings.length > 0, "item listings is empty");
        if (this.wanderingTrades.isEmpty()) {
            NeoForge.EVENT_BUS.addListener((final WandererTradesEvent event) -> {
                this.wanderingTrades.forEach((WanderingTrade wanderingTrade) -> wanderingTrade.registerTrades(event));
            });
        }

        this.wanderingTrades.add(new WanderingTrade(pool, ImmutableList.copyOf(itemListings)));
    }

    record VillagerTrade(VillagerProfession profession,
                         VillagerLevel level,
                         Consumer<List<VillagerTrades.ItemListing>> factories) {

        public void registerTrades(VillagerTradesEvent event) {
            if (event.getType().equals(this.profession)) {
                List<VillagerTrades.ItemListing> itemListings = event.getTrades().get(this.level.getLevel());
                if (itemListings != null) {
                    this.factories.accept(itemListings);
                }
            }
        }
    }

    record WanderingTrade(WanderingTradesPool pool, List<VillagerTrades.ItemListing> itemListings) {

        public void registerTrades(WandererTradesEvent event) {
            this.getTradesPool(event, this.pool).addAll(this.itemListings);
        }

        List<VillagerTrades.ItemListing> getTradesPool(WandererTradesEvent event, WanderingTradesPool pool) {
            return switch (pool) {
                case PURCHASES, COMMON_SALES -> event.getGenericTrades();
                case SPECIAL_SALES -> event.getRareTrades();
            };
        }
    }
}
