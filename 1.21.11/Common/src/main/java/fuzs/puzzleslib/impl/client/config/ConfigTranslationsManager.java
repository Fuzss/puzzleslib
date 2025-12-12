package fuzs.puzzleslib.impl.client.config;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.locale.Language;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public final class ConfigTranslationsManager implements ResourceManagerReloadListener {
    public static final ConfigTranslationsManager INSTANCE = new ConfigTranslationsManager();

    private final Map<String, String> translations = new ConcurrentHashMap<>();

    private ConfigTranslationsManager() {
        // NO-OP
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        if (Language.getInstance() instanceof ClientLanguage clientLanguage) {
            if (!(clientLanguage.storage instanceof HashMap<String, String>)) {
                clientLanguage.storage = new HashMap<>(clientLanguage.storage);
            }

            this.translations.forEach(clientLanguage.storage::putIfAbsent);
        }
    }

    public void addModConfig(String modId, String configType, String fileName, ModConfigSpec configSpec) {
        this.addConfigTitle(modId);
        this.addConfigFile(modId, fileName, configType);
        this.addConfigValues(modId, configSpec.getSpec(), new ArrayList<>(), configSpec::getLevelComment);
    }

    private void addConfigValues(String modId, UnmodifiableConfig config, List<String> path, Function<List<String>, @Nullable String> levelCommentGetter) {
        for (UnmodifiableConfig.Entry entry : config.entrySet()) {
            this.addConfigValue(modId, entry.getKey());
            String comment;
            if (entry.getValue() instanceof ModConfigSpec.ValueSpec valueSpec) {
                comment = valueSpec.getComment();
            } else if (entry.getValue() instanceof UnmodifiableConfig) {
                path = new ArrayList<>(path);
                path.add(entry.getKey());
                comment = levelCommentGetter.apply(path);
                this.addConfigValues(modId, entry.getValue(), path, levelCommentGetter);
            } else {
                comment = null;
            }

            this.addConfigValueComment(modId, entry.getKey(), comment);
            this.addConfigValueButton(modId, entry.getKey());
        }
    }

    public void addConfigTitle(String modId) {
        this.translations.put(modId + ".configuration.title", "%s Configuration");
    }

    public void addConfigFile(String modId, String fileName, String configType) {
        configType = getConfigTypeFromFileName(fileName).orElse(configType);
        configType = getCapitalizedString(configType);
        fileName = fileName.replaceAll("[^a-zA-Z0-9]+", ".")
                .replaceFirst("^\\.", "")
                .replaceFirst("\\.$", "")
                .toLowerCase();
        this.translations.put(modId + ".configuration.section." + fileName, "%s " + configType + " Settings");
        this.translations.put(modId + ".configuration.section." + fileName + ".title",
                "%s " + configType + " Configuration");
    }

    private static Optional<String> getConfigTypeFromFileName(String fileName) {
        int startIndex = fileName.lastIndexOf('-');
        int endIndex = fileName.lastIndexOf('.');
        if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
            return Optional.of(fileName.substring(startIndex + 1, endIndex));
        } else {
            return Optional.empty();
        }
    }

    public void addConfigValue(String modId, String valueName) {
        Objects.requireNonNull(valueName, "value name is null");
        this.addConfigValue(modId, Collections.singletonList(valueName));
    }

    public void addConfigValue(String modId, List<String> valuePath) {
        this.translations.put(modId + ".configuration." + valuePath.getLast(),
                getCapitalizedString(valuePath.getLast()));
    }

    public void addConfigValueComment(String modId, String valueName, @Nullable String comment) {
        Objects.requireNonNull(valueName, "value name is null");
        this.addConfigValueComment(modId,
                Collections.singletonList(valueName),
                comment != null ? new ArrayList<>(Arrays.asList(comment.split("\\R"))) : Collections.emptyList());
    }

    public void addConfigValueComment(String modId, List<String> valuePath, List<String> comments) {
        String value = String.join(System.lineSeparator(), getStylizedStrings(comments));
        // also put in empty strings, as translations are expected for config values without a comment as well
        this.translations.put(modId + ".configuration." + valuePath.getLast() + ".tooltip", value);
    }

    public void addConfigValueButton(String modId, String valueName) {
        Objects.requireNonNull(valueName, "value name is null");
        this.addConfigValueButton(modId, Collections.singletonList(valueName));
    }

    public void addConfigValueButton(String modId, List<String> valuePath) {
        this.translations.put(valuePath.getLast() + ".button", "Edit...");
        this.translations.put(modId + ".configuration." + valuePath.getLast() + ".button", "Edit...");
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
