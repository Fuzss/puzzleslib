package fuzs.puzzleslib.impl.client.core.contexts;

import fuzs.puzzleslib.api.client.core.v1.contexts.ItemModelPropertiesContext;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;

import java.util.Objects;

public final class ItemModelPropertiesContextFabricImpl implements ItemModelPropertiesContext {

    @Override
    public void registerGlobalProperty(ResourceLocation identifier, ClampedItemPropertyFunction function) {
        Objects.requireNonNull(identifier, "property name is null");
        Objects.requireNonNull(function, "property function is null");
        ItemProperties.registerGeneric(identifier, function);
    }

    @Override
    public void registerItemProperty(ResourceLocation identifier, ClampedItemPropertyFunction function, ItemLike object, ItemLike... objects) {
        Objects.requireNonNull(identifier, "property name is null");
        Objects.requireNonNull(function, "property function is null");
        Objects.requireNonNull(object, "item is null");
        ItemProperties.register(object.asItem(), identifier, function);
        Objects.requireNonNull(objects, "items is null");
        for (ItemLike item : objects) {
            Objects.requireNonNull(item, "item is null");
            ItemProperties.register(item.asItem(), identifier, function);
        }
    }
}
