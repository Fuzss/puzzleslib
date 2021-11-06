package fuzs.puzzleslib;

import fuzs.puzzleslib.core.EnvTypeExecutor;
import fuzs.puzzleslib.network.NetworkHandler;
import fuzs.puzzleslib.proxy.ClientProxy;
import fuzs.puzzleslib.proxy.IProxy;
import fuzs.puzzleslib.proxy.ServerProxy;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fmllegacy.network.FMLNetworkConstants;

/**
 * more of a utility class for mods using this library, main puzzles lib mod class is at {@link fuzs.puzzleslib.core.PuzzlesLibMod}
 */
public class PuzzlesLib {
    /**
     * sided proxy depending on physical side
     */
    @SuppressWarnings("Convert2MethodRef")
    public static final IProxy PROXY = EnvTypeExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());

    /**
     * set mod to only be required on one side, server or client
     * works like <code>clientSideOnly</code> back in 1.12
     */
    public static void setSideOnly() {
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> FMLNetworkConstants.IGNORESERVERONLY, (remote, server) -> true));
    }

    /**
     * @return network handler for puzzles lib mods
     */
    public static NetworkHandler getNetworkHandler() {
        return NetworkHandler.INSTANCE;
    }
}
