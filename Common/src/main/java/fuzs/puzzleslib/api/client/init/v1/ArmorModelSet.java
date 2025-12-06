package fuzs.puzzleslib.api.client.init.v1;

import com.google.common.collect.ImmutableMap.Builder;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.world.entity.EquipmentSlot;

import java.util.function.Function;

public record ArmorModelSet<T>(T head, T chest, T legs, T feet) {

    public T get(EquipmentSlot slot) {
        return (switch (slot) {
            case HEAD -> this.head;
            case CHEST -> this.chest;
            case LEGS -> this.legs;
            case FEET -> this.feet;
            default -> throw new IllegalStateException("No model for slot: " + slot);
        });
    }

    public <U> ArmorModelSet<U> map(Function<? super T, ? extends U> mapper) {
        return new ArmorModelSet<>(mapper.apply(this.head),
                mapper.apply(this.chest),
                mapper.apply(this.legs),
                mapper.apply(this.feet));
    }

    public void putFrom(ArmorModelSet<LayerDefinition> other, Builder<T, LayerDefinition> builder) {
        builder.put(this.head, other.head);
        builder.put(this.chest, other.chest);
        builder.put(this.legs, other.legs);
        builder.put(this.feet, other.feet);
    }

    public static <M extends HumanoidModel<?>> ArmorModelSet<M> bake(ArmorModelSet<ModelLayerLocation> armorModelSet, EntityModelSet entityModelSet, Function<ModelPart, M> baker) {
        return armorModelSet.map((ModelLayerLocation modelLayerLocation) -> {
            return baker.apply(entityModelSet.bakeLayer(modelLayerLocation));
        });
    }
}
