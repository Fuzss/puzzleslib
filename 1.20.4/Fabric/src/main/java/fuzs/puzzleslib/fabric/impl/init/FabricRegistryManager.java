package fuzs.puzzleslib.fabric.impl.init;

import fuzs.puzzleslib.api.init.v3.registry.ExtendedMenuSupplier;
import fuzs.puzzleslib.api.init.v3.registry.RegistryHelper;
import fuzs.puzzleslib.impl.init.RegistryManagerImpl;
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

public final class FabricRegistryManager extends RegistryManagerImpl {

    public FabricRegistryManager(String modId) {
        super(modId);
    }

    @Override
    protected <T> Holder.Reference<T> getHolderReference(ResourceKey<? extends Registry<? super T>> registryKey, String path, Supplier<T> supplier) {
        T value = supplier.get();
        Objects.requireNonNull(value, "value is null");
        Registry<T> registry = RegistryHelper.findBuiltInRegistry(registryKey);
        Holder.Reference<T> holder;
        // PointOfInterestHelper also registers the poi type, so skip this call for that scenario
        if (!registryKey.equals(Registries.POINT_OF_INTEREST_TYPE)) {
            holder = Registry.registerForHolder(registry, this.makeKey(path), value);
        } else {
            holder = registry.getHolderOrThrow(this.makeResourceKey(registryKey, path));
        }
        // bind the value immediately, so we can use it
        // Fabric Api should already do this for us, but better safe than sorry ¯\_(ツ)_/¯
        if (!holder.isBound()) holder.bindValue(value);
        return holder;
    }

    @Override
    public Holder.Reference<Item> registerSpawnEggItem(Holder<? extends EntityType<? extends Mob>> entityTypeReference, int backgroundColor, int highlightColor, Item.Properties itemProperties) {
        return this.registerItem(entityTypeReference.unwrapKey().orElseThrow().location().getPath() + "_spawn_egg", () -> new SpawnEggItem(entityTypeReference.value(), backgroundColor, highlightColor, itemProperties));
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
