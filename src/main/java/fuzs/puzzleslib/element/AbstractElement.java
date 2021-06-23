package fuzs.puzzleslib.element;

import fuzs.puzzleslib.PuzzlesLib;
import fuzs.puzzleslib.config.ConfigManager;
import fuzs.puzzleslib.config.option.ConfigOption;
import fuzs.puzzleslib.config.option.OptionsBuilder;
import fuzs.puzzleslib.element.side.IClientElement;
import fuzs.puzzleslib.element.side.ICommonElement;
import fuzs.puzzleslib.element.side.IServerElement;
import fuzs.puzzleslib.element.side.ISidedElement;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
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
@SuppressWarnings("unused")
public abstract class AbstractElement extends EventListener implements IConfigurableElement, IRegistryElement<AbstractElement> {

    /**
     * registry name of this element
     */
    private ResourceLocation name;
    /**
     * is this element enabled (are events registered)
     * 1 and 0 for enable / disable, -1 for force disable where reloading the config doesn't have any effect
     */
    private int enabled = this.getDefaultState() ? 1 : 0;
    /**
     * all events registered by this element
     */
    private final List<EventStorage<? extends Event>> eventListeners = Lists.newArrayList();
    /**
     * config options for this element
     */
    private final Map<String, ConfigOption<?>> configOptions = Maps.newHashMap();

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

            throw new UnsupportedOperationException("Cannot set name for element: " + "Name already set");
        }

        this.name = name;

        return this;
    }

    @Override
    public final String getDisplayName() {

        return Stream.of(this.getRegistryName().getPath().split("_")).map(StringUtils::capitalize).collect(Collectors.joining(" "));
    }

    @Override
    public boolean getDefaultState() {

        return true;
    }

    /**
     * @return mods whose presence prevent this element from loading
     */
    protected String[] isIncompatibleWith() {

        return new String[0];
    }

    /**
     * @return has an incompatible mod been found
     */
    protected final boolean isIncompatibilityPresent() {

        return Stream.of(this.isIncompatibleWith()).anyMatch(modId -> ModList.get().isLoaded(modId));
    }

    @Override
    public final void setupGeneralConfig(OptionsBuilder builder) {

        builder.define(this.getDisplayName(), this.getDefaultState()).comment(this.getDescription()).sync(this::setEnabled);
    }

    /**
     * build element config and get event listeners
     */
    public final void setup() {

        ISidedElement.setup((ISidedElement) this);
    }

    /**
     * call sided load methods and register Forge events from internal storage
     * no need to check physical side as the setup event won't be called by Forge anyways
     * @param evt setup event this is called from
     */
    public final void load(ParallelDispatchEvent evt) {

        // don't load anything if an incompatible mod is detected
        if (this.isIncompatibilityPresent()) {

            this.enabled = -1;
            return;
        }

        ISidedElement.loadSide((ISidedElement) this, evt);
        if (this instanceof ICommonElement) {

            if (evt instanceof FMLCommonSetupEvent) {

                this.reload(this.isEnabled(), true);
            }
        } else if (this instanceof IClientElement && evt instanceof FMLClientSetupEvent || this instanceof IServerElement && evt instanceof FMLDedicatedServerSetupEvent) {

            this.reload(this.isEnabled(), true);
        }
    }

    /**
     * update status of all reloadable components such as events and everything specified in sided load methods
     * @param firstLoad should unregistering not happen, as nothing has been loaded yet anyways
     */
    private void reload(boolean enabled, boolean firstLoad) {

        if (enabled || this.isAlwaysEnabled()) {

            this.reloadEventListeners(true);
        } else if (!firstLoad) {

            this.reloadEventListeners(false);
            ISidedElement.unload((ISidedElement) this);
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

    @Override
    public final boolean isEnabled() {

        return this.enabled == 1;
    }

    /**
     * are contents from this element always active
     * @return is always enabled
     */
    protected boolean isAlwaysEnabled() {

        return false;
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

            this.reload(enabled == 1, false);
            this.enabled = enabled;
        }
    }

    /**
     * something went wrong using this element, disable until game is restarted
     */
    protected final void setDisabled() {

        this.setEnabled(-1);
        PuzzlesLib.LOGGER.warn("Detected issue in {} element: {}", this.getDisplayName(), "Disabling until game restart");
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

        PuzzlesLib.LOGGER.error("Unable to get option at path \"" + singlePath + "\": " + "Option not found");
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    @Override
    public final <T> Optional<T> getValue(String... path) {

        return (Optional<T>) this.getOption().map(ConfigOption::get);
    }

    public Collection<ConfigOption<?>> getOptions() {

        return this.configOptions.values();
    }

    /**
     * empty element needed for some aspects of {@link ConfigManager}
     * @param name element name
     * @return dummy element
     */
    public static AbstractElement createEmpty(ResourceLocation name) {

        return new EmptyElement(name);
    }

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
