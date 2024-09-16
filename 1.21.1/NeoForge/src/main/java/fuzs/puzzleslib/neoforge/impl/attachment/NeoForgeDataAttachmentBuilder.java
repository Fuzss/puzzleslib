package fuzs.puzzleslib.neoforge.impl.attachment;

import fuzs.puzzleslib.api.attachment.v4.DataAttachmentType;
import fuzs.puzzleslib.impl.attachment.AttachmentTypeAdapter;
import fuzs.puzzleslib.impl.attachment.DataAttachmentBuilderImpl;
import fuzs.puzzleslib.impl.attachment.DataAttachmentTypeImpl;
import fuzs.puzzleslib.neoforge.api.core.v1.NeoForgeModContainerHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

import java.util.function.BiConsumer;

public class NeoForgeDataAttachmentBuilder<T extends IAttachmentHolder, A> extends DataAttachmentBuilderImpl<T, A> {

    @Override
    public DataAttachmentType<T, A> build(ResourceLocation resourceLocation) {
        DeferredRegister<AttachmentType<?>> registrar = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES,
                resourceLocation.getNamespace()
        );
        NeoForgeModContainerHelper.getOptionalModEventBus(resourceLocation.getNamespace())
                .ifPresent(registrar::register);
        DeferredHolder<AttachmentType<?>, AttachmentType<A>> attachmentType = registrar.register(resourceLocation.getPath(),
                () -> {
                    AttachmentType.Builder<A> builder = AttachmentType.builder(() -> {
                        throw new UnsupportedOperationException("Attachment type " + resourceLocation + " does not support a default value!");
                    });
                    this.configureBuilder(builder);
                    return builder.build();
                }
        );
        AttachmentTypeAdapter<T, A> adapter = new NeoForgeAttachmentTypeAdapter<>(
                attachmentType);
        BiConsumer<T, A> synchronizer = this.getSynchronizer(resourceLocation, adapter);
        return new DataAttachmentTypeImpl<>(adapter, this.defaultValues, synchronizer);
    }

    @MustBeInvokedByOverriders
    void configureBuilder(AttachmentType.Builder<A> builder) {
        if (this.codec != null) {
            builder.serialize(this.codec).copyHandler((A value, IAttachmentHolder holder, HolderLookup.Provider registries) -> {
                return value;
            });
        }
    }
}
