package fuzs.puzzleslib.impl.init;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.resources.ResourceKey;

import java.util.Objects;

public final class DirectReferenceHolder<T> extends Holder.Reference<T> {

    public DirectReferenceHolder(ResourceKey<T> key, T value) {
        super(Type.STAND_ALONE, null, key, value);
        Objects.requireNonNull(key, "key is null");
        Objects.requireNonNull(value, "value is null");
    }

    @Override
    public boolean canSerializeIn(HolderOwner<T> owner) {
        return true;
    }

    @Override
    public void bindValue(T value) {
        throw new UnsupportedOperationException();
    }
}
