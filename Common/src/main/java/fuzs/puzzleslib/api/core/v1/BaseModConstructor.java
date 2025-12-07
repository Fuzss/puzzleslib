package fuzs.puzzleslib.api.core.v1;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * An extension for common and client mod constructors for handling their construction order to guarantee the common
 * constructor to be constructed first.
 */
@Deprecated
public interface BaseModConstructor {

    /**
     * @return an identifier matching the identifier returned from another {@link BaseModConstructor}
     */
    @Deprecated
    @Nullable
    default ResourceLocation getPairingIdentifier() {
        throw new UnsupportedOperationException();
    }

    /**
     * @return specific content this mod uses that needs to be additionally registered
     */
    default ContentRegistrationFlags[] getContentRegistrationFlags() {
        return new ContentRegistrationFlags[0];
    }
}
