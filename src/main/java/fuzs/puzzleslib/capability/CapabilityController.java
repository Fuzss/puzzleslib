package fuzs.puzzleslib.capability;

import com.google.common.collect.ArrayListMultimap;
import fuzs.puzzleslib.capability.core.CapabilityDispatcher;
import fuzs.puzzleslib.capability.core.CapabilityStorage;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Function;

/**
 * helper object for registering and attaching mod capabilities, needs to be extended by every mod individually
 */
public class CapabilityController {

    /**
     * capabilities that need to be attached later
     */
    private final ArrayListMultimap<Class<?>, Pair<ResourceLocation, Function<Object, CapabilityDispatcher<?>>>> capabilityEntries = ArrayListMultimap.create();

    /**
     * private singleton constructor
     * create new object just for registering and adding listeners
     */
    private CapabilityController() {

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onAttachCapabilities(final AttachCapabilitiesEvent<?> evt) {

        this.capabilityEntries.get((Class<?>) evt.getGenericType()).forEach(capability -> {

            Optional<CapabilityDispatcher<?>> optional = Optional.ofNullable(capability.getRight().apply(evt.getObject()));
            optional.ifPresent(dispatcher -> evt.addCapability(capability.getLeft(), dispatcher));
        });
    }

    /**
     * register capabilities by calling {@link #register(Class, Callable)}
     * then add them to a map for attaching later
     * @param genericType generic type of capability
     * @param path registry path
     * @param capabilityType interface for this capability
     * @param capabilityFactory capability factory
     * @param function function for providing dispatcher, might return null when object is not compatible
     * @param <T> capability type
     */
    private <T extends INBTSerializable<CompoundNBT>, S extends CapabilityProvider<S>> void addCapability(Class<S> genericType, ResourceLocation path, Class<T> capabilityType, Callable<T> capabilityFactory, Function<Object, CapabilityDispatcher<?>> function) {

        register(capabilityType, capabilityFactory);
        this.capabilityEntries.put(genericType, Pair.of(path, function));
    }

    /**
     * register capabilities for item stacks
     * @param path registry path
     * @param capabilityType interface for this capability
     * @param capabilityFactory capability factory
     * @param function function for providing dispatcher, might return null when object is not compatible
     * @param <T> capability type
     */
    public <T extends INBTSerializable<CompoundNBT>> void addItemStackCapability(ResourceLocation path, Class<T> capabilityType, Callable<T> capabilityFactory, Function<Object, CapabilityDispatcher<?>> function) {

        this.addCapability(ItemStack.class, path, capabilityType, capabilityFactory, function);
    }

    /**
     * register capabilities for entities
     * @param path registry path
     * @param capabilityType interface for this capability
     * @param capabilityFactory capability factory
     * @param function function for providing dispatcher, might return null when object is not compatible
     * @param <T> capability type
     */
    public <T extends INBTSerializable<CompoundNBT>> void addEntityCapability(ResourceLocation path, Class<T> capabilityType, Callable<T> capabilityFactory, Function<Object, CapabilityDispatcher<?>> function) {

        this.addCapability(Entity.class, path, capabilityType, capabilityFactory, function);
    }

    /**
     * register capabilities for tile entities
     * @param path registry path
     * @param capabilityType interface for this capability
     * @param capabilityFactory capability factory
     * @param function function for providing dispatcher, might return null when object is not compatible
     * @param <T> capability type
     */
    public <T extends INBTSerializable<CompoundNBT>> void addTileEntityCapability(ResourceLocation path, Class<T> capabilityType, Callable<T> capabilityFactory, Function<Object, CapabilityDispatcher<?>> function) {

        this.addCapability(TileEntity.class, path, capabilityType, capabilityFactory, function);
    }

    /**
     * register capabilities for worlds
     * @param path registry path
     * @param capabilityType interface for this capability
     * @param capabilityFactory capability factory
     * @param function function for providing dispatcher, might return null when object is not compatible
     * @param <T> capability type
     */
    public <T extends INBTSerializable<CompoundNBT>> void addWorldCapability(ResourceLocation path, Class<T> capabilityType, Callable<T> capabilityFactory, Function<Object, CapabilityDispatcher<?>> function) {

        this.addCapability(World.class, path, capabilityType, capabilityFactory, function);
    }

    /**
     * register capabilities for chunks
     * @param path registry path
     * @param capabilityType interface for this capability
     * @param capabilityFactory capability factory
     * @param function function for providing dispatcher, might return null when object is not compatible
     * @param <T> capability type
     */
    public <T extends INBTSerializable<CompoundNBT>> void addChunkCapability(ResourceLocation path, Class<T> capabilityType, Callable<T> capabilityFactory, Function<Object, CapabilityDispatcher<?>> function) {

        this.addCapability(Chunk.class, path, capabilityType, capabilityFactory, function);
    }

    /**
     * register new capability to {@link CapabilityManager} instance, to be called from {@link #addCapability(Class, ResourceLocation, Class, Callable, Function)}
     * @param type class of generic type
     * @param factory method reference
     * @param <T> generic type
     */
    private static <T> void register(Class<T> type, Callable<? extends T> factory) {

        CapabilityManager.INSTANCE.register(type, new CapabilityStorage<>(), factory);
    }

    /**
     * overload to avoid always null problems
     * @param provider provider object
     * @param capability capability to get
     * @param <T> capability type
     * @return capability object
     */
    @Nonnull
    public static <T> LazyOptional<T> getCapability(ICapabilityProvider provider, Capability<T> capability) {

        return provider.getCapability(capability);
    }

    /**
     * @return {@link CapabilityController} instance
     */
    public static CapabilityController getInstance() {

        return CapabilityController.CapabilityControllerHolder.INSTANCE;
    }

    /**
     * instance holder class for lazy and thread-safe initialization
     */
    private static class CapabilityControllerHolder {

        private static final CapabilityController INSTANCE = new CapabilityController();

    }

}
