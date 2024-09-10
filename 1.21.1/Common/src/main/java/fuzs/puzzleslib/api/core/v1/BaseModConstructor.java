package fuzs.puzzleslib.api.core.v1;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * An extension for common and client mod constructors for handling their construction order to guarantee the common constructor to be constructed first.
 */
public interface BaseModConstructor {

    /**
     * @return an identifier matching the identifier returned from another {@link BaseModConstructor}
     */
    @Nullable
    default ResourceLocation getPairingIdentifier() {
        return null;
    }

    /**
     * @return specific content this mod uses that needs to be additionally registered
     */
    default ContentRegistrationFlags[] getContentRegistrationFlags() {
        return new ContentRegistrationFlags[0];
    }
}
