package fuzs.puzzleslib.fabric.impl.core.context;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.core.v1.context.VillagerTradesContext;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public final class VillagerTradesContextFabricImpl implements VillagerTradesContext {

    @Override
    public void registerVillagerTrades(ResourceKey<VillagerProfession> profession, VillagerLevel level, Consumer<List<VillagerTrades.ItemListing>> factories) {
        Objects.requireNonNull(profession, "profession is null");
        Objects.requireNonNull(level, "level is null");
        Objects.requireNonNull(factories, "factories is null");
        TradeOfferHelper.registerVillagerOffers(profession, level.getLevel(), factories);
    }

    @Override
    public void registerWanderingTrades(WanderingTradesPool pool, VillagerTrades.ItemListing... itemListings) {
        Objects.requireNonNull(pool, "pool is null");
        Objects.requireNonNull(itemListings, "item listings is null");
        Preconditions.checkArgument(itemListings.length > 0, "item listings is empty");
        TradeOfferHelper.registerWanderingTraderOffers((TradeOfferHelper.WanderingTraderOffersBuilder builder) -> {
            ResourceLocation resourceLocation = this.getTradesPool(pool);
            builder.addOffersToPool(resourceLocation, itemListings);
        });
    }

    private ResourceLocation getTradesPool(WanderingTradesPool pool) {
        return switch (pool) {
            case PURCHASES -> TradeOfferHelper.WanderingTraderOffersBuilder.BUY_ITEMS_POOL;
            case COMMON_SALES -> TradeOfferHelper.WanderingTraderOffersBuilder.SELL_COMMON_ITEMS_POOL;
            case SPECIAL_SALES -> TradeOfferHelper.WanderingTraderOffersBuilder.SELL_SPECIAL_ITEMS_POOL;
        };
    }
}
