package fuzs.puzzleslib.fabric.impl.attachment;

import fuzs.puzzleslib.impl.attachment.AttachmentTypeAdapter;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@SuppressWarnings("UnstableApiUsage")
public record FabricAttachmentTypeAdapter<T extends AttachmentTarget, A>(AttachmentType<A> attachmentType) implements AttachmentTypeAdapter<T, A> {

    @Override
    public ResourceLocation resourceLocation() {
        return this.attachmentType.identifier();
    }

    @Override
    public boolean hasData(T holder) {
        Objects.requireNonNull(holder, "holder is null");
        return holder.hasAttached(this.attachmentType);
    }

    @Override
    public @Nullable A getData(T holder) {
        Objects.requireNonNull(holder, "holder is null");
        return holder.getAttached(this.attachmentType);
    }

    @Override
    public void setData(T holder, A value) {
        Objects.requireNonNull(holder, "holder is null");
        Objects.requireNonNull(value, "value is null");
        holder.setAttached(this.attachmentType, value);
    }

    @Override
    public void removeData(T holder) {
        Objects.requireNonNull(holder, "holder is null");
        holder.removeAttached(this.attachmentType);
    }
}
