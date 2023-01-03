package fuzs.puzzleslib.impl.client.renderer.item;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import fuzs.puzzleslib.api.client.renderer.item.v1.ItemModelOverrides;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ItemModelOverridesImpl implements ItemModelOverrides {
    private static final Map<Item, ItemModelData> ITEM_MODEL_PROVIDERS = Maps.newIdentityHashMap();

    public static Optional<BakedModel> getModelByType(ItemStack stack, ItemModelShaper itemModelShaper, @Nullable ItemTransforms.TransformType transformType) {
        if (!stack.isEmpty()) {
            ItemModelData itemModelData = ITEM_MODEL_PROVIDERS.get(stack.getItem());
            if (itemModelData != null) {
                ModelResourceLocation modelLocation = itemModelData.getModelLocationByType(transformType);
                return Optional.of(itemModelShaper.getModelManager().getModel(modelLocation));
            }
        }
        return Optional.empty();
    }

    @Override
    public void register(Item item, ModelResourceLocation itemModel, ModelResourceLocation customModel, ItemTransforms.TransformType... itemModelTransforms) {
        // avoid making map concurrent, as synchronization is only necessary during registration and will needlessly slow down item rendering...
        synchronized (ItemModelOverridesImpl.class) {
            if (ITEM_MODEL_PROVIDERS.put(item, new ItemModelData(itemModel, customModel, ImmutableSet.copyOf(itemModelTransforms))) != null) {
                throw new IllegalStateException("Duplicate custom item model provider registered for item %s".formatted(Registry.ITEM.getKey(item)));
            }
        }
    }

    private record ItemModelData(ModelResourceLocation itemModel, ModelResourceLocation customModel, Set<ItemTransforms.TransformType> itemModelTransforms) {

        public ModelResourceLocation getModelLocationByType(@Nullable ItemTransforms.TransformType transformType) {
            if (transformType != null && this.itemModelTransforms.contains(transformType)) {
                return this.itemModel;
            } else {
                return this.customModel;
            }
        }
    }
}
