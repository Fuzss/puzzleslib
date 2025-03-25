package fuzs.puzzleslib.neoforge.api.client.data.v2;

import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.*;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.neoforged.fml.ModList;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

@Deprecated(forRemoval = true)
public final class ExistingFilesHelper {

    private ExistingFilesHelper() {
        // NO-OP
    }

    public static CloseableResourceManager createResourceManager(String modId) {
        List<PackResources> packResources = new ArrayList<>();
        packResources.add(new VanillaPackResourcesBuilder().exposeNamespace("minecraft")
                .pushJarResources()
                .build(createPackInfo("vanilla")));
        packResources.add(new PathPackResources(createPackInfo(modId),
                ModList.get().getModFileById(modId).getFile().getSecureJar().getRootPath()));
        return new MultiPackResourceManager(PackType.CLIENT_RESOURCES, packResources);
    }

    private static PackLocationInfo createPackInfo(String modId) {
        Component component = Component.literal(capitalizeFully(modId));
        return new PackLocationInfo(modId, component, PackSource.BUILT_IN, Optional.empty());
    }

    public static String capitalizeFully(String s) {
        s = s.replaceAll("\\W+", " ").replace('_', ' ');
        StringJoiner stringJoiner = new StringJoiner(" ");
        for (String string : s.split("\\s+")) {
            if (!string.isEmpty()) {
                stringJoiner.add(Character.toUpperCase(string.charAt(0)) + string.substring(1));
            }
        }
        return stringJoiner.toString();
    }
}
