package fuzs.puzzleslib.forge.impl.init;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.mojang.brigadier.arguments.ArgumentType;
import fuzs.puzzleslib.api.init.v3.registry.ExtendedMenuSupplier;
import fuzs.puzzleslib.forge.api.core.v1.ForgeModContainerHelper;
import fuzs.puzzleslib.impl.init.LazyHolder;
import fuzs.puzzleslib.impl.init.RegistryManagerImpl;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.EntityDataSerializer;
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
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

public final class ForgeRegistryManager extends RegistryManagerImpl {
    @Nullable
    private final IEventBus eventBus;
    private final Map<ResourceKey<? extends Registry<?>>, DeferredRegister<?>> registers = Maps.newIdentityHashMap();

    public ForgeRegistryManager(String modId) {
        super(modId);
        this.eventBus = ForgeModContainerHelper.getOptionalModEventBus(modId).orElse(null);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T> Holder.Reference<T> getHolderReference(ResourceKey<? extends Registry<? super T>> registryKey, String path, Supplier<T> supplier, boolean skipRegistration) {
        Preconditions.checkState(!skipRegistration, "Skipping registration is not supported on Forge");
        DeferredRegister<T> registrar = (DeferredRegister<T>) this.registers.computeIfAbsent(registryKey, $ -> {
            DeferredRegister<T> deferredRegister = DeferredRegister.create((ResourceKey<? extends Registry<T>>) registryKey,
                    this.modId
            );
            Objects.requireNonNull(this.eventBus, "mod event bus for %s is null".formatted(this.modId));
            deferredRegister.register(this.eventBus);
            return deferredRegister;
        });
        RegistryObject<T> registryObject = registrar.register(path, () -> {
            T value = supplier.get();
            Objects.requireNonNull(value, "value is null");
            return value;
        });
        return new LazyHolder<>(registryKey,
                registryObject.getKey(),
                () -> registryObject.getHolder().orElseThrow(() -> {
                    return new IllegalStateException("Missing key in " + registryKey + ": " + registryObject.getKey());
                })
        );
    }

    @Override
    public Holder.Reference<Item> registerSpawnEggItem(Holder<? extends EntityType<? extends Mob>> entityTypeReference, int backgroundColor, int highlightColor, Item.Properties itemProperties) {
        return this.registerItem(entityTypeReference.unwrapKey().orElseThrow().location().getPath() + "_spawn_egg",
                () -> new ForgeSpawnEggItem(entityTypeReference, backgroundColor, highlightColor, itemProperties)
        );
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends AbstractContainerMenu> Holder.Reference<MenuType<T>> registerExtendedMenuType(String path, Supplier<ExtendedMenuSupplier<T>> entry) {
        return this.register((ResourceKey<Registry<MenuType<T>>>) (ResourceKey<?>) Registries.MENU,
                path,
                () -> IForgeMenuType.create(entry.get()::create)
        );
    }

    @Override
    public Holder.Reference<PoiType> registerPoiType(String path, Supplier<Set<BlockState>> matchingStates, int maxTickets, int validRange) {
        return this.register(Registries.POINT_OF_INTEREST_TYPE,
                path,
                () -> new PoiType(matchingStates.get(), maxTickets, validRange)
        );
    }

    @Override
    public <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>> Holder.Reference<ArgumentTypeInfo<?, ?>> registerArgumentType(String path, Class<? extends A> argumentClass, ArgumentTypeInfo<A, T> argumentTypeInfo) {
        return this.register(Registries.COMMAND_ARGUMENT_TYPE, path, () -> {
            ArgumentTypeInfos.registerByClass((Class<A>) argumentClass, argumentTypeInfo);
            return argumentTypeInfo;
        });
    }

    @Override
    public <T> Holder.Reference<EntityDataSerializer<T>> registerEntityDataSerializer(String path, Supplier<EntityDataSerializer<T>> entry) {
        return this.register(ForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, path, entry);
    }
}
