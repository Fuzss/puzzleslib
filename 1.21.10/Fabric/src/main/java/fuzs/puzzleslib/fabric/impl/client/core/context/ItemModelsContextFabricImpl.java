package fuzs.puzzleslib.fabric.impl.client.core.context;

import com.mojang.serialization.MapCodec;
import fuzs.puzzleslib.api.client.core.v1.context.ItemModelsContext;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.color.item.ItemTintSources;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModels;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperties;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperties;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperties;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public final class ItemModelsContextFabricImpl implements ItemModelsContext {

    @Override
    public void registerItemModel(ResourceLocation resourceLocation, MapCodec<? extends ItemModel.Unbaked> codec) {
        Objects.requireNonNull(resourceLocation, "resource location is null");
        Objects.requireNonNull(codec, "codec is null");
        ItemModels.ID_MAPPER.put(resourceLocation, codec);
    }

    @Override
    public void registerSpecialModelRenderer(ResourceLocation resourceLocation, MapCodec<? extends SpecialModelRenderer.Unbaked> codec) {
        Objects.requireNonNull(resourceLocation, "resource location is null");
        Objects.requireNonNull(codec, "codec is null");
        SpecialModelRenderers.ID_MAPPER.put(resourceLocation, codec);
    }

    @Override
    public void registerItemTintSource(ResourceLocation resourceLocation, MapCodec<? extends ItemTintSource> codec) {
        Objects.requireNonNull(resourceLocation, "resource location is null");
        Objects.requireNonNull(codec, "codec is null");
        ItemTintSources.ID_MAPPER.put(resourceLocation, codec);
    }

    @Override
    public void registerSelectItemModelProperty(ResourceLocation resourceLocation, SelectItemModelProperty.Type<?, ?> type) {
        Objects.requireNonNull(resourceLocation, "resource location is null");
        Objects.requireNonNull(type, "type is null");
        SelectItemModelProperties.ID_MAPPER.put(resourceLocation, type);
    }

    @Override
    public void registerConditionalItemModelProperty(ResourceLocation resourceLocation, MapCodec<? extends ConditionalItemModelProperty> codec) {
        Objects.requireNonNull(resourceLocation, "resource location is null");
        Objects.requireNonNull(codec, "codec is null");
        ConditionalItemModelProperties.ID_MAPPER.put(resourceLocation, codec);
    }

    @Override
    public void registerRangeSelectItemModelProperty(ResourceLocation resourceLocation, MapCodec<? extends RangeSelectItemModelProperty> codec) {
        Objects.requireNonNull(resourceLocation, "resource location is null");
        Objects.requireNonNull(codec, "codec is null");
        RangeSelectItemModelProperties.ID_MAPPER.put(resourceLocation, codec);
    }
}
