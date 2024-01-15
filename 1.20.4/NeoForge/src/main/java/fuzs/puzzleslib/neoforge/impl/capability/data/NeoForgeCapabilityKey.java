package fuzs.puzzleslib.neoforge.impl.capability.data;

import fuzs.puzzleslib.api.capability.v2.CapabilityController;
import fuzs.puzzleslib.api.capability.v2.data.CapabilityComponent;
import fuzs.puzzleslib.api.capability.v2.data.CapabilityKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Objects;
import java.util.function.Predicate;

public abstract class NeoForgeCapabilityKey<T, C extends CapabilityComponent<T>> implements CapabilityKey<T, C> {
    private final DeferredHolder<AttachmentType<?>, AttachmentType<C>> holder;
    private final Predicate<Object> filter;

    public NeoForgeCapabilityKey(DeferredHolder<AttachmentType<?>, AttachmentType<C>> holder, Predicate<Object> filter) {
        this.holder = holder;
        this.filter = filter;
        CapabilityController.register(this);
    }

    @Override
    public ResourceLocation identifier() {
        return this.holder.getId();
    }

    @Override
    public C get(T holder) {
        if (holder instanceof IAttachmentHolder attachmentHolder && this.filter.test(holder)) {
            C capabilityComponent = attachmentHolder.getData(this.holder.value());
            Objects.requireNonNull(capabilityComponent, "data is null");
            return capabilityComponent;
        } else {
            throw new IllegalArgumentException("invalid capability holder: %s".formatted(holder));
        }
    }

    @Override
    public boolean isProvidedBy(Object holder) {
        return this.filter.test(holder);
    }

    @FunctionalInterface
    public interface Factory<T, C extends CapabilityComponent<T>, F extends CapabilityKey<T, C>> {

        F apply(DeferredHolder<AttachmentType<?>, AttachmentType<C>> holder, Predicate<Object> filter);
    }
}