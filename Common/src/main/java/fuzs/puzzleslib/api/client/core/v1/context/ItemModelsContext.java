package fuzs.puzzleslib.api.client.core.v1.context;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.Identifier;

/**
 * Register codecs for handling custom item model types and properties.
 */
public interface ItemModelsContext {

    /**
     * Register a codec for a custom {@link ItemModel.Unbaked} type.
     *
     * @param identifier the identifier
     * @param codec            the corresponding codec for the type
     */
    void registerItemModel(Identifier identifier, MapCodec<? extends ItemModel.Unbaked> codec);

    /**
     * Register a codec for a custom {@link SpecialModelRenderer.Unbaked} type.
     *
     * @param identifier the identifier
     * @param codec            the corresponding codec for the type
     */
    void registerSpecialModelRenderer(Identifier identifier, MapCodec<? extends SpecialModelRenderer.Unbaked> codec);

    /**
     * Register a codec for a custom {@link ItemTintSource} type.
     *
     * @param identifier the identifier
     * @param codec            the corresponding codec for the type
     */
    void registerItemTintSource(Identifier identifier, MapCodec<? extends ItemTintSource> codec);

    /**
     * Register a type for a custom {@link SelectItemModelProperty} implementation.
     *
     * @param identifier the identifier
     * @param type             the corresponding codec for the type
     */
    void registerSelectItemModelProperty(Identifier identifier, SelectItemModelProperty.Type<?, ?> type);

    /**
     * Register a codec for a custom {@link ConditionalItemModelProperty} type.
     *
     * @param identifier the identifier
     * @param codec            the corresponding codec for the type
     */
    void registerConditionalItemModelProperty(Identifier identifier, MapCodec<? extends ConditionalItemModelProperty> codec);

    /**
     * Register a codec for a custom {@link RangeSelectItemModelProperty} type.
     *
     * @param identifier the identifier
     * @param codec            the corresponding codec for the type
     */
    void registerRangeSelectItemModelProperty(Identifier identifier, MapCodec<? extends RangeSelectItemModelProperty> codec);
}
