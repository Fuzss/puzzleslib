package fuzs.puzzleslib.capability;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface CapabilityKey<T> {

    ResourceLocation getId();

    Class<T> getComponentClass();

    @Nullable
    <V> T get(@Nullable V provider);

    <V> Optional<T> maybeGet(@Nullable V provider);
}
