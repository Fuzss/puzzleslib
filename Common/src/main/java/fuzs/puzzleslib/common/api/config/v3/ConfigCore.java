package fuzs.puzzleslib.common.api.config.v3;

import fuzs.puzzleslib.common.api.config.v3.serialization.ConfigDataSet;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Config template interface for each config category.
 * <p>Can be nested, meaning fields in a subclass can be once again of type {@link ConfigCore}).
 */
public interface ConfigCore {

    /**
     * Add config entries, legacy method, useful for types unsupported by annotation system.
     * <p>Not available when constructing static config classes.
     *
     * @param builder builder to add entries to
     * @param callback register save callback
     */
    default void addToBuilder(ModConfigSpec.Builder builder, ValueCallback callback) {
        // NO-OP
    }

    /**
     * Transform config options to proper type after reload, e.g. list of strings to registry entries.
     *
     * @see ConfigDataSet
     */
    default void afterConfigReload() {
        // NO-OP
    }
}
