package fuzs.puzzleslib.neoforge.impl.capability.data;

import com.mojang.serialization.Codec;
import fuzs.puzzleslib.api.capability.v3.data.CapabilityComponent;
import fuzs.puzzleslib.api.capability.v3.data.CapabilityKey;
import fuzs.puzzleslib.impl.capability.GlobalCapabilityRegister;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Predicate;

public abstract class NeoForgeCapabilityKey<T, C extends CapabilityComponent<T>> implements CapabilityKey<T, C> {
    private final DeferredHolder<AttachmentType<?>, AttachmentType<C>> attachmentType;
    private final Codec<C> codec;
    private final Predicate<Object> filter;

    public NeoForgeCapabilityKey(DeferredHolder<AttachmentType<?>, AttachmentType<C>> attachmentType, Codec<C> codec, Predicate<Object> filter) {
        this.attachmentType = attachmentType;
        this.codec = codec;
        this.filter = filter;
        GlobalCapabilityRegister.register(this);
    }

    @Override
    public ResourceLocation identifier() {
        return this.attachmentType.getId();
    }

    @Override
    public Codec<C> codec() {
        return this.codec;
    }

    @Override
    public C get(@NotNull T holder) {
        Objects.requireNonNull(holder, "holder is null");
        if (holder instanceof IAttachmentHolder attachmentHolder && this.isProvidedBy(holder)) {
            C capabilityComponent = attachmentHolder.getData(this.attachmentType.value());
            Objects.requireNonNull(capabilityComponent, "data is null");
            // if the attachment is created from deserialization this has not been called yet
            capabilityComponent.initialize((CapabilityKey<T, CapabilityComponent<T>>) this, holder);
            return capabilityComponent;
        } else {
            throw new IllegalArgumentException("Invalid capability holder: " + holder);
        }
    }

    @Override
    public void set(@NotNull T holder, @NotNull C capabilityComponent) {
        Objects.requireNonNull(holder, "holder is null");
        Objects.requireNonNull(capabilityComponent, "data is null");
        if (holder instanceof IAttachmentHolder attachmentHolder && this.isProvidedBy(holder)) {
            // if the attachment is created from deserialization this has not been called yet
            capabilityComponent.initialize((CapabilityKey<T, CapabilityComponent<T>>) this, holder);
            attachmentHolder.setData(this.attachmentType.value(), capabilityComponent);
        } else {
            throw new IllegalArgumentException("Invalid capability holder: " + holder);
        }
    }

    @Override
    public boolean isProvidedBy(@Nullable Object holder) {
        return holder instanceof IAttachmentHolder && this.filter.test(holder);
    }

    public void configureBuilder(AttachmentType.Builder<C> builder) {
        // NO-OP
    }

    @FunctionalInterface
    public interface Factory<T, C extends CapabilityComponent<T>, K extends CapabilityKey<T, C>> {

        K apply(DeferredHolder<AttachmentType<?>, AttachmentType<C>> attachmentType, Codec<C> codec, Predicate<Object> filter);
    }
}
