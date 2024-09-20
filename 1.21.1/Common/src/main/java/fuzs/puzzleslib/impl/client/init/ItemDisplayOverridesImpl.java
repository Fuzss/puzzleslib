package fuzs.puzzleslib.impl.client.init;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import fuzs.puzzleslib.api.client.init.v1.ItemModelDisplayOverrides;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class ItemDisplayOverridesImpl<T> implements ItemModelDisplayOverrides {
    private final Map<ModelResourceLocation, Map<ItemDisplayContext, Function<BakedModelResolver, BakedModel>>> overrideLocations = new HashMap<>();

    protected ItemDisplayOverridesImpl() {
        this.registerEventHandlers();
    }

    protected void register(ModelResourceLocation modelResourceLocation, Function<BakedModelResolver, BakedModel> bakedModelGetter, ItemDisplayContext[] itemDisplayContexts) {
        Objects.requireNonNull(modelResourceLocation, "item model is null");
        Preconditions.checkState(itemDisplayContexts.length > 0, "item display contexts is empty");
        Map<ItemDisplayContext, Function<BakedModelResolver, BakedModel>> overrides = this.overrideLocations.computeIfAbsent(
                modelResourceLocation, $ -> new EnumMap<>(ItemDisplayContext.class));
        for (ItemDisplayContext itemDisplayContext : itemDisplayContexts) {
            if (overrides.put(itemDisplayContext, bakedModelGetter) != null) {
                throw new IllegalStateException(
                        "Attempting to register duplicate item model display override for model %s and display context %s".formatted(
                                modelResourceLocation, itemDisplayContext));
            }
        }
    }

    protected Map<T, Map<ItemDisplayContext, BakedModel>> computeOverrideModels(BakedModelResolver modelResolver, BakedModel missingModel) {
        ImmutableMap.Builder<T, Map<ItemDisplayContext, BakedModel>> builder = ImmutableMap.builder();
        for (Map.Entry<ModelResourceLocation, Map<ItemDisplayContext, Function<BakedModelResolver, BakedModel>>> overrideEntry : this.overrideLocations.entrySet()) {
            BakedModel itemModel = modelResolver.getModel(overrideEntry.getKey());
            Preconditions.checkState(itemModel != missingModel, "item model is missing");
            ImmutableMap.Builder<ItemDisplayContext, BakedModel> overrideBuilder = ImmutableMap.builder();
            for (Map.Entry<ItemDisplayContext, Function<BakedModelResolver, BakedModel>> entry : overrideEntry.getValue()
                    .entrySet()) {
                BakedModel overrideModel = entry.getValue().apply(modelResolver);
                Preconditions.checkState(overrideModel != missingModel, "override model is missing");
                overrideBuilder.put(entry.getKey(), overrideModel);
            }
            T overrideModelKey = this.createOverrideModelKey(overrideEntry.getKey(), itemModel);
            builder.put(overrideModelKey, overrideBuilder.build());
        }
        return builder.build();
    }

    protected abstract T createOverrideModelKey(ModelResourceLocation modelResourceLocation, BakedModel itemModel);

    protected abstract void registerEventHandlers();

    protected interface BakedModelResolver {

        BakedModel getModel(ModelResourceLocation modelResourceLocation);

        BakedModel getModel(ResourceLocation resourceLocation);
    }
}
