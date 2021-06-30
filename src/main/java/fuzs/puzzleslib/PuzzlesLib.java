package fuzs.puzzleslib;

import fuzs.puzzleslib.capability.CapabilityController;
import fuzs.puzzleslib.element.AbstractElement;
import fuzs.puzzleslib.element.ElementRegistry;
import fuzs.puzzleslib.element.side.ISidedElement;
import fuzs.puzzleslib.network.NetworkHandler;
import fuzs.puzzleslib.proxy.IProxy;
import fuzs.puzzleslib.recipe.ElementConfigCondition;
import fuzs.puzzleslib.registry.RegistryManager;
import fuzs.puzzleslib.util.PuzzlesUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

@Mod(PuzzlesLib.MODID)
public class PuzzlesLib {

    public static final String MODID = "puzzleslib";
    public static final String NAME = "Puzzles Lib";
    public static final Logger LOGGER = LogManager.getLogger(PuzzlesLib.NAME);

    private static IProxy<?> sidedProxy;
    private static RegistryManager registryManager;
    private static NetworkHandler networkHandler;
    private static CapabilityController capabilityController;

    private static boolean isConditionLoaded;

    public PuzzlesLib() {

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onCommonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onServerSetup);
        FMLJavaModLoadingContext.get().getModEventBus().register(getRegistryManager());
    }

    private void onCommonSetup(final FMLCommonSetupEvent evt) {

        evt.enqueueWork(() -> ElementRegistry.load(evt, ModConfig.Type.COMMON));
    }

    private void onClientSetup(final FMLClientSetupEvent evt) {

        evt.enqueueWork(() -> ElementRegistry.load(evt, ModConfig.Type.CLIENT));
    }

    private void onServerSetup(final FMLDedicatedServerSetupEvent evt) {

        evt.enqueueWork(() -> ElementRegistry.load(evt, ModConfig.Type.SERVER));
    }

    /**
     * set mod to only be required on one side, server or client
     * works like <code>clientSideOnly</code> back in 1.12
     */
    public static void setSideSideOnly() {

        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (remote, isServer) -> true));
    }

    /**
     * load {@link ElementConfigCondition} in case this element is adding any configurable recipes
     */
    public static void loadConfigCondition() {

        if (!isConditionLoaded) {

            isConditionLoaded = true;
            CraftingHelper.register(new ElementConfigCondition.Serializer());
        }
    }

    /**
     * @return proxy for getting physical side specific objects
     */
    public static IProxy<?> getProxy() {

        return PuzzlesUtil.getOrElse(sidedProxy, IProxy::getProxy, instance -> sidedProxy = instance);
    }

    /**
     * @return registry manager for puzzles lib mods
     */
    public static RegistryManager getRegistryManager() {

        return PuzzlesUtil.getOrElse(registryManager, RegistryManager::new, instance -> registryManager = instance);
    }

    /**
     * @return network handler for puzzles lib mods
     */
    public static NetworkHandler getNetworkHandler() {

        return PuzzlesUtil.getOrElse(networkHandler, NetworkHandler::new, instance -> networkHandler = instance);
    }

    /**
     * @return capability controller for puzzles lib mods
     */
    public static CapabilityController getCapabilityController() {

        return PuzzlesUtil.getOrElse(capabilityController, CapabilityController::new, instance -> capabilityController = instance);
    }

    /**
     * register an element
     * @param modId parent mod id
     * @param key identifier for this element
     * @param supplier supplier for element to be registered
     * @return <code>element</code>
     * @param <T> make sure element also extends ISidedElement
     */
    public static <T extends AbstractElement & ISidedElement> AbstractElement register(String modId, String key, Supplier<T> supplier) {

        return ElementRegistry.register(new ResourceLocation(modId, key), supplier);
    }

    /**
     * register an element
     * @param modId parent mod id
     * @param key identifier for this element
     * @param supplier supplier for element to be registered
     * @param dist physical side to register on
     * @return <code>element</code>
     * @param <T> make sure element also extends ISidedElement
     */
    public static <T extends AbstractElement & ISidedElement> AbstractElement register(String modId, String key, Supplier<T> supplier, Dist dist) {

        return ElementRegistry.register(new ResourceLocation(modId, key), supplier, dist);
    }

    /**
     * load all elements registered during the current mod's construction
     * @param config should config files be created
     * @param path optional config directory inside of main config dir
     */
    public static void setup(boolean config, String... path) {

        ElementRegistry.setup(config, path);
    }

}
