package fuzs.puzzleslib.api.client.data.v2;

import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import net.minecraft.client.data.models.EquipmentAssetProvider;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.equipment.EquipmentAsset;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public abstract class AbstractEquipmentProvider extends EquipmentAssetProvider {

    public AbstractEquipmentProvider(DataProviderContext context) {
        this(context.getPackOutput());
    }

    public AbstractEquipmentProvider(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cachedOutput) {
        Map<ResourceKey<EquipmentAsset>, EquipmentClientInfo> values = new LinkedHashMap<>();
        this.addEquipmentAssets((ResourceKey<EquipmentAsset> resourceKey, EquipmentClientInfo equipmentClientInfo) -> {
            if (values.putIfAbsent(resourceKey, equipmentClientInfo) != null) {
                throw new IllegalStateException("Tried to register equipment asset twice for id: " + resourceKey);
            }
        });

        return DataProvider.saveAll(cachedOutput, EquipmentClientInfo.CODEC, this.pathProvider::json, values);
    }

    public abstract void addEquipmentAssets(BiConsumer<ResourceKey<EquipmentAsset>, EquipmentClientInfo> equipmentAssetConsumer);

    /**
     * @see EquipmentAssetProvider#onlyHumanoid(String)
     */
    public static EquipmentClientInfo onlyHumanoid(Identifier identifier) {
        return EquipmentClientInfo.builder().addHumanoidLayers(identifier).build();
    }

    /**
     * @see EquipmentAssetProvider#humanoidAndMountArmor(String)
     */
    public static EquipmentClientInfo humanoidAndHorse(Identifier identifier) {
        return EquipmentClientInfo.builder()
                .addHumanoidLayers(identifier)
                .addLayers(EquipmentClientInfo.LayerType.HORSE_BODY,
                        EquipmentClientInfo.Layer.leatherDyeable(identifier, false))
                .build();
    }

    public static EquipmentClientInfo simple(EquipmentClientInfo.LayerType layerType, Identifier identifier) {
        return EquipmentClientInfo.builder().addLayers(layerType, new EquipmentClientInfo.Layer(identifier)).build();
    }
}
