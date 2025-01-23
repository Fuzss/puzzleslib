package fuzs.puzzleslib.neoforge.api.data.v2.client;

import fuzs.puzzleslib.neoforge.api.data.v2.core.NeoForgeDataProviderContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.equipment.EquipmentModel;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.JsonCodecProvider;

import java.util.concurrent.CompletableFuture;

/**
 * TODO rename to {@code AbstractEquipmentProvider} and also rename {@link #addEquipmentModels()}
 */
public abstract class AbstractEquipmentModelProvider extends JsonCodecProvider<EquipmentModel> {

    public AbstractEquipmentModelProvider(NeoForgeDataProviderContext context) {
        this(context.getModId(), context.getPackOutput(), context.getRegistries(), context.getFileHelper());
    }

    public AbstractEquipmentModelProvider(String modId, PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper fileHelper) {
        super(packOutput,
                PackOutput.Target.RESOURCE_PACK,
                "models/equipment",
                PackType.CLIENT_RESOURCES,
                EquipmentModel.CODEC,
                lookupProvider,
                modId,
                fileHelper);
    }

    @Override
    protected final void gather() {
        this.addEquipmentModels();
    }

    public abstract void addEquipmentModels();

    @Override
    public String getName() {
        return "Equipment Models";
    }
}
