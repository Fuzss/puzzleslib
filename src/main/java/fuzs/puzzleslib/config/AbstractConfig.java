package fuzs.puzzleslib.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import fuzs.puzzleslib.config.annotation.ConfigBuilder;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;
import java.util.Map;

/**
 * abstract config template
 */
public abstract class AbstractConfig {
    /**
     * category name, if no category leave empty
     */
    private final String name;
    /**
     * category comment, only added when this is a category ({@link #name} is present)
     */
    private final Map<List<String>, String[]> categoryComments = Maps.newHashMap();

    /**
     * @param name category name
     */
    public AbstractConfig(String name) {
        this.name = name;
    }

    /**
     * setup config from config holder
     * @param builder builder to add entries to
     * @param saveCallback register save callback
     */
    public final void setupConfig(ForgeConfigSpec.Builder builder, ConfigHolder.ConfigCallback saveCallback) {
        setupConfig(this, builder, saveCallback);
    }

    /**
     * add config entries
     * @param builder builder to add entries to
     * @param saveCallback register save callback
     */
    protected void addToBuilder(ForgeConfigSpec.Builder builder, ConfigHolder.ConfigCallback saveCallback) {
    }

    /**
     * transform config options to proper type after reload, e.g. strings to registry entries
     */
    protected void afterConfigReload() {
    }

    /**
     * @param comment comment for this category
     */
    protected final void addComment(String... comment) {
        this.addComment(Lists.newArrayList(), comment);
    }

    /**
     * @param path category path for <code>comment</code>
     * @param comment comment
     */
    protected final void addComment(List<String> path, String... comment) {
        this.categoryComments.put(path, comment);
    }

    /**
     * adds entries, category, and category comment
     * @param config config to build
     * @param builder builder to add entries to
     * @param saveCallback register save callback
     */
    public static void setupConfig(AbstractConfig config, ForgeConfigSpec.Builder builder, ConfigHolder.ConfigCallback saveCallback) {
        final boolean withCategory = config.name != null && !config.name.isEmpty();
        if (withCategory) {
            final String[] comment = config.categoryComments.get(Lists.<String>newArrayList());
            if (comment != null && comment.length != 0) builder.comment(comment);
            builder.push(config.name);
        }
        // currently, supports both registering via annotation system and builder method
        ConfigBuilder.serialize(builder, saveCallback, Maps.newHashMap(config.categoryComments), config);
        // legacy method, kept for now for types unsupported by annotation system
        config.addToBuilder(builder, saveCallback);
        if (withCategory) {
            builder.pop();
        }
    }
}