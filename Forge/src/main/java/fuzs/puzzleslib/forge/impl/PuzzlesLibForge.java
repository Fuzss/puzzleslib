package fuzs.puzzleslib.forge.impl;

import cpw.mods.jarhandling.SecureJar;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.impl.PuzzlesLibMod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import net.minecraftforge.forgespi.language.IModFileInfo;

import java.net.URI;

@Mod(PuzzlesLib.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class PuzzlesLibForge {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ModConstructor.construct(PuzzlesLib.MOD_ID, PuzzlesLibMod::new);
        for (ModFileInfo modFile : FMLLoader.getLoadingModList().getModFiles()) {
            ModFile file = modFile.getFile();
//            PuzzlesLib.LOGGER.info("mod file {}: mod file info {}, type {}, mod infos {}, file path {}, file name {}, provider {}, loaders {}, secure jar {}", modFile.getMods().get(0).getDisplayName(), file.getModFileInfo(), file.getType(), file.getModInfos(), file.getFilePath(), file.getFileName(), file.getProvider(), file.getLoaders(), file.getSecureJar());
            SecureJar jar = file.getSecureJar();
//            PuzzlesLib.LOGGER.info("mod file {}: primary path {}, module data provider {}, manifest signers {}, providers {}", modFile.getMods().get(0).getDisplayName(), jar.getPrimaryPath(), jar.moduleDataProvider(), jar.getManifestSigners(), jar.getProviders());
            SecureJar.ModuleDataProvider dataProvider = jar.moduleDataProvider();
//            PuzzlesLib.LOGGER.info("mod file {}: descriptor {}, name {}, uri {}, manifest {}", modFile.getMods().get(0).getDisplayName(), dataProvider.descriptor(), dataProvider.name(), dataProvider.uri(), dataProvider.getManifest());
            URI uri = dataProvider.uri();
//            PuzzlesLib.LOGGER.info("mod file {}: fragment {} ({}), path {} ({}), authority {} ({}), host {}, port {}, query {} ({}), user info {} ({}), scheme {}, scheme specification {}, ({})", modFile.getMods().get(0).getDisplayName(), uri.getFragment(), uri.getRawFragment(), uri.getPath(), uri.getRawPath(), uri.getAuthority(), uri.getRawAuthority(), uri.getHost(), uri.getPort(), uri.getQuery(), uri.getRawQuery(), uri.getUserInfo(), uri.getRawUserInfo(), uri.getScheme(), uri.getSchemeSpecificPart(), uri.getRawSchemeSpecificPart());
        }
    }
}
