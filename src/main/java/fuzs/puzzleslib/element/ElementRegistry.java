package fuzs.puzzleslib.element;

import com.google.common.collect.*;
import fuzs.puzzleslib.PuzzlesLib;
import fuzs.puzzleslib.config.ConfigManager;
import fuzs.puzzleslib.element.side.IClientElement;
import fuzs.puzzleslib.element.side.ICommonElement;
import fuzs.puzzleslib.element.side.IServerElement;
import fuzs.puzzleslib.element.side.ISidedElement;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.ParallelDispatchEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * registry for elements
 */
@SuppressWarnings("unused")
public class ElementRegistry {

    /**
     * general storage for elements of all mods for performing actions on all of them
     */
    private static final BiMap<ResourceLocation, AbstractElement> LOADED_ELEMENTS = HashBiMap.create();
    /**
     * all elements belonging to the active mod, will be cleared after those elements have been added to {@link #LOADED_ELEMENTS}
     * use tree map for alphabetical sorting cause why not
     */
    private static final TreeMap<ResourceLocation, AbstractElement> REGISTERED_ELEMENTS = Maps.newTreeMap();

    /**
     * register an element
     * @param elementName identifier for this element
     * @param supplier supplier for element to be registered
     * @return <code>element</code>
     * @param <T> make sure element also extends ISidedElement
     */
    public static <T extends AbstractElement & ISidedElement> AbstractElement register(ResourceLocation elementName, Supplier<T> supplier) {

        return register(elementName, supplier, FMLEnvironment.dist);
    }

    /**
     * register an element
     * @param elementName identifier for this element
     * @param supplier supplier for element to be registered
     * @param dist physical side to register on
     * @return <code>element</code>
     * @param <T> make sure element also extends ISidedElement
     */
    @Nullable
    public static <T extends AbstractElement & ISidedElement> AbstractElement register(ResourceLocation elementName, Supplier<T> supplier, Dist dist) {

        if (dist == FMLEnvironment.dist) {

            AbstractElement element = supplier.get();

            assert element instanceof ICommonElement || FMLEnvironment.dist.isClient() || element instanceof IServerElement : "Unable to register element: " + "Trying to register client element for server side";
            assert element instanceof ICommonElement || FMLEnvironment.dist.isDedicatedServer() || element instanceof IClientElement : "Unable to register element: " + "Trying to register server element for client side";

            REGISTERED_ELEMENTS.put(elementName, element);
            return element;
        }

        return null;
    }

    /**
     * get an element from another mod which uses this registry
     * @param namespace namespace of owning mod
     * @param key key for element to get
     * @return optional element
     */
    public static Optional<AbstractElement> get(String namespace, String key) {

        return get(new ResourceLocation(namespace, key));
    }

    /**
     * get an element from another mod which uses this registry
     * @param name name of element to get
     * @return optional element
     */
    public static Optional<AbstractElement> get(ResourceLocation name) {

        return Optional.ofNullable(LOADED_ELEMENTS.get(name));
    }

    /**
     * @param namespace modid to get elements for
     * @return elements for <code>namespace</code> as set
     */
    public static Set<AbstractElement> getAllElements(String namespace) {

        return LOADED_ELEMENTS.entrySet().stream()
                .filter(entry -> entry.getKey().getNamespace().equals(namespace))
                .map(Map.Entry::getValue)
                .collect(Collectors.toSet());
    }

    /**
     * to be used by other mods using this library
     * @param namespace namespace of owning mod
     * @param key key for element to get
     * @param path path for config value
     * @return the config value
     * @param <T> type of config option
     */
    public static <T> Optional<T> getConfigValue(String namespace, String key, String... path) {

        return getConfigValue(new ResourceLocation(namespace, key));
    }

    /**
     * to be used by other mods using this library
     * @param name name of element to get
     * @param path path for config value
     * @return the config value
     * @param <T> type of config option
     */
    public static <T> Optional<T> getConfigValue(ResourceLocation name, String... path) {

        Optional<AbstractElement> element = get(name);
        if (element.isPresent()) {

            return getConfigValue(element.get(), path);
        }

        PuzzlesLib.LOGGER.error("Unable to get config value: " + "Invalid element name");
        return Optional.empty();
    }

    /**
     * to be used from inside of this mod
     * @param element element to get value from
     * @param path path for config value
     * @return the config value
     * @param <T> type of config option
     */
    public static <T> Optional<T> getConfigValue(AbstractElement element, String... path) {

        return element.getValue(path);
    }

    /**
     * generate general config section for controlling elements, setup individual config sections and collect events to be registered in {@link #load}
     * @param config should config files be created
     * @param path optional config directory inside of main config dir
     */
    public static void setup(boolean config, String... path) {

        String activeNamespace = ModLoadingContext.get().getActiveNamespace();
        Set<AbstractElement> activeElements = Sets.newHashSet();
        for (Map.Entry<ResourceLocation, AbstractElement> entry : REGISTERED_ELEMENTS.entrySet()) {

            ResourceLocation elementName = entry.getKey();
            if (elementName.getNamespace().equals(activeNamespace)) {

                AbstractElement element = entry.getValue();
                LOADED_ELEMENTS.put(elementName, element.setRegistryName(elementName));
                activeElements.add(element);
            }
        }

        if (config) {

            // create dummy element for general config section
            AbstractElement generalElement = AbstractElement.createEmpty(new ResourceLocation(activeNamespace, "general"));
            ConfigManager.load(generalElement, ImmutableSet.copyOf(activeElements), type -> ConfigManager.getFileName(activeNamespace, type, path));
            // add general option to storage so it can be reloaded during load phase
            LOADED_ELEMENTS.put(generalElement.getRegistryName(), generalElement);
        }

        activeElements.forEach(AbstractElement::setup);
    }

    /**
     * execute load for common and both sides, also register events
     * which sided elements to load is defined by provided event instance
     * loads all elements, no matter which mod they're from
     * @param evt event type
     * @param syncType config option type to sync
     */
    public static void load(ParallelDispatchEvent evt, ModConfig.Type syncType) {

        REGISTERED_ELEMENTS.clear();
        Set<AbstractElement> elements = LOADED_ELEMENTS.values();
        ConfigManager.syncOptions(elements, syncType);
        elements.forEach(element -> element.load(evt));
    }

}
