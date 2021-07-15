package fuzs.puzzleslib.element;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import fuzs.puzzleslib.PuzzlesLib;
import fuzs.puzzleslib.config.ConfigManager;
import fuzs.puzzleslib.config.option.ConfigOption;
import fuzs.puzzleslib.config.option.OptionsBuilder;
import fuzs.puzzleslib.element.side.IClientElement;
import fuzs.puzzleslib.element.side.ICommonElement;
import fuzs.puzzleslib.element.side.IServerElement;
import fuzs.puzzleslib.element.side.ISidedElement;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.ParallelDispatchEvent;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * all features a mod adds are structured into elements which are then registered, this is an abstract version
 */
public abstract class AbstractElement extends EventListener implements IConfigurableElement, IRegistryElement<AbstractElement> {

    /**
     * registry name of this element
     */
    private ResourceLocation name;
    /**
     * is this element enabled (are events registered)
     * 1 and 0 for enable / disable, -1 for force disable where reloading the config doesn't have any effect
     */
    private int enabled = this.isEnabledByDefault() ? 1 : 0;
    /**
     * all events registered by this element
     */
    private final List<EventStorage<? extends Event>> eventListeners = Lists.newArrayList();
    /**
     * config options for this element
     */
    private final Map<String, ConfigOption<?>> configOptions = Maps.newHashMap();
    /**
     * has {@link #load} been called
     */
    private boolean isLoaded;

    @Nonnull
    @Override
    public final ResourceLocation getRegistryName() {

        if (this.name == null) {

            throw new UnsupportedOperationException("Cannot get name for element: " + "Name not set");
        }

        return this.name;
    }

    @Nonnull
    @Override
    public final AbstractElement setRegistryName(@Nonnull ResourceLocation name) {

        if (this.name != null) {

            throw new UnsupportedOperationException("Cannot set name \"" + name + "\" for element: " + "Name already set as \"" + this.name + "\"");
        }

        this.name = name;
        return this;
    }

    @Override
    public final String getDisplayName() {

        return Stream.of(this.getRegistryName().getPath().split("_")).map(StringUtils::capitalize).collect(Collectors.joining(" "));
    }

    @Override
    public boolean isEnabledByDefault() {

        return true;
    }

    /**
     * @return mods whose presence prevent this element from loading
     */
    protected String[] getIncompatibleMods() {

        return new String[0];
    }

    /**
     * @return has an incompatible mod been found
     */
    protected final boolean isIncompatibleModPresent() {

        return Stream.of(this.getIncompatibleMods()).anyMatch(modId -> ModList.get().isLoaded(modId));
    }

    @Override
    public final void setupGeneralConfig(OptionsBuilder builder) {

        // persistent elements cannot be disabled by the user
        if (!this.isPersistent()) {

            builder.define(this.getDisplayName(), this.isEnabledByDefault()).comment(this.getDescription()).sync(this::setEnabled);
        }
    }

    /**
     * build element config and get event listeners
     */
    public final void setup() {

        ISidedElement.runSide((ISidedElement) this, ICommonElement::constructCommon, IClientElement::constructClient, IServerElement::constructServer);
    }

    /**
     * call sided load methods and register Forge events from internal storage
     * no need to check physical side as the setup event won't be called by Forge anyways
     * @param evt setup event this is called from
     */
    public final void load(ParallelDispatchEvent evt) {

        this.isLoaded = true;

        // don't load anything if an incompatible mod is detected
        if (this.isIncompatibleModPresent()) {

            this.enabled = -1;
            return;
        }

        // this is always called, even when the element is disabled
        ISidedElement.loadSide((ISidedElement) this, evt, ICommonElement::setupCommon2, IClientElement::setupClient2, IServerElement::setupServer2);
        if (!this.isEnabled()) {

            return;
        }

        if (evt instanceof FMLCommonSetupEvent) {

            this.reloadEventListeners(true);
        }

        // this is only called when the element is enabled, or later on when it is re-enabled
        ISidedElement.loadSide((ISidedElement) this, evt, ICommonElement::loadCommon, IClientElement::loadClient, IServerElement::loadServer);
    }

    /**
     * update status of all reloadable components such as events and everything specified in sided load methods
     */
    private void reload(boolean enable) {

        if (enable) {

            this.reloadEventListeners(true);
            ISidedElement.runSide((ISidedElement) this, ICommonElement::loadCommon, IClientElement::loadClient, IServerElement::loadServer);
        } else {

            this.reloadEventListeners(false);
            ISidedElement.runSide((ISidedElement) this, ICommonElement::unloadCommon, IClientElement::unloadClient, IServerElement::unloadServer);
        }
    }

    /**
     * update status of all stored events
     * @param enable should events be loaded, otherwise they're unloaded
     */
    private void reloadEventListeners(boolean enable) {

        if (enable) {

            this.getEventListeners().forEach(EventStorage::register);
        } else {

            this.getEventListeners().forEach(EventStorage::unregister);
        }
    }

    /**
     * skip creating option to disable this in general config section
     * {@link #isEnabled()} and {@link #disable()} still work as usual
     * @return is always enabled
     */
    protected boolean isPersistent() {

        return false;
    }

    @Override
    public final boolean isEnabled() {

        return this.enabled == 1;
    }

    /**
     * set {@link #enabled} state, reload when changed
     * @param enabled enabled
     */
    private void setEnabled(boolean enabled) {

        this.setEnabled(enabled ? 1 : 0);
    }

    /**
     * set {@link #enabled} state, reload when changed
     * @param enabled enabled as int
     */
    private void setEnabled(int enabled) {

        if (this.enabled != -1 && this.enabled != enabled) {

            this.enabled = enabled;
            // prevent things from changing due to config reloading when the element hasn't even been loaded yet
            if (this.isLoaded) {

                this.reload(enabled == 1);
            }
        }
    }

    /**
     * something went wrong using this element, disable until game is restarted
     */
    protected final void disable() {

        this.setEnabled(-1);
        PuzzlesLib.LOGGER.warn("Detected issue in {} element: {}", this.getDisplayName(), "Disabling until game restart");
        if (this.isPersistent()) {

            PuzzlesLib.LOGGER.warn("{} is a persistent element", this.getDisplayName());
        }
    }

    @Override
    public final List<EventStorage<? extends Event>> getEventListeners() {

        return this.eventListeners;
    }

    @Override
    public final void addOption(ConfigOption<?> option) {

        this.configOptions.put(String.join(".", option.getPath()), option);
    }

    @Override
    public final Optional<ConfigOption<?>> getOption(String... path) {

        String singlePath = String.join(".", path);
        ConfigOption<?> option = this.configOptions.get(singlePath);
        if (option != null) {

            return Optional.of(option);
        }

        PuzzlesLib.LOGGER.error("Unable to get option at path {}: {}", singlePath, "Option not found");
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    @Override
    public final <T> Optional<T> getValue(String... path) {

        return (Optional<T>) this.getOption().map(ConfigOption::get);
    }

    /**
     * @return config options
     */
    public Collection<ConfigOption<?>> getOptions() {

        return this.configOptions.values();
    }

    /**
     * @return has {@link #load} been called for this
     */
    public boolean isLoaded() {

        return this.isLoaded;
    }

    /**
     * empty element needed for some aspects of {@link ConfigManager}
     * @param name element name
     * @return dummy element
     */
    public static AbstractElement createEmpty(ResourceLocation name) {

        return new EmptyElement(name);
    }

    /**
     * almost an anonymous class, but needs to implement {@link ISidedElement}
     */
    private static class EmptyElement extends AbstractElement implements ISidedElement {

        public EmptyElement(ResourceLocation name) {

            this.setRegistryName(name);
        }

        @Override
        public String[] getDescription() {

            return new String[0];
        }

    }

}
