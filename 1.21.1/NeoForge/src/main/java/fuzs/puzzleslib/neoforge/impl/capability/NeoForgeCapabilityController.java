package fuzs.puzzleslib.neoforge.impl.capability;

import fuzs.puzzleslib.api.capability.v3.CapabilityController;
import fuzs.puzzleslib.api.capability.v3.data.*;
import fuzs.puzzleslib.impl.capability.GlobalCapabilityRegister;
import fuzs.puzzleslib.neoforge.api.core.v1.NeoForgeModContainerHelper;
import fuzs.puzzleslib.neoforge.impl.capability.data.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class NeoForgeCapabilityController implements CapabilityController {
    private final DeferredRegister<AttachmentType<?>> registrar;

    public NeoForgeCapabilityController(String modId) {
        this.registrar = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, modId);
        NeoForgeModContainerHelper.getOptionalModEventBus(modId).ifPresent(this.registrar::register);
    }

    @Override
    public <T extends Entity, C extends CapabilityComponent<T>> EntityCapabilityKey.Mutable<T, C> registerEntityCapability(String identifier, Class<C> capabilityType, Supplier<C> capabilityFactory, Class<T> entityType) {
        return this.registerCapability(Entity.class, identifier, capabilityFactory, entityType::isInstance, (NeoForgeCapabilityKey.Factory<T, C, NeoForgeEntityCapabilityKey<T, C>>) NeoForgeEntityCapabilityKey::new);
    }

    @Override
    public <T extends BlockEntity, C extends CapabilityComponent<T>> BlockEntityCapabilityKey<T, C> registerBlockEntityCapability(String identifier, Class<C> capabilityType, Supplier<C> capabilityFactory, Class<T> blockEntityType) {
        return this.registerCapability(BlockEntity.class, identifier, capabilityFactory, blockEntityType::isInstance, (NeoForgeCapabilityKey.Factory<T, C, NeoForgeBlockEntityCapabilityKey<T, C>>) NeoForgeBlockEntityCapabilityKey::new);
    }

    @Override
    public <C extends CapabilityComponent<LevelChunk>> LevelChunkCapabilityKey<C> registerLevelChunkCapability(String identifier, Class<C> capabilityType, Supplier<C> capabilityFactory) {
        return this.registerCapability(LevelChunk.class, identifier, capabilityFactory, (NeoForgeCapabilityKey.Factory<LevelChunk, C, NeoForgeLevelChunkCapabilityKey<C>>) NeoForgeLevelChunkCapabilityKey::new);
    }

    @Override
    public <C extends CapabilityComponent<Level>> LevelCapabilityKey<C> registerLevelCapability(String identifier, Class<C> capabilityType, Supplier<C> capabilityFactory) {
        return this.registerCapability(Level.class, identifier, capabilityFactory, (NeoForgeCapabilityKey.Factory<Level, C, NeoForgeLevelCapabilityKey<C>>) NeoForgeLevelCapabilityKey::new);
    }

    private <T, C extends CapabilityComponent<T>, K extends CapabilityKey<T, C>> K registerCapability(Class<? extends IAttachmentHolder> holderType, String identifier, Supplier<C> capabilityFactory, NeoForgeCapabilityKey.Factory<T, C, K> capabilityKeyFactory) {
        return this.registerCapability(holderType, identifier, capabilityFactory, holderType::isInstance, capabilityKeyFactory);
    }

    @SuppressWarnings("unchecked")
    private <T, C extends CapabilityComponent<T>, K extends CapabilityKey<T, C>> K registerCapability(Class<? extends IAttachmentHolder> holderType, String identifier, Supplier<C> capabilityFactory, Predicate<Object> filter, NeoForgeCapabilityKey.Factory<T, C, K> capabilityKeyFactory) {
        GlobalCapabilityRegister.testHolderType(holderType);
        Object[] capabilityKey = new Object[1];
        DeferredHolder<AttachmentType<?>, AttachmentType<C>> holder = this.registrar.register(identifier, () -> {
            return AttachmentType.builder((IAttachmentHolder attachmentHolder) -> {
                C capabilityComponent = capabilityFactory.get();
                Objects.requireNonNull(capabilityComponent, "capability component is null");
                capabilityComponent.initialize((CapabilityKey<T, CapabilityComponent<T>>) capabilityKey[0], (T) attachmentHolder);
                return capabilityComponent;
            }).serialize(new IAttachmentSerializer<>() {

                @Override
                public C read(IAttachmentHolder attachmentHolder, Tag tag, HolderLookup.Provider registries) {
                    C capabilityComponent = capabilityFactory.get();
                    Objects.requireNonNull(capabilityComponent, "capability component is null");
                    capabilityComponent.initialize((CapabilityKey<T, CapabilityComponent<T>>) capabilityKey[0], (T) attachmentHolder);
                    capabilityComponent.read((CompoundTag) tag, registries);
                    return capabilityComponent;
                }

                @Override
                public Tag write(C object, HolderLookup.Provider registries) {
                    return object.toCompoundTag(registries);
                }
            }).build();
        });
        return (K) (capabilityKey[0] = capabilityKeyFactory.apply(holder, filter));
    }
}
