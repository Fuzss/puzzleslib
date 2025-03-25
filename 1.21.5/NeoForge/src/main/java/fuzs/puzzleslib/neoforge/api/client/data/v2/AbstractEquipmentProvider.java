package fuzs.puzzleslib.neoforge.api.client.data.v2;

import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.JsonCodecProvider;

import java.util.concurrent.CompletableFuture;

public abstract class AbstractEquipmentProvider extends JsonCodecProvider<EquipmentClientInfo> {

    public AbstractEquipmentProvider(DataProviderContext context) {
        this(context.getModId(), context.getPackOutput(), context.getRegistries());
    }

    public AbstractEquipmentProvider(String modId, PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput,
                PackOutput.Target.RESOURCE_PACK,
                "equipment",
                EquipmentClientInfo.CODEC,
                lookupProvider,
                modId);
    }

    @Override
    protected final void gather() {
        this.addEquipment();
    }

    public abstract void addEquipment();

    @Override
    public String getName() {
        return "Equipment";
    }
}
