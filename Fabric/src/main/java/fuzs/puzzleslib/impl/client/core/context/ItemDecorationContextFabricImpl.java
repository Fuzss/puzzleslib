package fuzs.puzzleslib.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.ItemDecorationContext;
import fuzs.puzzleslib.api.client.event.v1.ItemDecoratorRegistry;
import fuzs.puzzleslib.api.client.init.v1.DynamicItemDecorator;
import fuzs.puzzleslib.api.core.v1.context.MultiRegistrationContext;
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
