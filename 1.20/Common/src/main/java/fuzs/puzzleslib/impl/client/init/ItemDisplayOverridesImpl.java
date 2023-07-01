package fuzs.puzzleslib.impl.client.init;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import fuzs.puzzleslib.api.client.init.v1.ItemModelOverrides;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;

import java.util.Map;
import java.util.Objects;

public abstract class ItemDisplayOverridesImpl implements ItemModelOverrides {
    protected final Map<ModelResourceLocation, Map<ItemDisplayContext, ModelResourceLocation>> overrideLocations = Maps.newHashMap();

    @Override
    public final void register(ModelResourceLocation itemModel, ModelResourceLocation itemModelOverride, ItemDisplayContext... contexts) {
        Objects.requireNonNull(itemModel, "item model is null");
        Objects.requireNonNull(itemModelOverride, "item model override is null");
        Preconditions.checkPositionIndex(0, contexts.length - 1, "item display contexts is empty");
        Map<ItemDisplayContext, ModelResourceLocation> overrides = this.overrideLocations.computeIfAbsent(itemModel, $ -> Maps.newEnumMap(ItemDisplayContext.class));
        for (ItemDisplayContext context : contexts) {
            if (overrides.put(context, itemModelOverride) != null) {
                throw new IllegalStateException("Attempting to register duplicate item model display override for model %s and display context %s".formatted(itemModel, context));
            }
        }
    }
}
