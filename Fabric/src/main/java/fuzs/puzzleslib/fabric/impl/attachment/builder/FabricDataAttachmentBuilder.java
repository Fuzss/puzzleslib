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
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

import java.util.function.Function;

/**
 * We do not force the type parameter to extend {@link AttachmentTarget}, as AttachmentTarget is not injected into
 * {@link Level}, only {@link ServerLevel}, via interface injection.
 * <p>
 * But the mixin responsible for implementing the interface on all supported classes does in fact target {@link Level},
 * not just {@link ServerLevel}.
 * <p>
 * This also mirrors the attachment implementation on NeoForge.
 */
@SuppressWarnings("UnstableApiUsage")
public abstract class FabricDataAttachmentBuilder<T, V, B extends DataAttachmentRegistry.Builder<T, V, B>> extends DataAttachmentBuilder<T, V, B> {
    private final Function<T, RegistryAccess> registryAccessExtractor;

    public FabricDataAttachmentBuilder(Function<T, RegistryAccess> registryAccessExtractor) {
        this.registryAccessExtractor = registryAccessExtractor;
    }

    @Override
    public DataAttachmentType<T, V> build(ResourceLocation resourceLocation) {
        AttachmentType<V> attachmentType = AttachmentRegistry.create(resourceLocation, this::configureBuilder);
        AttachmentTypeAdapter<T, V> adapter = new FabricAttachmentTypeAdapter<>(attachmentType);
        return new DataAttachmentTypeImpl<>(adapter, this.registryAccessExtractor, this.defaultValues);
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
