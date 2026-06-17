package fuzs.puzzleslib.neoforge.impl.client;

import fuzs.puzzleslib.common.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.common.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.common.impl.PuzzlesLib;
import fuzs.puzzleslib.common.impl.PuzzlesLibMod;
import fuzs.puzzleslib.common.impl.client.PuzzlesLibClient;
import fuzs.puzzleslib.common.impl.content.client.PuzzlesLibClientDevelopment;
import fuzs.puzzleslib.neoforge.mixin.client.accessor.BrandingControlAccessor;
import fuzs.puzzleslib.neoforge.mixin.client.accessor.RegisterKeyMappingsEventNeoForgeAccessor;
import net.minecraft.SharedConstants;
import net.minecraft.client.Options;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.i18n.FMLTranslations;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.internal.BrandingControl;

import java.util.Collections;

@Mod(value = PuzzlesLib.MOD_ID, dist = Dist.CLIENT)
public class PuzzlesLibNeoForgeClient {

    public PuzzlesLibNeoForgeClient(ModContainer modContainer) {
        ClientModConstructor.construct(PuzzlesLib.MOD_ID, PuzzlesLibClient::new);
        if (ModLoaderEnvironment.INSTANCE.isDevelopmentEnvironmentWithoutDataGeneration(PuzzlesLib.MOD_ID)) {
            ClientModConstructor.construct(PuzzlesLibMod.id("client/development"), PuzzlesLibClientDevelopment::new);
        }

        registerLoadingHandlers(modContainer.getEventBus());
    }

    private static void registerLoadingHandlers(IEventBus eventBus) {
        if (!ModLoaderEnvironment.INSTANCE.isDevelopmentEnvironmentWithoutDataGeneration(PuzzlesLib.MOD_ID)) {
            return;
        }

        eventBus.addListener((final RegisterKeyMappingsEvent event) -> {
            Options options = ((RegisterKeyMappingsEventNeoForgeAccessor) event).puzzleslib$getOptions();
            // we hijack the event; it fires at the perfect time for us to manipulate game options before the file can be written
            PuzzlesLibClientDevelopment.setupGameOptions(options);
        });
        eventBus.addListener((final AddClientReloadListenersEvent event) -> {
            event.addListener(PuzzlesLibMod.id("branding"),
                    (ResourceManagerReloadListener) (ResourceManager resourceManager) -> {
                        BrandingControlAccessor.puzzleslib$setBrandings(Collections.singletonList(getBrandingMessage()));
                        BrandingControlAccessor.puzzleslib$setOverCopyrightBrandings(Collections.emptyList());
                    });
        });
    }

    private static String getBrandingMessage() {
        return "Minecraft " + SharedConstants.getCurrentVersion().name() + "/" + BrandingControl.BRANDING_NAME
                + getModListMessage();
    }

    private static String getModListMessage() {
        return FMLTranslations.parseMessageWithFallback("fml.menu.branding", () -> {
            return " (" + FMLTranslations.parseMessageWithFallback("fml.menu.loadingmods",
                    () -> "%s mods",
                    ModList.get().size()) + ")";
        }, "", ModList.get().size());
    }
}
