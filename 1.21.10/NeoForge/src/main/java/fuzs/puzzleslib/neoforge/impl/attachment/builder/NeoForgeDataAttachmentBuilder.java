package fuzs.puzzleslib.neoforge.impl.attachment.builder;

import fuzs.puzzleslib.api.attachment.v4.DataAttachmentRegistry;
import fuzs.puzzleslib.api.attachment.v4.DataAttachmentType;
import fuzs.puzzleslib.impl.attachment.AttachmentTypeAdapter;
import fuzs.puzzleslib.impl.attachment.DataAttachmentTypeImpl;
import fuzs.puzzleslib.impl.attachment.builder.DataAttachmentBuilder;
import fuzs.puzzleslib.neoforge.api.core.v1.NeoForgeModContainerHelper;
import fuzs.puzzleslib.neoforge.impl.attachment.NeoForgeAttachmentTypeAdapter;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

public abstract class NeoForgeDataAttachmentBuilder<T extends IAttachmentHolder, V, B extends DataAttachmentRegistry.Builder<T, V, B>> extends DataAttachmentBuilder<T, V, B> {

    @Override
    public DataAttachmentType<T, V> build(ResourceLocation resourceLocation) {
        DeferredRegister<AttachmentType<?>> registrar = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES,
                resourceLocation.getNamespace());
        NeoForgeModContainerHelper.getOptionalModEventBus(resourceLocation.getNamespace())
                .ifPresent(registrar::register);
        DeferredHolder<AttachmentType<?>, AttachmentType<V>> attachmentType = registrar.register(resourceLocation.getPath(),
                () -> {
                    AttachmentType.Builder<V> builder = AttachmentType.builder(() -> {
                        // we handle this ourselves later as there is no appropriate context available here
                        throw new UnsupportedOperationException(
                                "Attachment type " + resourceLocation + " does not support a default value!");
                    });
                    this.configureBuilder(resourceLocation, builder);
                    return builder.build();
                });
        AttachmentTypeAdapter<T, V> adapter = new NeoForgeAttachmentTypeAdapter<>(attachmentType);
        return new DataAttachmentTypeImpl<>(adapter, this::getRegistryAccess, this.defaultValues);
    }

    @MustBeInvokedByOverriders
    void configureBuilder(ResourceLocation resourceLocation, AttachmentType.Builder<V> builder) {
        if (this.codec != null) {
            builder.serialize(this.codec.fieldOf(resourceLocation.toString()))
                    .copyHandler((V value, IAttachmentHolder holder, HolderLookup.Provider registries) -> {
                        return value;
                    });
        }

        if (this.streamCodec != null) {
            builder.sync((IAttachmentHolder holder, ServerPlayer serverPlayer) -> {
                return this.syncWith((T) holder, serverPlayer);
            }, this.streamCodec);
        }
    }
}
