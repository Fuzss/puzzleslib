package fuzs.puzzleslib.core;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraftforge.registries.ForgeRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public record ForgeRegistryAccess<T>(ForgeRegistry<T> registry) implements RegistryAccess<T> {

    @Override
    public ResourceKey<? extends Registry<T>> key() {
        return this.registry().getRegistryKey();
    }

    @Override
    public @Nullable ResourceLocation getKey(T object) {
        return this.registry().getKey(object);
    }

    @Override
    public Optional<ResourceKey<T>> getResourceKey(T object) {
        return this.registry().getResourceKey(object);
    }

    @Override
    public int getId(@Nullable T object) {
        return this.registry().getID(object);
    }

    @Override
    public @Nullable T get(@Nullable ResourceKey<T> resourceKey) {
        return this.registry().;
    }

    @Override
    public @Nullable T get(@Nullable ResourceLocation resourceLocation) {
        return null;
    }

    @Override
    public Set<ResourceLocation> keySet() {
        return this.registry().getKeys();
    }

    @Override
    public Set<Map.Entry<ResourceKey<T>, T>> entrySet() {
        return this.registry().getEntries();
    }

    @Override
    public Set<ResourceKey<T>> registryKeySet() {
        return this.registry().getDelegate();
    }

    @Override
    public Optional<Holder<T>> getRandom(RandomSource randomSource) {
        return this.registry().hold;
    }

    @Override
    public boolean containsKey(ResourceLocation resourceLocation) {
        return this.registry().containsKey(resourceLocation);
    }

    @Override
    public boolean containsKey(ResourceKey<T> resourceKey) {
        return this.registry().containsKey();
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return this.registry().iterator();
    }
}
