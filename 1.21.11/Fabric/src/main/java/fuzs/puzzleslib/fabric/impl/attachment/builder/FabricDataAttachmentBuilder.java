package fuzs.puzzleslib.fabric.impl.attachment.builder;

import fuzs.puzzleslib.api.attachment.v4.DataAttachmentRegistry;
import fuzs.puzzleslib.api.attachment.v4.DataAttachmentType;
import fuzs.puzzleslib.fabric.impl.attachment.FabricAttachmentTypeAdapter;
import fuzs.puzzleslib.impl.attachment.AttachmentTypeAdapter;
import fuzs.puzzleslib.impl.attachment.DataAttachmentTypeImpl;
import fuzs.puzzleslib.impl.attachment.builder.DataAttachmentBuilder;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

@SuppressWarnings("UnstableApiUsage")
public abstract class FabricDataAttachmentBuilder<T extends AttachmentTarget, V, B extends DataAttachmentRegistry.Builder<T, V, B>> extends DataAttachmentBuilder<T, V, B> {

    @Override
    public DataAttachmentType<T, V> build(ResourceLocation resourceLocation) {
        AttachmentType<V> attachmentType = AttachmentRegistry.create(resourceLocation,
                (AttachmentRegistry.Builder<V> builder) -> {
                    builder.initializer(() -> {
                        // we handle this ourselves later as there is no appropriate context available here
                        throw new UnsupportedOperationException(
                                "Attachment type " + resourceLocation + " does not support a default value!");
                    });
                    this.configureBuilder(builder);
                });
        AttachmentTypeAdapter<T, V> adapter = new FabricAttachmentTypeAdapter<>(attachmentType);
        return new DataAttachmentTypeImpl<>(adapter, this::getRegistryAccess, this.defaultValues);
    }

    @MustBeInvokedByOverriders
    void configureBuilder(AttachmentRegistry.Builder<V> builder) {
        if (this.codec != null) {
            builder.persistent(this.codec);
        }

        if (this.streamCodec != null) {
            builder.syncWith(this.streamCodec, (AttachmentTarget attachmentTarget, ServerPlayer serverPlayer) -> {
                return this.syncWith((T) attachmentTarget, serverPlayer);
            });
        }
    }
}
