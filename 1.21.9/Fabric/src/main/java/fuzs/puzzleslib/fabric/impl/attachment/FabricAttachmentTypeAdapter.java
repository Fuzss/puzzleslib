package fuzs.puzzleslib.fabric.impl.attachment;

import fuzs.puzzleslib.impl.attachment.AttachmentTypeAdapter;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("UnstableApiUsage")
public record FabricAttachmentTypeAdapter<T, A>(AttachmentType<A> attachmentType) implements AttachmentTypeAdapter<T, A> {

    @Override
    public ResourceLocation resourceLocation() {
        return this.attachmentType.identifier();
    }

    @Override
    public boolean hasData(T holder) {
        return ((AttachmentTarget) holder).hasAttached(this.attachmentType);
    }

    @Override
    public A getData(T holder) {
        return ((AttachmentTarget) holder).getAttached(this.attachmentType);
    }

    @Override
    public A setData(T holder, A value) {
        return ((AttachmentTarget) holder).setAttached(this.attachmentType, value);
    }

    @Override
    public A removeData(T holder) {
        return ((AttachmentTarget) holder).removeAttached(this.attachmentType);
    }
}
