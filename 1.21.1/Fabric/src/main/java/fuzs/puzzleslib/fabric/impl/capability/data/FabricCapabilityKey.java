package fuzs.puzzleslib.fabric.impl.capability.data;

import com.mojang.serialization.Codec;
import fuzs.puzzleslib.api.capability.v3.data.CapabilityComponent;
import fuzs.puzzleslib.api.capability.v3.data.CapabilityKey;
import fuzs.puzzleslib.impl.capability.GlobalCapabilityRegister;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

@SuppressWarnings("UnstableApiUsage")
public abstract class FabricCapabilityKey<T, C extends CapabilityComponent<T>> implements CapabilityKey<T, C> {
    private final Supplier<AttachmentType<C>> supplier;
    private final Predicate<Object> filter;
    private final Function<T, Supplier<C>> factory;
    @Nullable
    private AttachmentType<C> attachmentType;

    public FabricCapabilityKey(Supplier<AttachmentType<C>> attachmentType, Predicate<Object> filter, Supplier<C> factory) {
        this.supplier = attachmentType;
        this.filter = filter;
        this.factory = (T holder) -> {
            return () -> {
                C capabilityComponent = factory.get();
                Objects.requireNonNull(capabilityComponent, "capability component is null");
                capabilityComponent.initialize((CapabilityKey<T, CapabilityComponent<T>>) this, holder);
                return capabilityComponent;
            };
        };
        GlobalCapabilityRegister.register(this);
    }

    private AttachmentType<C> getAttachmentType() {
        Objects.requireNonNull(this.attachmentType, "attachment type is null");
        return this.attachmentType;
    }

    public void register() {
        this.attachmentType = this.supplier.get();
    }

    @Override
    public ResourceLocation identifier() {
        return this.getAttachmentType().identifier();
    }

    @Override
    public Codec<C> codec() {
        Codec<C> codec = this.getAttachmentType().persistenceCodec();
        Objects.requireNonNull(codec, "codec is null");
        return codec;
    }

    @Override
    public C get(@NotNull T holder) {
        Objects.requireNonNull(holder, "holder is null");
        if (holder instanceof AttachmentTarget attachmentTarget && this.isProvidedBy(holder)) {
            C capabilityComponent = attachmentTarget.getAttachedOrCreate(this.getAttachmentType(), this.factory.apply(holder));
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
        if (holder instanceof AttachmentTarget attachmentTarget && this.isProvidedBy(holder)) {
            // if the attachment is created from deserialization this has not been called yet
            capabilityComponent.initialize((CapabilityKey<T, CapabilityComponent<T>>) this, holder);
            attachmentTarget.setAttached(this.getAttachmentType(), capabilityComponent);
        } else {
            throw new IllegalArgumentException("Invalid capability holder: " + holder);
        }
    }

    @Override
    public boolean isProvidedBy(@Nullable Object holder) {
        return holder instanceof AttachmentTarget && this.filter.test(holder);
    }

    public void configureBuilder(AttachmentRegistry.Builder<C> builder) {
        // NO-OP
    }

    @FunctionalInterface
    public interface Factory<T, C extends CapabilityComponent<T>, K extends CapabilityKey<T, C>> {

        K apply(Supplier<AttachmentType<C>> attachmentType, Predicate<Object> filter, Supplier<C> factory);
    }
}
