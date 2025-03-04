package fuzs.puzzleslib.impl.capability.v3.data;

import fuzs.puzzleslib.api.capability.v3.data.CapabilityComponent;
import fuzs.puzzleslib.api.capability.v3.data.CapabilityKey;
import fuzs.puzzleslib.impl.capability.v3.GlobalCapabilityRegister;
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
    private final AttachmentType<C> attachmentType;
    private final Predicate<Object> filter;
    private final Function<T, Supplier<C>> factory;

    public FabricCapabilityKey(AttachmentType<C> attachmentType, Predicate<Object> filter, Supplier<C> factory) {
        this.attachmentType = attachmentType;
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

    @Override
    public ResourceLocation identifier() {
        return this.attachmentType.identifier();
    }

    @Override
    public C get(@NotNull T holder) {
        Objects.requireNonNull(holder, "holder is null");
        if (holder instanceof AttachmentTarget attachmentTarget && this.isProvidedBy(holder)) {
            C capabilityComponent = attachmentTarget.getAttachedOrCreate(this.attachmentType, this.factory.apply(holder));
            Objects.requireNonNull(capabilityComponent, "data is null");
            // if the attachment is created from deserialization this has not been called yet
            capabilityComponent.initialize((CapabilityKey<T, CapabilityComponent<T>>) this, holder);
            return capabilityComponent;
        } else {
            throw new IllegalArgumentException("Invalid capability holder: %s".formatted(holder));
        }
    }

    @Override
    public boolean isProvidedBy(@Nullable Object holder) {
        return holder instanceof AttachmentTarget && this.filter.test(holder);
    }

    @FunctionalInterface
    public interface Factory<T, C extends CapabilityComponent<T>, K extends CapabilityKey<T, C>> {

        K apply(AttachmentType<C> attachmentType, Predicate<Object> filter, Supplier<C> factory);
    }
}
