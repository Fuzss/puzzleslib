package fuzs.puzzleslib.impl.config;

import fuzs.puzzleslib.impl.PuzzlesLibMod;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.locale.Language;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;

public final class ConfigTranslationsManager {
    public static final Map<String, String> TRANSLATIONS = new HashMap<>();

    private ConfigTranslationsManager() {
        // NO-OP
    }

    public static void onAddResourcePackReloadListeners(BiConsumer<ResourceLocation, PreparableReloadListener> consumer) {
        consumer.accept(PuzzlesLibMod.id("config_translations"), (ResourceManagerReloadListener) (ResourceManager resourceManager) -> {
            if (Language.getInstance() instanceof ClientLanguage clientLanguage) {
                if (!(clientLanguage.storage instanceof HashMap<String, String>)) {
                    clientLanguage.storage = new HashMap<>(clientLanguage.storage);
                }
                TRANSLATIONS.forEach(clientLanguage.storage::putIfAbsent);
            }
        });
    }

    public static void addConfigTitle(String modId) {
        TRANSLATIONS.put(modId + ".configuration.title", "%s Configuration");
    }

    public static void addConfig(String modId, String fileName, String configType) {
        configType = getCapitalizedString(configType);
        fileName = fileName.replaceAll("[^a-zA-Z0-9]+", ".").replaceFirst("^\\.", "").replaceFirst("\\.$", "").toLowerCase();
        TRANSLATIONS.put(modId + ".configuration.section." + fileName, configType + " Settings");
        TRANSLATIONS.put(modId + ".configuration.section." + fileName + ".title", "%s " + configType + " Configuration");
    }

    public static void addConfigValue(String modId, String valueName) {
        Objects.requireNonNull(valueName, "value name is null");
        addConfigValue(modId, Collections.singletonList(valueName));
    }

    public static void addConfigValue(String modId, List<String> valuePath) {
        TRANSLATIONS.put(modId + ".configuration." + valuePath.getLast(), getCapitalizedString(valuePath.getLast()));
    }

    public static void addConfigValueComment(String modId, String valueName, @Nullable String comment) {
        Objects.requireNonNull(valueName, "value name is null");
        addConfigValueComment(modId, Collections.singletonList(valueName), comment != null ? Arrays.asList(comment.split("\\r?\\n")) : Collections.emptyList());
    }

    public static void addConfigValueComment(String modId, List<String> valuePath, List<String> comments) {
        String value = String.join(System.lineSeparator(), getStylizedStrings(comments));
        if (!value.isEmpty()) {
            TRANSLATIONS.put(modId + ".configuration." + valuePath.getLast() + ".tooltip", value);
        }
    }

    static String getCapitalizedString(String s) {
        String[] strings = s.toLowerCase().split("[\\s_]+");
        StringJoiner joiner = new StringJoiner(" ");
        for (String string : strings) {
            joiner.add(StringUtils.capitalize(string));
        }
        return joiner.toString();
    }

    static List<String> getStylizedStrings(List<String> strings) {
        strings = new ArrayList<>(strings);
        for (int i = 0; i < strings.size(); i++) {
            ChatFormatting chatFormatting = i % 2 == 0 ? ChatFormatting.YELLOW : ChatFormatting.GOLD;
            strings.set(i, chatFormatting + strings.get(i) + ChatFormatting.RESET);
        }
        return strings;
    }
}
