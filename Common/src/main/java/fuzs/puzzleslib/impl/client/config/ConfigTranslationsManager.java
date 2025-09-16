package fuzs.puzzleslib.impl.client.config;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import fuzs.puzzleslib.impl.PuzzlesLibMod;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.locale.Language;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

public final class ConfigTranslationsManager {
    public static final Map<String, String> TRANSLATIONS = new ConcurrentHashMap<>();

    private ConfigTranslationsManager() {
        // NO-OP
    }

    public static void onAddResourcePackReloadListeners(BiConsumer<ResourceLocation, PreparableReloadListener> consumer) {
        consumer.accept(PuzzlesLibMod.id("config_translations"),
                (ResourceManagerReloadListener) (ResourceManager resourceManager) -> {
                    if (Language.getInstance() instanceof ClientLanguage clientLanguage) {
                        if (!(clientLanguage.storage instanceof HashMap<String, String>)) {
                            clientLanguage.storage = new HashMap<>(clientLanguage.storage);
                        }
                        TRANSLATIONS.forEach(clientLanguage.storage::putIfAbsent);
                    }
                });
    }

    public static void addModConfig(String modId, String configType, String fileName, ModConfigSpec configSpec) {
        addConfigTitle(modId);
        addConfigFile(modId, fileName, configType);
        addConfigValues(modId, configSpec.getSpec(), new ArrayList<>(), configSpec::getLevelComment);
    }

    private static void addConfigValues(String modId, UnmodifiableConfig config, List<String> path, Function<List<String>, @Nullable String> levelCommentGetter) {
        for (UnmodifiableConfig.Entry entry : config.entrySet()) {
            addConfigValue(modId, entry.getKey());
            String comment;
            if (entry.getValue() instanceof ModConfigSpec.ValueSpec valueSpec) {
                comment = valueSpec.getComment();
            } else if (entry.getValue() instanceof UnmodifiableConfig) {
                path = new ArrayList<>(path);
                path.add(entry.getKey());
                comment = levelCommentGetter.apply(path);
                addConfigValues(modId, entry.getValue(), path, levelCommentGetter);
            } else {
                comment = null;
            }
            addConfigValueComment(modId, entry.getKey(), comment);
            addConfigValueButton(modId, entry.getKey());
        }
    }

    public static void addConfigTitle(String modId) {
        TRANSLATIONS.put(modId + ".configuration.title", "%s Configuration");
    }

    public static void addConfigFile(String modId, String fileName, String configType) {
        configType = getConfigTypeFromFileName(fileName).orElse(configType);
        configType = getCapitalizedString(configType);
        fileName = fileName.replaceAll("[^a-zA-Z0-9]+", ".")
                .replaceFirst("^\\.", "")
                .replaceFirst("\\.$", "")
                .toLowerCase();
        TRANSLATIONS.put(modId + ".configuration.section." + fileName, "%s " + configType + " Settings");
        TRANSLATIONS.put(modId + ".configuration.section." + fileName + ".title",
                "%s " + configType + " Configuration");
    }

    static Optional<String> getConfigTypeFromFileName(String fileName) {
        int startIndex = fileName.lastIndexOf('-');
        int endIndex = fileName.lastIndexOf('.');
        if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
            return Optional.of(fileName.substring(startIndex + 1, endIndex));
        } else {
            return Optional.empty();
        }
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
        addConfigValueComment(modId,
                Collections.singletonList(valueName),
                comment != null ? new ArrayList<>(Arrays.asList(comment.split("\\R"))) : Collections.emptyList());
    }

    public static void addConfigValueComment(String modId, List<String> valuePath, List<String> comments) {
        String value = String.join(System.lineSeparator(), getStylizedStrings(comments));
        // also put in empty strings, as translations are expected for config values without a comment as well
        TRANSLATIONS.put(modId + ".configuration." + valuePath.getLast() + ".tooltip", value);
    }

    public static void addConfigValueButton(String modId, String valueName) {
        Objects.requireNonNull(valueName, "value name is null");
        addConfigValueButton(modId, Collections.singletonList(valueName));
    }

    public static void addConfigValueButton(String modId, List<String> valuePath) {
        TRANSLATIONS.put(valuePath.getLast() + ".button", "Edit...");
        TRANSLATIONS.put(modId + ".configuration." + valuePath.getLast() + ".button", "Edit...");
    }

    private static String getCapitalizedString(String s) {
        String[] strings = s.toLowerCase().split("[\\s_]+");
        StringJoiner joiner = new StringJoiner(" ");
        for (String string : strings) {
            joiner.add(StringUtils.capitalize(string));
        }
        return joiner.toString().replace(" And ", " & ").replace(" Or ", " / ");
    }

    private static List<String> getStylizedStrings(List<String> strings) {
        strings.removeIf((String string) -> string.matches("^ Default: .*"));
        for (int i = 0; i < strings.size(); i++) {
            String string = strings.get(i).replaceAll("^ Range: ", "Value Range: ");
            ChatFormatting chatFormatting = i % 2 == 0 ? ChatFormatting.YELLOW : ChatFormatting.GOLD;
            strings.set(i, chatFormatting + string + ChatFormatting.RESET);
        }
        return strings;
    }
}
