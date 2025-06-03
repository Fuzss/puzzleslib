package fuzs.puzzleslib.neoforge.impl.client.core.context;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.client.core.v1.context.ItemDecorationsContext;
import fuzs.puzzleslib.api.client.init.v1.DynamicItemDecorator;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.client.event.RegisterItemDecorationsEvent;

import java.util.Objects;

public record ItemDecorationsContextNeoForgeImpl(RegisterItemDecorationsEvent evt) implements ItemDecorationsContext {

    @Override
    public void registerItemDecorator(DynamicItemDecorator decorator, ItemLike... items) {
        Objects.requireNonNull(decorator, "decorator is null");
        Objects.requireNonNull(items, "items is null");
        Preconditions.checkState(items.length > 0, "items is empty");
        for (ItemLike item : items) {
            Objects.requireNonNull(item, "item is null");
            this.evt.register(item, decorator::renderItemDecorations);
        }
    }
}
