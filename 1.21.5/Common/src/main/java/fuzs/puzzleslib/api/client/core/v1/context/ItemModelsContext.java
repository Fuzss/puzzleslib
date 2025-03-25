package fuzs.puzzleslib.api.client.core.v1.context;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.ResourceLocation;

/**
 * Register codecs for handling custom item model types and properties.
 */
public interface ItemModelsContext {

    /**
     * Register a codec for a custom {@link ItemModel.Unbaked} type.
     *
     * @param resourceLocation the resource location
     * @param codec            the corresponding codec for the type
     */
    void registerItemModel(ResourceLocation resourceLocation, MapCodec<? extends ItemModel.Unbaked> codec);

    /**
     * Register a codec for a custom {@link SpecialModelRenderer.Unbaked} type.
     *
     * @param resourceLocation the resource location
     * @param codec            the corresponding codec for the type
     */
    void registerSpecialModelRenderer(ResourceLocation resourceLocation, MapCodec<? extends SpecialModelRenderer.Unbaked> codec);

    /**
     * Register a codec for a custom {@link ItemTintSource} type.
     *
     * @param resourceLocation the resource location
     * @param codec            the corresponding codec for the type
     */
    void registerItemTintSource(ResourceLocation resourceLocation, MapCodec<? extends ItemTintSource> codec);

    /**
     * Register a type for a custom {@link SelectItemModelProperty} implementation.
     *
     * @param resourceLocation the resource location
     * @param type             the corresponding codec for the type
     */
    void registerSelectItemModelProperty(ResourceLocation resourceLocation, SelectItemModelProperty.Type<?, ?> type);

    /**
     * Register a codec for a custom {@link ConditionalItemModelProperty} type.
     *
     * @param resourceLocation the resource location
     * @param codec            the corresponding codec for the type
     */
    void registerConditionalItemModelProperty(ResourceLocation resourceLocation, MapCodec<? extends ConditionalItemModelProperty> codec);

    /**
     * Register a codec for a custom {@link RangeSelectItemModelProperty} type.
     *
     * @param resourceLocation the resource location
     * @param codec            the corresponding codec for the type
     */
    void registerRangeSelectItemModelProperty(ResourceLocation resourceLocation, MapCodec<? extends RangeSelectItemModelProperty> codec);
}
