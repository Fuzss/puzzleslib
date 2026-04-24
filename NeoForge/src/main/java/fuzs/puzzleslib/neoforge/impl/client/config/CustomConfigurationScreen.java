package fuzs.puzzleslib.neoforge.impl.client.config;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.google.common.collect.ImmutableSet;
import fuzs.puzzleslib.common.api.core.v1.ModContainer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.config.ModConfigs;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.logging.log4j.util.Strings;
import org.jspecify.annotations.Nullable;

import java.util.*;

/**
 * An extension of {@link ConfigurationScreen} that replaces translated components with literals.
 * <p>
 * It also allows for displaying configs from multiple mods in a single selection menu.
 * <p>
 * This class is synced between Fabric &amp; NeoForge, as there is no shared config library in Common to build against.
 */
public class CustomConfigurationScreen extends OptionsSubScreen {
    /**
     * Show server configs last; all else are global configs that are merged under the same section header.
     */
    private static final List<ModConfig.Type> CONFIG_TYPE_DISPLAY_ORDER = List.of(ModConfig.Type.STARTUP,
            ModConfig.Type.COMMON,
            ModConfig.Type.CLIENT,
            ModConfig.Type.SERVER);
    private static final Component EDIT_COMPONENT = Component.literal("Edit").append(CommonComponents.ELLIPSIS);
    /**
     * @see ConfigurationScreen#LANG_PREFIX
     */
    private static final String LANG_PREFIX = "neoforge.configuration.uitext.";
    /**
     * @see ConfigurationScreen#CRUMB
     */
    private static final String CRUMB = LANG_PREFIX + "breadcrumb.order";

    protected final Set<String> modIds;

    public CustomConfigurationScreen(String modId, Screen lastScreen) {
        this(Collections.singleton(modId), lastScreen, getConfigTitleComponent(modId));
    }

    public CustomConfigurationScreen(String modId, Screen lastScreen, String... modIds) {
        this(ImmutableSet.<String>builder().add(modId).add(modIds).build(), lastScreen, getConfigTitleComponent(modId));
    }

    public CustomConfigurationScreen(Set<String> modIds, Screen lastScreen, Component title) {
        super(lastScreen, Minecraft.getInstance().options, title);
        this.modIds = modIds;
    }

    @Override
    protected void addOptions() {
        boolean hasSectionHeader = false;
        List<ModConfig> modConfigs = new ArrayList<>();
        for (ModConfig.Type type : CONFIG_TYPE_DISPLAY_ORDER) {
            // We combine all global config types under the same header, while only server configs get their own.
            if (type == ModConfig.Type.SERVER) {
                hasSectionHeader = false;
            }

            for (ModConfig modConfig : ModConfigs.getConfigSet(type)) {
                if (this.modIds.contains(modConfig.getModId())) {
                    if (!hasSectionHeader) {
                        // This tricks the string widget into centering the text, while also allowing it to scroll instead of overflowing.
                        this.list.addSmall(new StringWidget(ConfigurationScreen.BIG_BUTTON_WIDTH,
                                Button.DEFAULT_HEIGHT,
                                getConfigSectionComponent(type),
                                this.font) {
                            @Override
                            public int getWidth() {
                                return this.width;
                            }
                        }.setMaxWidth(1, StringWidget.TextOverflow.SCROLLING), null);
                        hasSectionHeader = true;
                    }

                    Button button = Button.builder(Component.literal(modConfig.getFileName()), (Button buttonX) -> {
                        this.openModConfigScreen(modConfig, this);
                    }).width(ConfigurationScreen.BIG_BUTTON_WIDTH).build();
                    Component tooltip = this.getTooltipComponent(type, modConfig);
                    if (tooltip != null) {
                        button.setTooltip(Tooltip.create(tooltip));
                        button.active = false;
                    }

                    this.list.addSmall(button, null);
                    modConfigs.add(modConfig);
                }
            }
        }

        // When there is only one config, go to that config screen directly.
        if (modConfigs.size() == 1) {
            this.openModConfigScreen(modConfigs.getFirst(), this.lastScreen);
        }
    }

    protected void openModConfigScreen(ModConfig modConfig, Screen lastScreen) {
        Component component = getConfigTitleComponent(modConfig.getModId());
        this.minecraft.setScreen(new CustomConfigurationSectionScreen(lastScreen,
                modConfig.getType(),
                modConfig,
                component));
    }

    /**
     * @see ConfigurationScreen#addOptions()
     */
    protected Component getTooltipComponent(ModConfig.Type type, ModConfig modConfig) {
        if (!((ModConfigSpec) modConfig.getSpec()).isLoaded()) {
            return ConfigurationScreen.TOOLTIP_CANNOT_EDIT_NOT_LOADED;
        } else if (type == ModConfig.Type.SERVER && this.minecraft.getCurrentServer() != null
                && !this.minecraft.isSingleplayer()) {
            return ConfigurationScreen.TOOLTIP_CANNOT_EDIT_THIS_WHILE_ONLINE;
        } else if (type == ModConfig.Type.SERVER && this.minecraft.hasSingleplayerServer()
                && this.minecraft.getSingleplayerServer().isPublished()) {
            return ConfigurationScreen.TOOLTIP_CANNOT_EDIT_THIS_WHILE_OPEN_TO_LAN;
        } else {
            return null;
        }
    }

    /**
     * The title component shown on top of the config selection screen.
     *
     * @param modId the mod id
     * @return the title component
     */
    private static Component getConfigTitleComponent(String modId) {
        return Component.literal(ModContainer.getDisplayName(modId) + " Settings");
    }

    /**
     * Creates a section component for grouping configs based on their type.
     *
     * @param type the config type
     * @return the config type section component
     */
    private static Component getConfigSectionComponent(ModConfig.Type type) {
        String message;
        if (type != ModConfig.Type.SERVER) {
            message = "Global Configurations";
        } else {
            message = "World Configurations";
        }

        return Component.literal(message).withStyle(ChatFormatting.BOLD, ChatFormatting.YELLOW);
    }

    /**
     * Turns a config value key usually in lowercase underscore format into human-readable text.
     *
     * @param valueKey the value name in the config
     * @return the text component
     */
    private static MutableComponent getConfigValueComponent(String valueKey) {
        String string = ModContainer.getCapitalizedString(valueKey).replace(" And ", " & ").replace(" Or ", " / ");
        return Component.literal(string);
    }

    /**
     * Applies pretty text component formatting including splitting and coloring to a config value description string.
     *
     * @param valueKeyComponent the value key component from {@link #getConfigValueComponent(String)}
     * @param rawComment        the raw comment string
     * @return the tooltip component
     */
    private static Component getConfigValueTooltipComponent(Component valueKeyComponent, String rawComment) {
        if (!Strings.isBlank(rawComment)) {
            Component component = getStylizedStrings(rawComment.split("\\R"));
            if (component != null) {
                return Component.empty()
                        .append(valueKeyComponent)
                        .append(CommonComponents.NEW_LINE)
                        .append(CommonComponents.NEW_LINE)
                        .append(component);
            }
        }

        return CommonComponents.EMPTY;
    }

    /**
     * Combines multiple strings into a multi-line component with altering text colors.
     *
     * @param strings the strings to combine
     * @return the colored component
     */
    private static @Nullable Component getStylizedStrings(String... strings) {
        MutableComponent mutableComponent = null;
        for (int i = 0, j = 0; i < strings.length; i++) {
            // The default value is otherwise displayed twice.
            if (!strings[i].matches("^ Default: .*")) {
                String string = strings[i].replaceAll("^ Range: ", "Value Range: ");
                ChatFormatting chatFormatting = j++ % 2 == 0 ? ChatFormatting.YELLOW : ChatFormatting.GOLD;
                MutableComponent component = Component.literal(string).withStyle(chatFormatting);
                if (mutableComponent != null) {
                    mutableComponent.append(CommonComponents.NEW_LINE).append(component);
                } else {
                    mutableComponent = component;
                }
            }
        }

        return mutableComponent;
    }

    public static class CustomConfigurationSectionScreen extends ConfigurationScreen.ConfigurationSectionScreen {

        public CustomConfigurationSectionScreen(Screen parent, ModConfig.Type type, ModConfig modConfig, Component title) {
            super(parent, type, modConfig, title);
            // This is set to GAME for startup configs, which is wrong, as they reload just fine.
            this.needsRestart = ModConfigSpec.RestartType.NONE;
        }

        public CustomConfigurationSectionScreen(final Context parentContext, final Screen parent, final Map<String, Object> valueSpecs, final String key, final Set<? extends UnmodifiableConfig.Entry> entrySet, Component title) {
            super(parentContext, parent, valueSpecs, key, entrySet, title);
            // This is set to GAME for startup configs, which is wrong, as they reload just fine.
            this.needsRestart = ModConfigSpec.RestartType.NONE;
        }

        @Override
        protected MutableComponent getTranslationComponent(String key) {
            // Replace the usage of translatable components with component literals directly computed from the value key.
            return getConfigValueComponent(key);
        }

        @Override
        protected Component getTooltipComponent(final String key, ModConfigSpec.@Nullable Range<?> range) {
            // Replace the usage of translatable components with component literals and apply pretty formatting.
            return getConfigValueTooltipComponent(this.getTranslationComponent(key), this.getComment(key));
        }

        @Override
        public ConfigurationScreen.ConfigurationSectionScreen rebuild() {
            return super.rebuild();
        }

        @Override
        public void onClose() {
            // Move the warning message regarding the restart type for changed settings to the individual config screen
            // instead of having it on the main config selection screen.
            // This makes it much easier to implement, especially as we remove the relog / restart options which are not necessary.
            super.onClose();
            if (this.changed && !(this.lastScreen instanceof ConfigurationScreen.ConfigurationSectionScreen)) {
                switch (this.needsRestart) {
                    case GAME -> {
                        this.openConfirmScreen(ConfigurationScreen.GAME_RESTART_TITLE,
                                ConfigurationScreen.GAME_RESTART_MESSAGE);
                    }
                    case WORLD -> {
                        if (this.minecraft.level != null) {
                            this.openConfirmScreen(ConfigurationScreen.SERVER_RESTART_TITLE,
                                    ConfigurationScreen.SERVER_RESTART_MESSAGE);
                        }
                    }
                }
            }
        }

        private void openConfirmScreen(Component title, Component message) {
            this.minecraft.setScreen(new ConfirmScreen((boolean hasConfirmed) -> {
                if (hasConfirmed) {
                    super.onClose();
                } else {
                    this.minecraft.setScreen(this);
                }
            }, title, message, CommonComponents.GUI_CONTINUE, CommonComponents.GUI_BACK));
        }

        @Nullable
        protected Element createSection(final String key, final UnmodifiableConfig subconfig, final UnmodifiableConfig subsection) {
            // Make sure our own screen is used for submenus, while also replacing translated components with literals.
            if (subconfig.isEmpty()) {
                return null;
            } else {
                return new Element(this.getTranslationComponent(key).append(CommonComponents.ELLIPSIS),
                        this.getTooltipComponent(key, null),
                        Button.builder(EDIT_COMPONENT,
                                        (Button button) -> this.minecraft.setScreen(new CustomConfigurationSectionScreen(this.context,
                                                this,
                                                subconfig.valueMap(),
                                                key,
                                                subsection.entrySet(),
                                                this.getTranslationComponent(key)).rebuild()))
                                .tooltip(Tooltip.create(this.getTooltipComponent(key, null)))
                                .build(),
                        false);
            }
        }

        @Nullable
        protected <T> Element createList(final String key, final ModConfigSpec.ListValueSpec spec, final ModConfigSpec.ConfigValue<List<T>> list) {
            // Make sure our own screen is used for submenus, while also replacing translated components with literals.
            return new Element(this.getTranslationComponent(key).append(CommonComponents.ELLIPSIS),
                    this.getTooltipComponent(key, null),
                    Button.builder(EDIT_COMPONENT,
                                    button -> this.minecraft.setScreen(new CustomConfigurationListScreen<>(Context.list(this.context,
                                            this),
                                            key,
                                            Component.translatable(CRUMB,
                                                    this.getTitle(),
                                                    ConfigurationScreen.CRUMB_SEPARATOR,
                                                    this.getTranslationComponent(key)),
                                            spec,
                                            list).rebuild()))
                            .tooltip(Tooltip.create(this.getTooltipComponent(key, null)))
                            .build(),
                    false);
        }
    }

    public static class CustomConfigurationListScreen<T> extends ConfigurationScreen.ConfigurationListScreen<T> {

        public CustomConfigurationListScreen(Context context, String key, Component title, ModConfigSpec.ListValueSpec spec, ModConfigSpec.ConfigValue<List<T>> valueList) {
            super(context, key, title, spec, valueList);
            // This is set to GAME for startup configs, which is wrong, as they reload just fine.
            this.needsRestart = ModConfigSpec.RestartType.NONE;
        }

        @Override
        protected MutableComponent getTranslationComponent(String key) {
            // Replace the usage of translatable components with component literals directly computed from the value key.
            return getConfigValueComponent(key);
        }

        @Override
        protected Component getTooltipComponent(final String key, ModConfigSpec.@Nullable Range<?> range) {
            // Replace the usage of translatable components with component literals and apply pretty formatting.
            return getConfigValueTooltipComponent(this.getTranslationComponent(key), this.getComment(key));
        }

        @Override
        public ConfigurationScreen.ConfigurationSectionScreen rebuild() {
            return super.rebuild();
        }

        @Override
        protected @Nullable Element createOtherValue(int idx, T entry) {
            // Remove the tooltip for list entries.
            Element element = super.createOtherValue(idx, entry);
            if (element != null && element.widget() != null) {
                element.widget().setTooltip(null);
            }

            return element;
        }

        @Override
        protected @Nullable Element createStringListValue(int idx, String value) {
            // Remove the tooltip for list entries.
            Element element = super.createStringListValue(idx, value);
            if (element != null && element.widget() != null) {
                element.widget().setTooltip(null);
            }

            return element;
        }

        @Override
        protected @Nullable Element createDoubleListValue(int idx, Double value) {
            // Remove the tooltip for list entries.
            Element element = super.createDoubleListValue(idx, value);
            if (element != null && element.widget() != null) {
                element.widget().setTooltip(null);
            }

            return element;
        }

        @Override
        protected @Nullable Element createLongListValue(int idx, Long value) {
            // Remove the tooltip for list entries.
            Element element = super.createLongListValue(idx, value);
            if (element != null && element.widget() != null) {
                element.widget().setTooltip(null);
            }

            return element;
        }

        @Override
        protected @Nullable Element createIntegerListValue(int idx, Integer value) {
            // Remove the tooltip for list entries.
            Element element = super.createIntegerListValue(idx, value);
            if (element != null && element.widget() != null) {
                element.widget().setTooltip(null);
            }

            return element;
        }

        @Override
        protected @Nullable Element createBooleanListValue(int idx, Boolean value) {
            // Remove the tooltip for list entries.
            Element element = super.createBooleanListValue(idx, value);
            if (element != null && element.widget() != null) {
                element.widget().setTooltip(null);
            }

            return element;
        }
    }
}
