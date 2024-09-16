package fuzs.puzzleslib.api.capability.v3.data;

import com.mojang.serialization.Codec;
import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import fuzs.puzzleslib.api.network.v3.PlayerSet;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Common wrapper for Fabric's Component / NeoForge's AttachmentType / Forge's Capability.
 *
 * @param <T> capability component holder type
 * @param <C> capability component type
 */
public interface CapabilityKey<T, C extends CapabilityComponent<T>> {

    /**
     * Identifier for this capability used for synchronization and serialization.
     *
     * @return capability id
     */
    ResourceLocation identifier();

    /**
     * @return the codec for serialization
     */
    Codec<C> codec();

    /**
     * @param capabilityComponent the capability
     * @param registries          the registry access
     * @return the cloned capability
     */
    default C clone(C capabilityComponent, HolderLookup.Provider registries) {
        RegistryOps<Tag> ops = registries.createSerializationContext(NbtOps.INSTANCE);
        Tag tag = this.codec().encodeStart(ops, capabilityComponent).getOrThrow();
        return this.codec().parse(ops, tag).getOrThrow();
    }

    /**
     * Get the capability from a holder.
     *
     * @param holder the provider to get the capability from
     * @return the capability
     */
    C get(@NotNull T holder);

    /**
     * Set the capability for a holder.
     *
     * @param holder              the provider to set the capability for
     * @param capabilityComponent the capability
     */
    void set(@NotNull T holder, @NotNull C capabilityComponent);

    /**
     * Checks if a capability is compatible with a holder.
     *
     * @param holder provider to test
     * @return is this holder compatible
     */
    boolean isProvidedBy(@Nullable Object holder);

    /**
     * Get capability implementation from a holder if present.
     * <p>Utility method to avoid having to cast the holder to the generic type.
     *
     * @param holder provider to get capability from
     * @return optional capability implementation for given holder
     */
    default Optional<C> getIfProvided(@Nullable Object holder) {
        return this.isProvidedBy(holder) ? Optional.of(this.get((T) holder)) : Optional.empty();
    }

    /**
     * Called from {@link CapabilityComponent#setChanged()} to notify that the component has changed and needs to be
     * serialized / synchronized.
     * <p>
     * Actual behavior varies depending on the type of holder, for that see implementations in sub-interfaces.
     * <p>
     * Provided {@link PlayerSet} is used only when synchronizing entity capabilities to clients, providing a
     * <code>null</code> value will use a fallback registered via {@link SyncStrategy}.
     *
     * @param capabilityComponent the component that has changed
     * @param playerSet           clients to synchronize to
     */
    void setChanged(C capabilityComponent, @Nullable PlayerSet playerSet);

    /**
     * Writes a component to a packet for sending to remotes.
     *
     * @param capabilityComponent the component for serialization
     * @return the packet
     */
    ClientboundMessage<?> toPacket(C capabilityComponent);
}
