package fuzs.puzzleslib.forge.impl.client.core.context;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.client.core.v1.context.ItemModelPropertiesContext;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;

import java.util.Objects;

public final class ItemModelPropertiesContextForgeImpl implements ItemModelPropertiesContext {

    @Override
    public void registerGlobalProperty(ResourceLocation identifier, ClampedItemPropertyFunction function) {
        Objects.requireNonNull(identifier, "property name is null");
        Objects.requireNonNull(function, "property function is null");
        ItemProperties.registerGeneric(identifier, function);
    }

    @Override
    public void registerItemProperty(ResourceLocation identifier, ClampedItemPropertyFunction function, ItemLike... items) {
        Objects.requireNonNull(identifier, "property name is null");
        Objects.requireNonNull(function, "property function is null");
        Objects.requireNonNull(items, "items is null");
        Preconditions.checkPositionIndex(1, items.length, "items is empty");
        for (ItemLike item : items) {
            Objects.requireNonNull(item, "items is null");
            ItemProperties.register(item.asItem(), identifier, function);
        }
    }
}
