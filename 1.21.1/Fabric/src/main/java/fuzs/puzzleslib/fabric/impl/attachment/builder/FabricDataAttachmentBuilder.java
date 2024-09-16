package fuzs.puzzleslib.fabric.impl.attachment.builder;

import fuzs.puzzleslib.api.attachment.v4.DataAttachmentType;
import fuzs.puzzleslib.fabric.impl.attachment.FabricAttachmentTypeAdapter;
import fuzs.puzzleslib.impl.attachment.AttachmentTypeAdapter;
import fuzs.puzzleslib.impl.attachment.builder.DataAttachmentBuilder;
import fuzs.puzzleslib.impl.attachment.DataAttachmentTypeImpl;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

import java.util.function.BiConsumer;

/**
 * We do not force the type parameter to extend {@link AttachmentTarget}, as AttachmentTarget is not injected into
 * {@link net.minecraft.world.level.Level}, only {@link net.minecraft.server.level.ServerLevel}, via interface
 * injection. But the mixin responsible for implementing the interface on all supported classes does in fact target
 * {@link net.minecraft.world.level.Level}, not just {@link net.minecraft.server.level.ServerLevel}. This also mirrors
 * the attachment implementation on NeoForge.
 */
@SuppressWarnings("UnstableApiUsage")
public class FabricDataAttachmentBuilder<T, A> extends DataAttachmentBuilder<T, A> {

    @Override
    public DataAttachmentType<T, A> build(ResourceLocation resourceLocation) {
        AttachmentRegistry.Builder<A> builder = AttachmentRegistry.builder();
        this.configureBuilder(builder);
        AttachmentType<A> attachmentType = builder.buildAndRegister(resourceLocation);
        AttachmentTypeAdapter<T, A> adapter = new FabricAttachmentTypeAdapter<>(attachmentType);
        BiConsumer<T, A> synchronizer = this.getSynchronizer(resourceLocation, adapter);
        return new DataAttachmentTypeImpl<>(adapter, this.defaultValues, synchronizer);
    }

    @MustBeInvokedByOverriders
    void configureBuilder(AttachmentRegistry.Builder<A> builder) {
        if (this.codec != null) builder.persistent(this.codec);
    }
}
