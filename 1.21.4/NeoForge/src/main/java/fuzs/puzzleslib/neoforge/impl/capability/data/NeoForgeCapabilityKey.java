package fuzs.puzzleslib.neoforge.impl.capability.data;

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
import java.util.function.BiFunction;
import java.util.function.Predicate;

public abstract class NeoForgeCapabilityKey<T, C extends CapabilityComponent<T>> implements CapabilityKey<T, C> {
    private final DeferredHolder<AttachmentType<?>, AttachmentType<C>> holder;
    private final Predicate<Object> filter;

    public NeoForgeCapabilityKey(DeferredHolder<AttachmentType<?>, AttachmentType<C>> holder, Predicate<Object> filter) {
        this.holder = holder;
        this.filter = filter;
        GlobalCapabilityRegister.register(this);
    }

    @Override
    public ResourceLocation identifier() {
        return this.holder.getId();
    }

    @Override
    public C get(@NotNull T holder) {
        Objects.requireNonNull(holder, "holder is null");
        if (holder instanceof IAttachmentHolder attachmentHolder && this.isProvidedBy(holder)) {
            C capabilityComponent = attachmentHolder.getData(this.holder.value());
            Objects.requireNonNull(capabilityComponent, "data is null");
            return capabilityComponent;
        } else {
            throw new IllegalArgumentException("Invalid capability holder: %s".formatted(holder));
        }
    }

    @Override
    public boolean isProvidedBy(@Nullable Object holder) {
        return holder instanceof IAttachmentHolder && this.filter.test(holder);
    }

    @FunctionalInterface
    public interface Factory<T, C extends CapabilityComponent<T>, K extends CapabilityKey<T, C>> extends BiFunction<DeferredHolder<AttachmentType<?>, AttachmentType<C>>, Predicate<Object>, K> {

    }
}
