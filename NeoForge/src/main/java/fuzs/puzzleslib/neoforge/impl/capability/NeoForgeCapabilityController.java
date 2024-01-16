package fuzs.puzzleslib.neoforge.impl.capability;

import fuzs.puzzleslib.api.capability.v3.CapabilityController;
import fuzs.puzzleslib.api.capability.v3.data.*;
import fuzs.puzzleslib.impl.capability.GlobalCapabilityRegister;
import fuzs.puzzleslib.neoforge.api.core.v1.NeoForgeModContainerHelper;
import fuzs.puzzleslib.neoforge.impl.capability.data.*;
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

    private <T, C1 extends CapabilityComponent<T>, C2 extends CapabilityKey<T, C1>> C2 registerCapability(Class<? extends IAttachmentHolder> holderType, String identifier, Supplier<C1> capabilityFactory, NeoForgeCapabilityKey.Factory<T, C1, C2> capabilityKeyFactory) {
        return this.registerCapability(holderType, identifier, capabilityFactory, holderType::isInstance, capabilityKeyFactory);
    }

    private <T, C1 extends CapabilityComponent<T>, C2 extends CapabilityKey<T, C1>> C2 registerCapability(Class<? extends IAttachmentHolder> holderType, String identifier, Supplier<C1> capabilityFactory, Predicate<Object> filter, NeoForgeCapabilityKey.Factory<T, C1, C2> capabilityKeyFactory) {
        GlobalCapabilityRegister.testHolderType(holderType);
        C2[] capabilityKey = (C2[]) new Object[1];
        DeferredHolder<AttachmentType<?>, AttachmentType<C1>> holder = this.registrar.register(identifier, () -> {
            return AttachmentType.builder(attachmentHolder -> {
                C1 capabilityComponent = capabilityFactory.get();
                capabilityComponent.initialize((CapabilityKey<T, CapabilityComponent<T>>) capabilityKey[0], (T) attachmentHolder);
                return capabilityComponent;
            }).serialize(new IAttachmentSerializer<>() {

                @Override
                public C1 read(IAttachmentHolder holder, Tag tag) {
                    C1 capabilityComponent = capabilityFactory.get();
                    capabilityComponent.initialize((CapabilityKey<T, CapabilityComponent<T>>) capabilityKey[0], (T) holder);
                    capabilityComponent.read((CompoundTag) tag);
                    return capabilityComponent;
                }

                @Override
                public Tag write(C1 object) {
                    return object.toCompoundTag();
                }
            }).build();
        });
        return capabilityKey[0] = capabilityKeyFactory.apply(holder, filter);
    }
}
