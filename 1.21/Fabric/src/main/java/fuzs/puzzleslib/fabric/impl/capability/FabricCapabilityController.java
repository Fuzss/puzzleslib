package fuzs.puzzleslib.fabric.impl.capability;

import com.mojang.serialization.Codec;
import fuzs.puzzleslib.api.capability.v3.CapabilityController;
import fuzs.puzzleslib.api.capability.v3.data.*;
import fuzs.puzzleslib.api.core.v1.utility.NbtSerializableCodec;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import fuzs.puzzleslib.fabric.impl.capability.data.*;
import fuzs.puzzleslib.impl.capability.GlobalCapabilityRegister;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.function.Predicate;
import java.util.function.Supplier;

public final class FabricCapabilityController implements CapabilityController {
    private final String modId;

    public FabricCapabilityController(String modId) {
        this.modId = modId;
    }

    @Override
    public <T extends Entity, C extends CapabilityComponent<T>> EntityCapabilityKey.Mutable<T, C> registerEntityCapability(String identifier, Class<C> capabilityType, Supplier<C> capabilityFactory, Class<T> entityType) {
        return this.registerCapability(Entity.class, identifier, capabilityFactory, entityType::isInstance, (FabricCapabilityKey.Factory<T, C, FabricEntityCapabilityKey<T, C>>) FabricEntityCapabilityKey::new);
    }

    @Override
    public <T extends BlockEntity, C extends CapabilityComponent<T>> BlockEntityCapabilityKey<T, C> registerBlockEntityCapability(String identifier, Class<C> capabilityType, Supplier<C> capabilityFactory, Class<T> blockEntityType) {
        return this.registerCapability(BlockEntity.class, identifier, capabilityFactory, blockEntityType::isInstance, (FabricCapabilityKey.Factory<T, C, FabricBlockEntityCapabilityKey<T, C>>) FabricBlockEntityCapabilityKey::new);
    }

    @Override
    public <C extends CapabilityComponent<LevelChunk>> LevelChunkCapabilityKey<C> registerLevelChunkCapability(String identifier, Class<C> capabilityType, Supplier<C> capabilityFactory) {
        return this.registerCapability(LevelChunk.class, identifier, capabilityFactory, (FabricCapabilityKey.Factory<LevelChunk, C, FabricLevelChunkCapabilityKey<C>>) FabricLevelChunkCapabilityKey::new);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <C extends CapabilityComponent<Level>> LevelCapabilityKey<C> registerLevelCapability(String identifier, Class<C> capabilityType, Supplier<C> capabilityFactory) {
        // AttachmentTarget is not injected into Level, only ServerLevel, via interface injection
        // But the mixin responsible for implementing the interface on all supported classes does in fact target Level, not just ServerLevel
        // This also mirrors the attachment implementation on NeoForge
        return this.registerCapability((Class<? extends AttachmentTarget>) (Class<?>) Level.class, identifier, capabilityFactory, (FabricCapabilityKey.Factory<Level, C, FabricLevelCapabilityKey<C>>) FabricLevelCapabilityKey::new);
    }

    @SuppressWarnings("UnstableApiUsage")
    private <T, C extends CapabilityComponent<T>, K extends CapabilityKey<T, C>> K registerCapability(Class<? extends AttachmentTarget> holderType, String identifier, Supplier<C> capabilityFactory, FabricCapabilityKey.Factory<T, C, K> capabilityKeyFactory) {
        return this.registerCapability(holderType, identifier, capabilityFactory, holderType::isInstance, capabilityKeyFactory);
    }

    @SuppressWarnings("UnstableApiUsage")
    private <T, C extends CapabilityComponent<T>, K extends CapabilityKey<T, C>> K registerCapability(Class<? extends AttachmentTarget> holderType, String identifier, Supplier<C> capabilityFactory, Predicate<Object> filter, FabricCapabilityKey.Factory<T, C, K> capabilityKeyFactory) {
        GlobalCapabilityRegister.testHolderType(holderType);
        Codec<C> codec = new NbtSerializableCodec<>(capabilityFactory);
        ResourceLocation capabilityName = ResourceLocationHelper.fromNamespaceAndPath(this.modId, identifier);
        AttachmentType<C> attachmentType = AttachmentRegistry.<C>builder().persistent(codec).buildAndRegister(capabilityName);
        return capabilityKeyFactory.apply(attachmentType, filter, capabilityFactory);
    }
}
