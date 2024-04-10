package fuzs.puzzleslib.neoforge.api.init.v3.capability;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.init.v3.registry.RegistryHelperV2;
import fuzs.puzzleslib.neoforge.api.core.v1.NeoForgeModContainerHelper;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * A utility class for conveniently registering handlers for {@link Capabilities}.
 *
 * @deprecated replace with {@link NeoForgeCapabilityHelperV2}
 */
@Deprecated(forRemoval = true)
public final class NeoForgeCapabilityHelper {

    private NeoForgeCapabilityHelper() {

    }

    /**
     * Register a {@link ICapabilityProvider} for implementations of {@link ChestBlock} to support handling double chests automatically.
     *
     * @param chestBlocks chest blocks to register
     */
    public static void registerChestBlock(ChestBlock... chestBlocks) {
        register(Registries.BLOCK, (RegisterCapabilitiesEvent registerCapabilitiesEvent, ChestBlock chestBlock) -> {
            registerCapabilitiesEvent.registerBlock(Capabilities.ItemHandler.BLOCK, (level, pos, state, blockEntity, side) -> {
                Container container = ChestBlock.getContainer((ChestBlock) state.getBlock(), state, level, pos, true);
                return new InvWrapper(container);
            }, chestBlock);
        }, chestBlocks);
    }

    /**
     * Register a {@link ICapabilityProvider} for {@link BlockEntityType}s that implement {@link Container}.
     * <p>An example is {@link net.minecraft.world.level.block.entity.BarrelBlockEntity}.
     *
     * @param blockEntityTypes block entity types to register
     * @param <T>              block entity super type
     */
    @SafeVarargs
    public static <T extends BlockEntity & Container> void registerBlockEntityContainer(BlockEntityType<? extends T>... blockEntityTypes) {
        registerBlockEntity((T blockEntity, @Nullable Direction direction) -> {
            return new InvWrapper(blockEntity);
        }, blockEntityTypes);
    }

    /**
     * Register a {@link ICapabilityProvider} for {@link BlockEntityType}s that implement {@link WorldlyContainer} for filtering inputs &amp; outputs based on direction.
     * <p>An example is {@link net.minecraft.world.level.block.entity.BrewingStandBlockEntity}.
     *
     * @param blockEntityTypes block entity types to register
     * @param <T>              block entity super type
     */
    @SafeVarargs
    public static <T extends BlockEntity & WorldlyContainer> void registerWorldlyBlockEntityContainer(BlockEntityType<? extends T>... blockEntityTypes) {
        registerBlockEntity((T blockEntity, @Nullable Direction direction) -> {
            return direction != null ? new SidedInvWrapper(blockEntity, direction) : new InvWrapper(blockEntity);
        }, blockEntityTypes);
    }

    /**
     * Register a {@link ICapabilityProvider} for {@link BlockEntityType}s that implement {@link WorldlyContainer} without filtering inputs &amp; outputs based on direction.
     * <p>An example is {@link net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity},
     * which uses {@link WorldlyContainer#canPlaceItemThroughFace(int, ItemStack, Direction)} merely to filter incoming items, ignoring the passed direction.
     *
     * @param blockEntityTypes block entity types to register
     * @param <T>              block entity super type
     */
    @SafeVarargs
    public static <T extends BlockEntity & WorldlyContainer> void registerShulkerBoxLikeBlockEntity(BlockEntityType<? extends T>... blockEntityTypes) {
        registerBlockEntity((T blockEntity, @Nullable Direction direction) -> {
            return new SidedInvWrapper(blockEntity, null);
        }, blockEntityTypes);
    }

    /**
     * Register a {@link ICapabilityProvider} for {@link BlockEntityType}s.
     *
     * @param capabilityProvider capability provider to register
     * @param blockEntityTypes   block entity types to register
     * @param <T>                block entity super type
     */
    @SafeVarargs
    public static <T extends BlockEntity> void registerBlockEntity(ICapabilityProvider<T, Direction, IItemHandler> capabilityProvider, BlockEntityType<? extends T>... blockEntityTypes) {
        register(Registries.BLOCK_ENTITY_TYPE, (RegisterCapabilitiesEvent registerCapabilitiesEvent, BlockEntityType<? extends T> blockEntityType) -> {
            registerCapabilitiesEvent.registerBlockEntity(Capabilities.ItemHandler.BLOCK, blockEntityType, capabilityProvider);
        }, blockEntityTypes);
    }

    /**
     * Helper method for registering types to {@link RegisterCapabilitiesEvent}.
     *
     * @param registryKey registry associated with type values
     * @param consumer    capability registration context
     * @param types       capability provider values
     * @param <T>         capability provider type
     */
    @SafeVarargs
    public static <T> void register(ResourceKey<? extends Registry<? super T>> registryKey, BiConsumer<RegisterCapabilitiesEvent, T> consumer, T... types) {
        Preconditions.checkState(types.length > 0, "capability provider types is empty");
        ResourceLocation resourceLocation = RegistryHelperV2.findBuiltInRegistry(registryKey).getKey(types[0]);
        Objects.requireNonNull(resourceLocation, "resource location is null");
        NeoForgeModContainerHelper.getModEventBus(resourceLocation.getNamespace()).addListener((final RegisterCapabilitiesEvent evt) -> {
            for (T blockEntityType : types) {
                consumer.accept(evt, blockEntityType);
            }
        });
    }
}
