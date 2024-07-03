package fuzs.puzzleslib.impl.init;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * A {@link Holder} implementation similar to DeferredHolder on NeoForge, but extending
 * {@link net.minecraft.core.Holder.Reference} to allow for using {@link Reference#key()}.
 * <p>
 * Also supports lazy initialization on Fabric &amp; Forge.
 */
public final class LazyHolder<T> extends Holder.Reference<T> {
    @Nullable
    private Supplier<Holder<T>> supplier;
    private Holder<T> holder;

    public LazyHolder(ResourceKey<? extends Registry<? super T>> registryKey, Holder<T> holder) {
        this(registryKey, holder.unwrapKey().orElseThrow(), () -> holder);
    }

    public LazyHolder(ResourceKey<? extends Registry<? super T>> registryKey, ResourceKey<T> key, Supplier<Holder<T>> supplier) {
        super(Holder.Reference.Type.STAND_ALONE, new HolderOwner<>() {
            @Override
            public String toString() {
                return registryKey.toString();
            }
        }, key, null);
        Objects.requireNonNull(registryKey, "registry key is null");
        Objects.requireNonNull(key, "key is null");
        Objects.requireNonNull(supplier, "supplier is null");
        this.supplier = supplier;
    }

    private void bindHolder(boolean failIfNull) {
        if (this.supplier != null) {
            Holder<T> holder = this.supplier.get();
            if (holder != null) {
                this.holder = holder;
                super.bindValue(this.holder.value());
                this.supplier = null;
            } else if (failIfNull) {
                throw new NullPointerException("holder is null");
            }
        }
    }

    @Override
    public void bindValue(T value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void bindTags(Collection<TagKey<T>> tags) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T value() {
        this.bindHolder(true);
        return super.value();
    }

    @Override
    public boolean isBound() {
        this.bindHolder(false);
        return super.isBound();
    }

    @Override
    public boolean is(TagKey<T> tagKey) {
        this.bindHolder(true);
        return this.holder.is(tagKey);
    }

    @Override
    public Stream<TagKey<T>> tags() {
        this.bindHolder(true);
        return this.holder.tags();
    }

    @Override
    public boolean canSerializeIn(HolderOwner<T> owner) {
        this.bindHolder(true);
        return this.holder.canSerializeIn(owner);
    }

    @Override
    public String toString() {
        return "Reference{" + this.key() + (this.supplier == null ? "=" + this.value() : "") + "}";
    }
}
