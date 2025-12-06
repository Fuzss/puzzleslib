package fuzs.puzzleslib.api.core.v1.resources;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;

/**
 * Allows for providing an identifier for a reload listener by wrapping it in an effort to help with debugging.
 */
@Deprecated
public interface NamedReloadListener extends PreparableReloadListener {

    /**
     * @return the identifier for this reload listener
     */
    ResourceLocation identifier();

    @Override
    default String getName() {
        return this.identifier().toString();
    }
}
