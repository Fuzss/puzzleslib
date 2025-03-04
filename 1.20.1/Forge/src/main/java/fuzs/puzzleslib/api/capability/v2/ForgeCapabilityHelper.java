package fuzs.puzzleslib.api.capability.v2;

import fuzs.puzzleslib.api.capability.v2.data.CapabilityComponent;
import fuzs.puzzleslib.api.capability.v2.data.CapabilityKey;
import fuzs.puzzleslib.impl.capability.v2.data.ForgeCapabilityKey;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;

/**
 * A helper class for providing the non-wrappable {@link net.minecraftforge.common.capabilities.CapabilityToken} for creating an actual {@link Capability}.
 * <p>Similar setup for capabilities is necessary on Fabric, but that is all done in <code>fabric.mod.json</code>.
 */
public final class ForgeCapabilityHelper {

    private ForgeCapabilityHelper() {

    }

    /**
     * After creating {@link fuzs.puzzleslib.api.capability.v2.data.CapabilityKey} in common project, the Forge project
     * must call this method for creating the actual {@link Capability}.
     *
     * @param key   the {@link fuzs.puzzleslib.api.capability.v2.data.CapabilityKey} to add the <code>token</code> to
     * @param token the token, created with an anonymous class
     * @param <C>   capability type
     */
    public static <C extends CapabilityComponent> void setCapabilityToken(CapabilityKey<C> key, CapabilityToken<C> token) {
        ((ForgeCapabilityKey<C>) key).createCapability(token);
    }
}
