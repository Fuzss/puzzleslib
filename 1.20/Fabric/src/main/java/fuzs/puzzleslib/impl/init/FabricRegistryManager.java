package fuzs.puzzleslib.impl.init;

import fuzs.puzzleslib.api.init.v2.RegistryReference;
import fuzs.puzzleslib.api.init.v2.builder.ExtendedMenuSupplier;
import fuzs.puzzleslib.api.init.v2.builder.PoiTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
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

    @SuppressWarnings("unchecked")
    @Override
    protected <T> RegistryReference<T> actuallyRegister(ResourceKey<? extends Registry<? super T>> registryKey, String path, Supplier<T> supplier) {
        T value = supplier.get();
        Objects.requireNonNull(value, "value is null");
        Registry<? super T> registry = (Registry<? super T>) BuiltInRegistries.REGISTRY.get(registryKey.location());
        Objects.requireNonNull(registry, "registry %s is null".formatted(registryKey));
        ResourceLocation key = this.makeKey(path);
        // PointOfInterestHelper also registers the poi type, so skip this call for that scenario
        if (!registryKey.equals(Registries.POINT_OF_INTEREST_TYPE)) Registry.register(registry, key, value);
        return new FabricRegistryReference<>(value, key, registry);
    }

    @Override
    public RegistryReference<Item> registerSpawnEggItem(RegistryReference<? extends EntityType<? extends Mob>> entityTypeReference, int backgroundColor, int highlightColor, Item.Properties itemProperties) {
        return this.registerItem(entityTypeReference.getResourceLocation().getPath() + "_spawn_egg", () -> new SpawnEggItem(entityTypeReference.get(), backgroundColor, highlightColor, itemProperties));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends AbstractContainerMenu> RegistryReference<MenuType<T>> registerExtendedMenuType(String path, Supplier<ExtendedMenuSupplier<T>> entry) {
        return this.register((ResourceKey<Registry<MenuType<T>>>) (ResourceKey<?>) Registries.MENU, path, () -> new ExtendedScreenHandlerType<>(entry.get()::create));
    }

    @Override
    public RegistryReference<PoiType> registerPoiTypeBuilder(String path, Supplier<PoiTypeBuilder> entry) {
        PoiTypeBuilder poiTypeBuilder = entry.get();
        Supplier<PoiType> supplier = () -> PointOfInterestHelper.register(this.makeKey(path), poiTypeBuilder.ticketCount(), poiTypeBuilder.searchDistance(), poiTypeBuilder.blocks());
        return this.register(Registries.POINT_OF_INTEREST_TYPE, path, supplier);
    }

    @Override
    public RegistryReference<PoiType> registerPoiType(String path, Supplier<Set<BlockState>> matchingStates, int maxTickets, int validRange) {
        return this.register(Registries.POINT_OF_INTEREST_TYPE, path, () -> PointOfInterestHelper.register(this.makeKey(path), maxTickets, validRange, matchingStates.get()));
    }
}
