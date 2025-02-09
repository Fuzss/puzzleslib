package fuzs.puzzleslib.neoforge.impl.client.core.context;

import com.mojang.serialization.MapCodec;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.core.v1.context.ItemModelsContext;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.client.event.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public final class ItemModelsContextNeoForgeImpl implements ItemModelsContext {
    private final List<Consumer<Event>> listeners = new ArrayList<>();

    public static ItemModelsContextNeoForgeImpl computeIfAbsent(ItemModelsContextNeoForgeImpl[] itemModelsContext, ClientModConstructor modConstructor) {
        if (itemModelsContext[0] == null) {
            itemModelsContext[0] = new ItemModelsContextNeoForgeImpl();
            modConstructor.onRegisterItemModels(itemModelsContext[0]);
        }
        return itemModelsContext[0];
    }

    @Override
    public void registerItemModel(ResourceLocation resourceLocation, MapCodec<? extends ItemModel.Unbaked> codec) {
        Objects.requireNonNull(resourceLocation, "resource location is null");
        Objects.requireNonNull(codec, "codec is null");
        this.registerForEvent(RegisterItemModelsEvent.class, (RegisterItemModelsEvent evt) -> {
            evt.register(resourceLocation, codec);
        });
    }

    @Override
    public void registerSpecialModelRenderer(ResourceLocation resourceLocation, MapCodec<? extends SpecialModelRenderer.Unbaked> codec) {
        Objects.requireNonNull(resourceLocation, "resource location is null");
        Objects.requireNonNull(codec, "codec is null");
        this.registerForEvent(RegisterSpecialModelRendererEvent.class, (RegisterSpecialModelRendererEvent evt) -> {
            evt.register(resourceLocation, codec);
        });
    }

    @Override
    public void registerItemTintSource(ResourceLocation resourceLocation, MapCodec<? extends ItemTintSource> codec) {
        Objects.requireNonNull(resourceLocation, "resource location is null");
        Objects.requireNonNull(codec, "codec is null");
        this.registerForEvent(RegisterColorHandlersEvent.ItemTintSources.class,
                (RegisterColorHandlersEvent.ItemTintSources evt) -> {
                    evt.register(resourceLocation, codec);
                });
    }

    @Override
    public void registerSelectItemModelProperty(ResourceLocation resourceLocation, SelectItemModelProperty.Type<?, ?> type) {
        Objects.requireNonNull(resourceLocation, "resource location is null");
        Objects.requireNonNull(type, "type is null");
        this.registerForEvent(RegisterSelectItemModelPropertyEvent.class,
                (RegisterSelectItemModelPropertyEvent evt) -> {
                    evt.register(resourceLocation, type);
                });
    }

    @Override
    public void registerConditionalItemModelProperty(ResourceLocation resourceLocation, MapCodec<? extends ConditionalItemModelProperty> codec) {
        Objects.requireNonNull(resourceLocation, "resource location is null");
        Objects.requireNonNull(codec, "codec is null");
        this.registerForEvent(RegisterConditionalItemModelPropertyEvent.class,
                (RegisterConditionalItemModelPropertyEvent evt) -> {
                    evt.register(resourceLocation, codec);
                });
    }

    @Override
    public void registerRangeSelectItemModelProperty(ResourceLocation resourceLocation, MapCodec<? extends RangeSelectItemModelProperty> codec) {
        Objects.requireNonNull(resourceLocation, "resource location is null");
        Objects.requireNonNull(codec, "codec is null");
        this.registerForEvent(RegisterRangeSelectItemModelPropertyEvent.class,
                (RegisterRangeSelectItemModelPropertyEvent evt) -> {
                    evt.register(resourceLocation, codec);
                });
    }

    private <T extends Event> void registerForEvent(Class<T> eventClazz, Consumer<T> consumer) {
        this.listeners.add((Event event) -> {
            if (eventClazz.isInstance(event)) {
                consumer.accept((T) event);
            }
        });
    }

    public void registerForEvent(Event event) {
        this.listeners.forEach((Consumer<Event> consumer) -> consumer.accept(event));
    }
}
