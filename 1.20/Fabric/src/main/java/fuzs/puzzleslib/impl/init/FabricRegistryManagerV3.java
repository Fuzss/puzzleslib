package fuzs.puzzleslib.impl.init;

import fuzs.puzzleslib.api.init.v2.builder.ExtendedMenuSupplier;
import fuzs.puzzleslib.mixin.accessor.ReferenceAccessor;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

public final class FabricRegistryManagerV3 extends RegistryManagerV3Impl {

    public FabricRegistryManagerV3(String modId) {
        super(modId);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Holder.Reference<T> getHolder(ResourceKey<? extends Registry<? super T>> registryKey, String path) {
        Registry<T> registry = findRegistry(registryKey);
        ResourceKey<T> resourceKey = this.makeResourceKey(registryKey, path);
        Holder.Reference<T> holder = registry.getHolder(resourceKey).orElseThrow();
        if (!holder.isBound()) {
            T value = registry.get(resourceKey);
            Objects.requireNonNull(value, "value is null");
            // bind the value immediately, so we can use it, Forge does this, too
            ((ReferenceAccessor<T>) holder).puzzleslib$callBindValue(value);
        }
        return holder;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T> Holder.Reference<T> _register(ResourceKey<? extends Registry<? super T>> registryKey, String path, Supplier<T> supplier) {
        T value = supplier.get();
        Objects.requireNonNull(value, "value is null");
        Holder.Reference<T> holder;
        // PointOfInterestHelper also registers the poi type, so skip this call for that scenario
        if (!registryKey.equals(Registries.POINT_OF_INTEREST_TYPE)) {
            holder = Registry.registerForHolder(findRegistry(registryKey), this.makeKey(path), value);
        } else {
            holder = this.getHolder(registryKey, path);
        }
        // bind the value immediately, so we can use it, Forge does this, too
        if (!holder.isBound()) ((ReferenceAccessor<T>) holder).puzzleslib$callBindValue(value);
        return holder;
    }

    @Override
    public Holder.Reference<Item> registerSpawnEggItem(Holder.Reference<? extends EntityType<? extends Mob>> entityTypeReference, int backgroundColor, int highlightColor, Item.Properties itemProperties) {
        return this.registerItem(entityTypeReference.key().location().getPath() + "_spawn_egg", () -> new SpawnEggItem(entityTypeReference.value(), backgroundColor, highlightColor, itemProperties));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends AbstractContainerMenu> Holder.Reference<MenuType<T>> registerExtendedMenuType(String path, Supplier<ExtendedMenuSupplier<T>> entry) {
        return this.register((ResourceKey<Registry<MenuType<T>>>) (ResourceKey<?>) Registries.MENU, path, () -> new ExtendedScreenHandlerType<>(entry.get()::create));
    }

    @Override
    public Holder.Reference<PoiType> registerPoiType(String path, Supplier<Set<BlockState>> matchingStates, int maxTickets, int validRange) {
        return this.register(Registries.POINT_OF_INTEREST_TYPE, path, () -> PointOfInterestHelper.register(this.makeKey(path), maxTickets, validRange, matchingStates.get()));
    }
}
