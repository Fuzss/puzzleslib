package fuzs.puzzleslib.impl.client.init;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import fuzs.puzzleslib.api.client.init.v1.ItemModelOverrides;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public class ItemModelOverridesImpl implements ItemModelOverrides {
    private static final Map<Item, ItemModelData> ITEM_MODEL_PROVIDERS = Maps.newIdentityHashMap();

    public static Optional<BakedModel> getSpecificModelOverride(ItemModelShaper itemModelShaper, ItemStack stack, ItemTransforms.TransformType transformType) {
        return getModelOverride(itemModelShaper, stack, Minecraft.getInstance().level, null, 0, data -> data.getModelLocationByType(transformType));
    }

    public static Optional<BakedModel> getGenericModelOverride(ItemModelShaper itemModelShaper, ItemStack stack, @Nullable Level level, @Nullable LivingEntity livingEntity, int seed) {
        return getModelOverride(itemModelShaper, stack, level, livingEntity, seed, ItemModelData::customModel);
    }

    public static Optional<BakedModel> getModelOverride(ItemModelShaper itemModelShaper, ItemStack stack, @Nullable Level level, @Nullable LivingEntity livingEntity, int seed, Function<ItemModelData, ModelResourceLocation> modelGetter) {
        return getItemModelData(stack).map(modelGetter)
                .map(itemModelShaper.getModelManager()::getModel)
                .map(model -> resolveVanillaModelOverrides(model, itemModelShaper, stack, level, livingEntity, seed));
    }

    private static Optional<ItemModelData> getItemModelData(ItemStack stack) {
        if (stack.isEmpty()) return Optional.empty();
        return Optional.ofNullable(ITEM_MODEL_PROVIDERS.get(stack.getItem()));
    }

    private static BakedModel resolveVanillaModelOverrides(BakedModel bakedModel, ItemModelShaper itemModelShaper, ItemStack itemStack, @Nullable Level level, @Nullable LivingEntity livingEntity, int seed) {
        ClientLevel clientLevel = level instanceof ClientLevel ? (ClientLevel) level : null;
        BakedModel modelOverride = bakedModel.getOverrides().resolve(bakedModel, itemStack, clientLevel, livingEntity, seed);
        return modelOverride == null ? itemModelShaper.getModelManager().getMissingModel() : modelOverride;
    }

    @Override
    public void register(Item item, ModelResourceLocation itemModel, ModelResourceLocation customModel, ItemTransforms.TransformType... itemModelTransforms) {
        // avoid making map concurrent, as synchronization is only necessary during registration and will needlessly slow down item rendering...
        synchronized (ItemModelOverridesImpl.class) {
            if (ITEM_MODEL_PROVIDERS.put(item, new ItemModelData(itemModel, customModel, ImmutableSet.copyOf(itemModelTransforms))) != null) {
                throw new IllegalStateException("Duplicate custom item model provider registered for item %s".formatted(BuiltInRegistries.ITEM.getKey(item)));
            }
        }
    }

    private record ItemModelData(ModelResourceLocation itemModel, ModelResourceLocation customModel, Set<ItemTransforms.TransformType> itemModelTransforms) {

        @Nullable
        public ModelResourceLocation getModelLocationByType(ItemTransforms.TransformType transformType) {
            return this.itemModelTransforms.contains(transformType) ? this.itemModel : null;
        }
    }
}
