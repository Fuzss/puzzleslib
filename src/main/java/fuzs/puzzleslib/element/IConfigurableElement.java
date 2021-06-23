package fuzs.puzzleslib.element;

import fuzs.puzzleslib.config.option.ConfigOption;
import fuzs.puzzleslib.config.option.OptionsBuilder;

import java.util.Optional;

/**
 * provides features for setting up a {@link AbstractElement} with config options
 */
public interface IConfigurableElement {

    /**
     * @return is the element enabled
     */
    boolean isEnabled();

    /**
     * @return is the element enabled by default
     */
    boolean getDefaultState();

    /**
     * @return name of this element
     */
    String getDisplayName();

    /**
     * @return description for this element
     */
    String[] getDescription();

    /**
     * add an entry for controlling this element in the general config section
     * @param builder active config builder
     */
    void setupGeneralConfig(OptionsBuilder builder);

    /**
     * add config option from inside {@link OptionsBuilder}
     * @param option config option to store
     */
    void addOption(ConfigOption<?> option);

    /**
     * get an option from this element
     * @param path  path to get option at
     * @return      found option or empty
     */
    Optional<ConfigOption<?>> getOption(String... path);

    /**
     * get the value from an option found by {@link #getOption}
     * @param path path to get option at
     * @param <T> type of value
     * @return the option value
     */
    <T> Optional<T> getValue(String... path);

}
