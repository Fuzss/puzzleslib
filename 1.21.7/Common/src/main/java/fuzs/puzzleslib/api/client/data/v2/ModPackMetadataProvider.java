package fuzs.puzzleslib.api.client.data.v2;

import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.puzzleslib.api.resources.v1.PackResourcesHelper;
import net.minecraft.DetectedVersion;
import net.minecraft.data.PackOutput;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.util.InclusiveRange;

import java.util.Optional;

public final class ModPackMetadataProvider extends PackMetadataGenerator {

    public ModPackMetadataProvider(DataProviderContext context) {
        this(context.getModId(), context.getPackOutput());
    }

    public ModPackMetadataProvider(String modId, PackOutput output) {
        super(output);
        this.add(PackMetadataSection.TYPE,
                new PackMetadataSection(PackResourcesHelper.getPackDescription(modId),
                        DetectedVersion.BUILT_IN.packVersion(PackType.SERVER_DATA),
                        Optional.of(new InclusiveRange<>(0, Integer.MAX_VALUE))));
    }
}
