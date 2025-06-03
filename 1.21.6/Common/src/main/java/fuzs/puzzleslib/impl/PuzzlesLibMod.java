package fuzs.puzzleslib.impl;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.core.v1.context.PayloadTypesContext;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import fuzs.puzzleslib.api.event.v1.LoadCompleteCallback;
import fuzs.puzzleslib.api.event.v1.server.RegisterConfigurationTasksCallback;
import fuzs.puzzleslib.impl.core.ClientboundModListMessage;
import fuzs.puzzleslib.impl.core.EventHandlerProvider;
import fuzs.puzzleslib.impl.core.ModContext;
import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import fuzs.puzzleslib.impl.event.core.EventInvokerImpl;
import net.minecraft.resources.ResourceLocation;

/**
 * This has been separated from {@link PuzzlesLib} to prevent issues with static initialization when accessing constants
 * in {@link PuzzlesLib} early.
 */
public class PuzzlesLibMod extends PuzzlesLib implements ModConstructor {

    @Override
    public void onConstructMod() {
        registerEventHandlers();
    }

    private static void registerEventHandlers() {
        RegisterConfigurationTasksCallback.EVENT.register(ModContext::onRegisterConfigurationTasks);
        LoadCompleteCallback.EVENT.register(ModContext::onLoadComplete);
        LoadCompleteCallback.EVENT.register(EventInvokerImpl::initialize);
        EventHandlerProvider.tryRegister(ProxyImpl.get());
    }

    @Override
    public void onRegisterPayloadTypes(PayloadTypesContext context) {
        context.optional();
        context.configurationToClient(ClientboundModListMessage.class, ClientboundModListMessage.STREAM_CODEC);
    }

    public static ResourceLocation id(String path) {
        return ResourceLocationHelper.fromNamespaceAndPath(MOD_ID, path);
    }
}
