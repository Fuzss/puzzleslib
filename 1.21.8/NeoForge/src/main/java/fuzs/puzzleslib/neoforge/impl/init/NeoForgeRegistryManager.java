package fuzs.puzzleslib.neoforge.impl.init;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.brigadier.arguments.ArgumentType;
import fuzs.puzzleslib.api.init.v3.registry.MenuSupplierWithData;
import fuzs.puzzleslib.impl.init.LazyHolder;
import fuzs.puzzleslib.impl.init.RegistryManagerImpl;
import fuzs.puzzleslib.neoforge.api.core.v1.NeoForgeModContainerHelper;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class NeoForgeRegistryManager extends RegistryManagerImpl {
    private List<Consumer<RegisterEvent>> registrars = new ArrayList<>();

    public NeoForgeRegistryManager(String modId) {
        super(modId);
    }

    private void submitRegistrar(Consumer<RegisterEvent> registrar) {
        if (this.registrars.isEmpty()) {
            NeoForgeModContainerHelper.getModEventBus(this.modId).addListener(this::registerAll);
        }

        this.registrars.add(registrar);
    }

    private void registerAll(RegisterEvent event) {
        for (Consumer<RegisterEvent> registrar : this.registrars) {
            registrar.accept(event);
        }
    }

    @Override
    public <T> Holder.Reference<T> registerLazily(ResourceKey<? extends Registry<? super T>> registryKey, String path) {
        this.isWritableOrThrow();
        return new LazyHolder<>(registryKey, DeferredHolder.create(this.makeResourceKey(registryKey, path)));
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T> Holder.Reference<T> getHolderReference(ResourceKey<? extends Registry<? super T>> registryKey, String path, Supplier<T> supplier, boolean skipRegistration) {
        if (!skipRegistration) {
            this.submitRegistrar((RegisterEvent event) -> {
                event.register((ResourceKey<? extends Registry<T>>) registryKey, this.makeKey(path), () -> {
                    T value = supplier.get();
                    Objects.requireNonNull(value, "value is null");
                    return value;
                });
            });
        }
        return this.registerLazily(registryKey, path);
    }

    @Override
    protected CreativeModeTab.Builder getCreativeModeTabBuilder(boolean withSearchBar) {
        return withSearchBar ? CreativeModeTab.builder().withSearchBar() : CreativeModeTab.builder();
    }

    @Override
    public <T extends BlockEntity> Holder.Reference<BlockEntityType<T>> registerBlockEntityType(String path, BiFunction<BlockPos, BlockState, T> blockEntityFactory, Supplier<Set<Block>> validBlocks) {
        return this.register((ResourceKey<Registry<BlockEntityType<T>>>) (ResourceKey<?>) Registries.BLOCK_ENTITY_TYPE,
                path,
                () -> {
                    return new BlockEntityType<>(blockEntityFactory::apply, ImmutableSet.copyOf(validBlocks.get()));
                });
    }

    @Override
    public <T extends AbstractContainerMenu, S> Holder.Reference<MenuType<T>> registerMenuType(String path, MenuSupplierWithData<T, S> menuSupplier, StreamCodec<? super RegistryFriendlyByteBuf, S> streamCodec) {
        return this.register((ResourceKey<Registry<MenuType<T>>>) (ResourceKey<?>) Registries.MENU,
                path,
                () -> new MenuTypeWithData<>(menuSupplier, streamCodec));
    }

    @Override
    public Holder.Reference<PoiType> registerPoiType(String path, int maxTickets, int validRange, Supplier<Set<BlockState>> matchingBlockStates) {
        return this.register(Registries.POINT_OF_INTEREST_TYPE,
                path,
                () -> new PoiType(matchingBlockStates.get(), maxTickets, validRange));
    }

    @Override
    public <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>> Holder.Reference<ArgumentTypeInfo<?, ?>> registerArgumentType(String path, Class<? extends A> argumentClass, ArgumentTypeInfo<A, T> argumentTypeInfo) {
        return this.register(Registries.COMMAND_ARGUMENT_TYPE, path, () -> {
            ArgumentTypeInfos.registerByClass((Class<A>) argumentClass, argumentTypeInfo);
            return argumentTypeInfo;
        });
    }

    @Override
    public <T> Holder.Reference<EntityDataSerializer<T>> registerEntityDataSerializer(String path, Supplier<EntityDataSerializer<T>> entityDataSerializerSupplier) {
        return this.register(NeoForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, path, entityDataSerializerSupplier);
    }

    @Override
    public <T> void prepareTag(ResourceKey<? extends Registry<? super T>> registryKey, TagKey<T> tagKey) {
        this.isWritableOrThrow();
        Objects.requireNonNull(registryKey, "registry key is null");
        Objects.requireNonNull(tagKey, "tag key is null");
        this.submitRegistrar((RegisterEvent event) -> {
            Registry<T> registry = event.getRegistry((ResourceKey<? extends Registry<T>>) registryKey);
            if (registry != null) {
                BuiltInRegistries.acquireBootstrapRegistrationLookup(registry).getOrThrow(tagKey);
            }
        });
    }

    @Override
    public void freeze() {
        this.isWritableOrThrow();
        this.registrars = ImmutableList.copyOf(this.registrars);
    }

    @Override
    public boolean isFrozen() {
        return this.registrars instanceof ImmutableList<Consumer<RegisterEvent>>;
    }
}
