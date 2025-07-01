package fuzs.puzzleslib.neoforge.impl;

import fuzs.puzzleslib.api.client.data.v2.ModPackMetadataProvider;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.impl.PuzzlesLibMod;
import fuzs.puzzleslib.impl.content.PuzzlesLibDevelopment;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import net.neoforged.fml.common.Mod;

@Mod(PuzzlesLib.MOD_ID)
public class PuzzlesLibNeoForge {

    public PuzzlesLibNeoForge() {
        ModConstructor.construct(PuzzlesLib.MOD_ID, PuzzlesLibMod::new);
        DataProviderHelper.registerDataProviders(PuzzlesLib.MOD_ID, ModPackMetadataProvider::new);
        if (ModLoaderEnvironment.INSTANCE.isDevelopmentEnvironmentWithoutDataGeneration(PuzzlesLib.MOD_ID)) {
            ModConstructor.construct(PuzzlesLibMod.id("common/development"), PuzzlesLibDevelopment::new);
        }
    }
}
