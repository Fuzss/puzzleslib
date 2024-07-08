package fuzs.puzzleslib.impl.resources;

import fuzs.puzzleslib.api.resources.v1.AbstractModPackResources;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.BuiltInMetadata;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;

import java.util.Optional;

public record ModPackResourcesSupplier(PackType packType,
                                       PackLocationInfo info,
                                       PackResourcesSupplier<AbstractModPackResources> supplier,
                                       BuiltInMetadata metadata) implements Pack.ResourcesSupplier {

    public static ModPackResourcesSupplier create(PackType packType, PackLocationInfo info, PackResourcesSupplier<AbstractModPackResources> supplier, Component description) {
        PackMetadataSection metadataSection = new PackMetadataSection(description,
                SharedConstants.getCurrentVersion().getPackVersion(packType),
                Optional.empty()
        );
        return new ModPackResourcesSupplier(packType,
                info,
                supplier,
                BuiltInMetadata.of(PackMetadataSection.TYPE, metadataSection)
        );
    }

    @Override
    public PackResources openPrimary(PackLocationInfo info) {
        return this.getAndSetupPackResources();
    }

    @Override
    public PackResources openFull(PackLocationInfo info, Pack.Metadata packMetadata) {
        return this.getAndSetupPackResources();
    }

    private AbstractModPackResources getAndSetupPackResources() {
        return this.supplier.apply(this.packType, this.info, this.metadata);
    }

    @FunctionalInterface
    public interface PackResourcesSupplier<T extends PackResources> {

        T apply(PackType packType, PackLocationInfo info, BuiltInMetadata metadata);
    }
}
