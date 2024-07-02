package fuzs.puzzleslib.neoforge.api.init.v3.capability;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.neoforge.api.core.v1.NeoForgeModContainerHelper;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
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

import java.util.function.BiConsumer;

/**
 * A utility class for conveniently registering handlers for {@link Capabilities}.
 */
public final class NeoForgeCapabilityHelper {

    private NeoForgeCapabilityHelper() {

    }

    /**
     * Register a {@link ICapabilityProvider} for implementations of {@link ChestBlock} to support handling double chests automatically.
     *
     * @param chestBlocks chest blocks to register
     */
    public static void registerChestBlock(Holder<? extends ChestBlock>... chestBlocks) {
        register((RegisterCapabilitiesEvent registerCapabilitiesEvent, ChestBlock chestBlock) -> {
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
    public static <T extends BlockEntity & Container> void registerBlockEntityContainer(Holder<? extends BlockEntityType<? extends T>>... blockEntityTypes) {
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
    public static <T extends BlockEntity & WorldlyContainer> void registerWorldlyBlockEntityContainer(Holder<? extends BlockEntityType<? extends T>>... blockEntityTypes) {
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
    public static <T extends BlockEntity & WorldlyContainer> void registerRestrictedBlockEntityContainer(Holder<? extends BlockEntityType<? extends T>>... blockEntityTypes) {
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
    public static <T extends BlockEntity> void registerBlockEntity(ICapabilityProvider<T, Direction, IItemHandler> capabilityProvider, Holder<? extends BlockEntityType<? extends T>>... blockEntityTypes) {
        register((RegisterCapabilitiesEvent evt, BlockEntityType<? extends T> blockEntityType) -> {
            evt.registerBlockEntity(Capabilities.ItemHandler.BLOCK, blockEntityType, capabilityProvider);
        }, blockEntityTypes);
    }

    /**
     * Register a {@link ICapabilityProvider} for {@link EntityType}s.
     * <p>An example is {@link net.minecraft.world.entity.vehicle.ChestBoat} and {@link net.minecraft.world.entity.vehicle.MinecartHopper}.
     *
     * @param entityTypes entity types to register
     * @param <T>         entity super type
     */
    @SafeVarargs
    public static <T extends Entity & Container> void registerEntityContainer(Holder<? extends EntityType<? extends T>>... entityTypes) {
        NeoForgeCapabilityHelper.register((RegisterCapabilitiesEvent evt, EntityType<? extends T> entityType) -> {
            evt.registerEntity(Capabilities.ItemHandler.ENTITY, entityType, (T entity, Void aVoid) -> {
                return new InvWrapper(entity);
            });
            evt.registerEntity(Capabilities.ItemHandler.ENTITY_AUTOMATION, entityType, (T entity, @Nullable Direction direction) -> {
                return new InvWrapper(entity);
            });
        }, entityTypes);
    }

    /**
     * Helper method for registering types to {@link RegisterCapabilitiesEvent}.
     *
     * @param consumer    capability registration context
     * @param types       capability provider values
     * @param <T>         capability provider type
     */
    @SafeVarargs
    public static <T> void register(BiConsumer<RegisterCapabilitiesEvent, T> consumer, Holder<? extends T>... types) {
        Preconditions.checkState(types.length > 0, "capability provider types is empty");
        ResourceLocation resourceLocation = types[0].unwrapKey().orElseThrow().location();
        NeoForgeModContainerHelper.getOptionalModEventBus(resourceLocation.getNamespace()).ifPresent(eventBus -> {
            eventBus.addListener((final RegisterCapabilitiesEvent evt) -> {
                for (Holder<? extends T> holder : types) {
                    consumer.accept(evt, holder.value());
                }
            });
        });
    }
}
