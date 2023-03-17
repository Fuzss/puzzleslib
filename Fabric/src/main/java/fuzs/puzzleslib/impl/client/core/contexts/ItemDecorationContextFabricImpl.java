package fuzs.puzzleslib.impl.client.core.contexts;

import fuzs.puzzleslib.api.client.core.v1.contexts.ItemDecorationContext;
import fuzs.puzzleslib.api.client.events.v2.ItemDecoratorRegistry;
import fuzs.puzzleslib.api.client.registration.v1.DynamicItemDecorator;
import fuzs.puzzleslib.api.core.v1.contexts.MultiRegistrationContext;
import net.minecraft.world.level.ItemLike;

public final class ItemDecorationContextFabricImpl implements ItemDecorationContext, MultiRegistrationContext<ItemLike, DynamicItemDecorator> {

    @Override
    public void registerItemDecorator(DynamicItemDecorator decorator, ItemLike object, ItemLike... objects) {
        this.register(decorator, object, objects);
    }

    @Override
    public void register(ItemLike object, DynamicItemDecorator type) {
        ItemDecoratorRegistry.INSTANCE.register(object, type);
    }
}
