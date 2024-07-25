package fuzs.puzzleslib.impl.config;

import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.locale.Language;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public final class ConfigTranslationsManager implements ResourceManagerReloadListener {
    public static final ConfigTranslationsManager INSTANCE = new ConfigTranslationsManager();

    private final Map<String, String> translations = new HashMap<>();

    private ConfigTranslationsManager() {
        // NO-OP
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        if (Language.getInstance() instanceof ClientLanguage clientLanguage) {
            if (!(clientLanguage.storage instanceof HashMap<String, String>)) {
                clientLanguage.storage = new HashMap<>(clientLanguage.storage);
            }
        }
    }

    public void addConfigTitle(String modId) {
        this.translations.put(modId + ".configuration.title", "%s Configuration");
    }

    public void addConfig(String modId, String fileName, String configType) {
        configType = capitalizeFully(configType);
        fileName = fileName.replaceAll("[^a-zA-Z0-9]+", ".").replaceFirst("^\\.", "").replaceFirst("\\.$", "").toLowerCase();
        this.translations.put(modId + ".configuration.section." + fileName, configType + " Settings");
        this.translations.put(modId + ".configuration.section." + fileName + ".title", "%s " + configType + " Configuration");
    }

    public void addConfigValue(String modId, List<String> valuePath) {
        this.translations.put(modId + ".configuration." + String.join(".", valuePath), capitalizeFully(valuePath.getLast()));
    }

    public void addConfigValueComment(String modId, List<String> valuePath, List<String> comments) {
        this.translations.put(modId + ".configuration." + String.join(".", valuePath) + ".tooltip", String.join(System.lineSeparator(), comments));
    }

    private static String capitalizeFully(String s) {
        String[] strings = s.toLowerCase().split("[\\s_]+");
        StringJoiner joiner = new StringJoiner(" ");
        for (String string : strings) {
            joiner.add(StringUtils.capitalize(string));
        }
        return joiner.toString();
    }
}
