package fuzs.puzzleslib.fabric.impl.client.config;

import com.google.common.collect.ImmutableSet;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.config.ModConfigs;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.Collection;
import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * An extension of {@link ConfigurationScreen} that allows for displaying configs from multiple mods.
 */
public class MultiConfigurationScreen extends OptionsSubScreen {
    private static final String LANG_PREFIX = "neoforge.configuration.uitext.";
    private static final String SECTION = LANG_PREFIX + "section";
    private static final String FILENAME_TOOLTIP = LANG_PREFIX + "filenametooltip";

    protected final Collection<String> modIds;
    protected final ConfigurationScreen configurationScreen;

    public static BiFunction<String, Screen, Screen> getScreenFactory(String[] mergedModIds) {
        return (String modId, Screen lastScreen) -> {
            ConfigurationScreen configurationScreen = new ConfigurationScreen(modId,
                    lastScreen,
                    (ConfigurationScreen configurationScreenX, ModConfig.Type type, ModConfig modConfig, Component component) -> {
                        // this is only used for determining the restart type, which is always set to GAME for startup configs
                        // this is wrong, as they reload just fine, so we pass any other type
                        return new ConfigurationScreen.ConfigurationSectionScreen(configurationScreenX,
                                ModConfig.Type.COMMON,
                                modConfig,
                                component);
                    });
            // always use our screen to have the rest of our custom implementation apply always
            return new MultiConfigurationScreen(ImmutableSet.<String>builder().add(modId).add(mergedModIds).build(),
                    lastScreen,
                    configurationScreen);
        };
    }

    MultiConfigurationScreen(Collection<String> modIds, Screen lastScreen, ConfigurationScreen configurationScreen) {
        super(lastScreen, Minecraft.getInstance().options, configurationScreen.getTitle());
        this.modIds = modIds;
        this.configurationScreen = configurationScreen;
    }

    @Override
    protected void init() {
        super.init();
        this.configurationScreen.minecraft = this.minecraft;
        this.configurationScreen.font = this.font;
        this.configurationScreen.width = this.width;
        this.configurationScreen.height = this.height;
    }

    @Override
    protected void addOptions() {
        // mostly copied from ConfigurationScreen, but the mod id is checked against a set of valid ids
        for (final ModConfig.Type type : ModConfig.Type.values()) {
            boolean headerAdded = false;
            for (final ModConfig modConfig : ModConfigs.getConfigSet(type)) {
                if (this.modIds.contains(modConfig.getModId())) {
                    if (!headerAdded) {
                        this.list.addSmall(new StringWidget(ConfigurationScreen.BIG_BUTTON_WIDTH,
                                Button.DEFAULT_HEIGHT,
                                Component.translatable(LANG_PREFIX + type.name().toLowerCase(Locale.ENGLISH))
                                        .withStyle(ChatFormatting.UNDERLINE),
                                this.font).alignLeft(), null);
                        headerAdded = true;
                    }
                    Button btn = Button.builder(Component.translatable(SECTION,
                                    this.translatableConfig(modConfig,
                                            "",
                                            LANG_PREFIX + "type." + modConfig.getType().name().toLowerCase(Locale.ROOT))),
                            button -> {
                                Component component = this.translatableConfig(modConfig,
                                        ".title",
                                        LANG_PREFIX + "title." + type.name().toLowerCase(Locale.ROOT));
                                this.minecraft.setScreen(new MultiConfigurationSectionScreen(this,
                                        type,
                                        modConfig,
                                        component,
                                        restartType -> {
                                            this.configurationScreen.needsRestart = this.configurationScreen.needsRestart.with(
                                                    restartType);
                                        }));
                            }).width(ConfigurationScreen.BIG_BUTTON_WIDTH).build();
                    MutableComponent tooltip = Component.empty();
                    if (!((ModConfigSpec) modConfig.getSpec()).isLoaded()) {
                        tooltip.append(ConfigurationScreen.TOOLTIP_CANNOT_EDIT_NOT_LOADED)
                                .append(Component.literal("\n\n"));
                        btn.active = false;
                    } else if (type == ModConfig.Type.SERVER && this.minecraft.getCurrentServer() != null &&
                            !this.minecraft.isSingleplayer()) {
                        tooltip.append(ConfigurationScreen.TOOLTIP_CANNOT_EDIT_THIS_WHILE_ONLINE)
                                .append(Component.literal("\n\n"));
                        btn.active = false;
                    } else if (type == ModConfig.Type.SERVER && this.minecraft.hasSingleplayerServer() &&
                            this.minecraft.getSingleplayerServer().isPublished()) {
                        tooltip.append(ConfigurationScreen.TOOLTIP_CANNOT_EDIT_THIS_WHILE_OPEN_TO_LAN)
                                .append(Component.literal("\n\n"));
                        btn.active = false;
                    }
                    tooltip.append(Component.translatable(FILENAME_TOOLTIP, modConfig.getFileName()));
                    btn.setTooltip(Tooltip.create(tooltip));
                    this.list.addSmall(btn, null);
                }
            }
        }
    }

    public Component translatableConfig(ModConfig modConfig, String suffix, String fallback) {
        // remove translation checker, it is not accessible
        String fileName = modConfig.getFileName()
                .replaceAll("[^a-zA-Z0-9]+", ".")
                .replaceFirst("^\\.", "")
                .replaceFirst("\\.$", "")
                .toLowerCase(Locale.ENGLISH);
        String translationKey = modConfig.getModId() + ".configuration.section." + fileName + suffix;
        String modName = ModLoaderEnvironment.INSTANCE.getModContainer(modConfig.getModId())
                .map(fuzs.puzzleslib.api.core.v1.ModContainer::getDisplayName)
                .orElse(modConfig.getModId());
        return Component.translatable(I18n.exists(translationKey) ? translationKey : fallback, modName);
    }

    @Override
    public void onClose() {
        this.configurationScreen.onClose();
    }

    public static class MultiConfigurationSectionScreen extends ConfigurationScreen.ConfigurationSectionScreen {
        private final Consumer<ModConfigSpec.RestartType> needsRestartCallback;

        public MultiConfigurationSectionScreen(Screen parent, ModConfig.Type type, ModConfig modConfig, Component title, Consumer<ModConfigSpec.RestartType> needsRestartCallback) {
            super(parent, type, modConfig, title);
            this.needsRestartCallback = needsRestartCallback;
            // this is set to GAME for startup configs, which is wrong, as they reload just fine
            this.needsRestart = ModConfigSpec.RestartType.NONE;
        }

        @Override
        public void onClose() {
            if (this.changed) this.needsRestartCallback.accept(this.needsRestart);
            super.onClose();
        }
    }
}
