package fuzs.puzzleslib;

import com.google.common.collect.Maps;
import fuzs.puzzleslib.capability.CapabilityController;
import fuzs.puzzleslib.core.EnvTypeExecutor;
import fuzs.puzzleslib.element.AbstractElement;
import fuzs.puzzleslib.element.ElementRegistry;
import fuzs.puzzleslib.element.side.ISidedElement;
import fuzs.puzzleslib.network.v2.NetworkHandler;
import fuzs.puzzleslib.proxy.ClientProxy;
import fuzs.puzzleslib.proxy.IProxy;
import fuzs.puzzleslib.proxy.ServerProxy;
import fuzs.puzzleslib.recipe.ElementConfigCondition;
import fuzs.puzzleslib.registry.FuelManager;
import fuzs.puzzleslib.registry.v2.RegistryManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
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

import java.util.Map;
import java.util.function.Supplier;

@Mod(PuzzlesLib.MODID)
public class PuzzlesLib {

    public static final String MODID = "puzzleslib";
    public static final String NAME = "Puzzles Lib";
    public static final Logger LOGGER = LogManager.getLogger(PuzzlesLib.NAME);
    /**
     * sided proxy depending on physical side
     */
    @SuppressWarnings("Convert2MethodRef")
    public static final IProxy PROXY = EnvTypeExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());

    /**
     * temporary {@link ElementRegistry} storage for backwards compatibility
     * use {@link #create} instead
     */
    @Deprecated
    private static final Map<String, ElementRegistry> ELEMENT_REGISTRIES = Maps.newHashMap();

    /**
     * add listeners to setup methods
     */
    public PuzzlesLib() {

        this.addListeners(FMLJavaModLoadingContext.get().getModEventBus());
    }

    /**
     * add listeners to setup methods
     */
    private void addListeners(IEventBus bus) {

        bus.addListener((final FMLCommonSetupEvent evt) -> evt.enqueueWork(() -> this.onCommonSetup(evt)));
        bus.addListener((final FMLClientSetupEvent evt) -> evt.enqueueWork(() -> this.onClientSetup(evt)));
        bus.addListener((final FMLDedicatedServerSetupEvent evt) -> evt.enqueueWork(() -> this.onDedicatedServerSetup(evt)));
    }

    private void onCommonSetup(final FMLCommonSetupEvent evt) {

        ElementRegistry.load(evt, ModConfig.Type.COMMON);
    }

    private void onClientSetup(final FMLClientSetupEvent evt) {

        ElementRegistry.load(evt, ModConfig.Type.CLIENT);
    }

    private void onDedicatedServerSetup(final FMLDedicatedServerSetupEvent evt) {

        ElementRegistry.load(evt, ModConfig.Type.SERVER);
    }

    /**
     * set mod to only be required on one side, server or client
     * works like <code>clientSideOnly</code> back in 1.12
     */
    public static void setSideOnly() {

        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (remote, isServer) -> true));
    }

    /**
     * @deprecated renamed to {@link #setSideOnly()}
     */
    @Deprecated
    public static void setSideSideOnly() {

        setSideOnly();
    }

    /**
     * load {@link ElementConfigCondition} in case this element is adding any configurable recipes
     */
    public static void loadRecipeCondition() {

        ElementConfigCondition.loadRecipeCondition();
    }

    /**
     * load {@link ElementConfigCondition} in case this element is adding any configurable recipes
     * use {@link #loadRecipeCondition()} instead
     */
    @Deprecated
    public static void loadConfigCondition() {

        loadRecipeCondition();
    }

    /**
     * @return proxy for getting physical side specific objects
     * @deprecated access field directly
     */
    @Deprecated
    public static IProxy getProxy() {

        return PROXY;
    }

    /**
     * @return registry manager for puzzles lib mods
     */
    public static RegistryManager getRegistryManagerV2() {

        return RegistryManager.INSTANCE;
    }

    /**
     * @return registry manager for puzzles lib mods
     * @deprecated use {@link #getRegistryManagerV2()}
     */
    @Deprecated
    public static fuzs.puzzleslib.registry.RegistryManager getRegistryManager() {

        return fuzs.puzzleslib.registry.RegistryManager.getInstance();
    }

    /**
     * @return fuel manager for puzzles lib mods
     */
    public static FuelManager getFuelManager() {

        return FuelManager.INSTANCE;
    }

    /**
     * @return network handler for puzzles lib mods
     */
    public static NetworkHandler getNetworkHandlerV2() {

        return NetworkHandler.INSTANCE;
    }

    /**
     * @return network handler for puzzles lib mods
     * @deprecated use {@link #getNetworkHandlerV2()}
     */
    @Deprecated
    public static fuzs.puzzleslib.network.NetworkHandler getNetworkHandler() {

        return fuzs.puzzleslib.network.NetworkHandler.getInstance();
    }

    /**
     * @return capability controller for puzzles lib mods
     */
    public static CapabilityController getCapabilityController() {

        return CapabilityController.getInstance();
    }

    /**
     * register an element
     * use {@link #create} instead
     *
     * @param modId parent mod id
     * @param elementName identifier for this element
     * @param supplier supplier for element to be registered
     * @return <code>element</code>
     * @param <T> make sure element also extends ISidedElement
     */
    @Deprecated
    public static <T extends AbstractElement & ISidedElement> AbstractElement register(String modId, String elementName, Supplier<T> supplier) {

        return ELEMENT_REGISTRIES.computeIfAbsent(modId, ElementRegistry::new).register(elementName, supplier);
    }

    /**
     * register an element
     * use {@link #create} instead
     *
     * @param modId parent mod id
     * @param elementName identifier for this element
     * @param supplier supplier for element to be registered
     * @param dist physical side to register on
     * @return <code>element</code>
     * @param <T> make sure element also extends ISidedElement
     */
    @Deprecated
    public static <T extends AbstractElement & ISidedElement> AbstractElement register(String modId, String elementName, Supplier<T> supplier, Dist dist) {

        return ELEMENT_REGISTRIES.computeIfAbsent(modId, ElementRegistry::new).register(elementName, supplier, dist);
    }

    /**
     * create {@link ElementRegistry} with preset mod id
     * @param modId mod id
     * @return {@link ElementRegistry}
     */
    public static ElementRegistry create(String modId) {

        return new ElementRegistry(modId);
    }

    /**
     * load all elements registered during the current mod's construction
     * @param shouldCreateConfig should config files be created
     * @param configSubPath optional config directory inside of main config dir
     */
    public static void setup(boolean shouldCreateConfig, String... configSubPath) {

        ElementRegistry.setup(shouldCreateConfig, false, configSubPath);
    }

    /**
     * load all elements registered during the current mod's construction
     * @param shouldCreateConfig should config files be created
     * @param loadConfigEarly    load configs during construct so they can be used in registry events
     * @param configSubPath optional config directory inside of main config dir
     */
    public static void setup(boolean shouldCreateConfig, boolean loadConfigEarly, String... configSubPath) {

        ElementRegistry.setup(shouldCreateConfig, loadConfigEarly, configSubPath);
    }

}
