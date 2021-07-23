package fuzs.puzzleslib;

import com.google.common.collect.Maps;
import fuzs.puzzleslib.capability.CapabilityController;
import fuzs.puzzleslib.element.AbstractElement;
import fuzs.puzzleslib.element.ElementRegistry;
import fuzs.puzzleslib.element.side.ISidedElement;
import fuzs.puzzleslib.network.NetworkHandler;
import fuzs.puzzleslib.proxy.IProxy;
import fuzs.puzzleslib.recipe.ElementConfigCondition;
import fuzs.puzzleslib.registry.RegistryManager;
import fuzs.puzzleslib.util.PuzzlesUtil;
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

import java.util.Map;
import java.util.function.Supplier;

@Mod(PuzzlesLib.MODID)
public class PuzzlesLib {

    public static final String MODID = "puzzleslib";
    public static final String NAME = "Puzzles Lib";
    public static final Logger LOGGER = LogManager.getLogger(PuzzlesLib.NAME);

    /**
     * temporary {@link ElementRegistry} storage for backwards compatibility
     * use {@link #create} instead
     */
    @Deprecated
    private static final Map<String, ElementRegistry> ELEMENT_REGISTRIES = Maps.newHashMap();

    /**
     * sided proxy depending on physical side
     */
    private static IProxy<?> proxy;
    /**
     * has config recipe condition been loaded via {@link #loadRecipeCondition()}
     */
    private static boolean isRecipeConditionLoaded;

    /**
     * add listeners to setup methods
     */
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
     * use {@link #loadRecipeCondition()} instead
     */
    @Deprecated
    public static void loadConfigCondition() {

        loadRecipeCondition();
    }

    /**
     * load {@link ElementConfigCondition} in case this element is adding any configurable recipes
     */
    public static void loadRecipeCondition() {

        if (!isRecipeConditionLoaded) {

            isRecipeConditionLoaded = true;
            CraftingHelper.register(new ElementConfigCondition.Serializer());
        }
    }

    /**
     * @return proxy for getting physical side specific objects
     */
    public static IProxy<?> getProxy() {

        return PuzzlesUtil.getOrElse(proxy, IProxy::getProxy, instance -> proxy = instance);
    }

    /**
     * @return registry manager for puzzles lib mods
     */
    public static RegistryManager getRegistryManager() {

        return RegistryManager.getInstance();
    }

    /**
     * @return network handler for puzzles lib mods
     */
    public static NetworkHandler getNetworkHandler() {

        return NetworkHandler.getInstance();
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
