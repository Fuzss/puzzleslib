package fuzs.puzzleslib.forge.api.capability.v3;

import fuzs.puzzleslib.api.capability.v3.data.CapabilityComponent;
import fuzs.puzzleslib.api.capability.v3.data.CapabilityKey;
import fuzs.puzzleslib.forge.impl.capability.data.ForgeCapabilityKey;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;

/**
 * A helper class for providing the non-wrappable {@link CapabilityToken} for creating an actual {@link Capability}.
 * <p>Similar setup for capabilities is necessary on Fabric, but that is all done in <code>fabric.mod.json</code>.
 */
@Deprecated(forRemoval = true)
public final class ForgeCapabilityHelper {

    private ForgeCapabilityHelper() {

    }

    /**
     * After creating {@link CapabilityKey} in common project, the Forge project
     * must call this method for creating the actual {@link Capability}.
     *
     * @param key   the {@link CapabilityKey} to add the <code>token</code> to
     * @param token the token, created with an anonymous class
     * @param <C>   capability type
     */
    public static <T, C extends CapabilityComponent<T>> void setCapabilityToken(CapabilityKey<T, C> key, CapabilityToken<C> token) {
        // NO-OP
    }
}
