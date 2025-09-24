package fuzs.puzzleslib.api.data.v2;

import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.puzzleslib.api.resources.v1.PackResourcesHelper;
import net.minecraft.DetectedVersion;
import net.minecraft.data.PackOutput;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.util.InclusiveRange;

public final class ModPackMetadataProvider extends PackMetadataGenerator {

    public ModPackMetadataProvider(DataProviderContext context) {
        this(PackType.SERVER_DATA, context);
    }

    public ModPackMetadataProvider(PackType packType, DataProviderContext context) {
        this(packType, context.getModId(), context.getPackOutput());
    }

    public ModPackMetadataProvider(String modId, PackOutput packOutput) {
        this(PackType.SERVER_DATA, modId, packOutput);
    }

    public ModPackMetadataProvider(PackType packType, String modId, PackOutput packOutput) {
        super(packOutput);
        Component component = PackResourcesHelper.getPackDescription(modId);
        this.add(PackMetadataSection.forPackType(packType),
                new PackMetadataSection(component,
                        new InclusiveRange<>(DetectedVersion.BUILT_IN.packVersion(packType))));
    }
}
