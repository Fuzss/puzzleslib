package fuzs.puzzleslib.neoforge.impl.init;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import fuzs.puzzleslib.impl.init.RegistryManagerV2Impl;
import fuzs.puzzleslib.neoforge.api.core.v1.ModContainerHelper;
import fuzs.puzzleslib.api.init.v2.RegistryReference;
import fuzs.puzzleslib.api.init.v2.builder.ExtendedMenuSupplier;
import fuzs.puzzleslib.api.init.v2.builder.PoiTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

public final class ForgeRegistryManagerV2 extends RegistryManagerV2Impl {
    @Nullable
    private final IEventBus eventBus;
    private final Map<ResourceKey<? extends Registry<?>>, DeferredRegister<?>> deferredRegisters = Maps.newIdentityHashMap();

    public ForgeRegistryManagerV2(String modId) {
        super(modId);
        this.eventBus = ModContainerHelper.getOptionalModEventBus(modId).orElse(null);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T> RegistryReference<T> actuallyRegister(ResourceKey<? extends Registry<? super T>> registryKey, String path, Supplier<T> supplier) {
        DeferredRegister<T> register = (DeferredRegister<T>) this.deferredRegisters.computeIfAbsent(registryKey, $ -> {
            DeferredRegister<T> deferredRegister = DeferredRegister.create((ResourceKey<? extends Registry<T>>) registryKey, this.modId);
            Objects.requireNonNull(this.eventBus, "mod event bus for %s is null".formatted(this.modId));
            deferredRegister.register(this.eventBus);
            return deferredRegister;
        });
        if (StringUtils.isEmpty(path)) throw new IllegalArgumentException("path is invalid");
        RegistryObject<T> registryObject = register.register(path, supplier);
        return new ForgeRegistryReference<>(registryObject, (ResourceKey<? extends Registry<T>>) registryKey);
    }

    @Override
    public RegistryReference<Item> registerSpawnEggItem(RegistryReference<? extends EntityType<? extends Mob>> entityTypeReference, int backgroundColor, int highlightColor, Item.Properties itemProperties) {
        return this.registerItem(entityTypeReference.getResourceLocation().getPath() + "_spawn_egg", () -> new ForgeSpawnEggItem(entityTypeReference::get, backgroundColor, highlightColor, itemProperties));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends AbstractContainerMenu> RegistryReference<MenuType<T>> registerExtendedMenuType(String path, Supplier<ExtendedMenuSupplier<T>> entry) {
        return this.register((ResourceKey<Registry<MenuType<T>>>) (ResourceKey<?>) Registries.MENU, path, () -> IForgeMenuType.create(entry.get()::create));
    }

    @Override
    public RegistryReference<PoiType> registerPoiTypeBuilder(String path, Supplier<PoiTypeBuilder> entry) {
        return this.register(Registries.POINT_OF_INTEREST_TYPE, path, () -> {
            PoiTypeBuilder poiTypeBuilder = entry.get();
            return new PoiType(ImmutableSet.copyOf(poiTypeBuilder.blocks()), poiTypeBuilder.ticketCount(), poiTypeBuilder.searchDistance());
        });
    }

    @Override
    public RegistryReference<PoiType> registerPoiType(String path, Supplier<Set<BlockState>> matchingStates, int maxTickets, int validRange) {
        return this.register(Registries.POINT_OF_INTEREST_TYPE, path, () -> new PoiType(matchingStates.get(), maxTickets, validRange));
    }
}
