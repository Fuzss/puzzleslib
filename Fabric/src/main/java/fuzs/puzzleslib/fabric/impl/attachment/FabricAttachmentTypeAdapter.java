package fuzs.puzzleslib.fabric.impl.attachment;

import fuzs.puzzleslib.impl.attachment.AttachmentTypeAdapter;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public record FabricAttachmentTypeAdapter<T extends AttachmentTarget, A>(AttachmentType<A> attachmentType) implements AttachmentTypeAdapter<T, A> {

    @Override
    public ResourceLocation id() {
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
    public @Nullable A setData(T holder, A value) {
        Objects.requireNonNull(holder, "holder is null");
        Objects.requireNonNull(value, "value is null");
        return holder.setAttached(this.attachmentType, value);
    }

    @Override
    public @Nullable A removeData(T holder) {
        Objects.requireNonNull(holder, "holder is null");
        return holder.removeAttached(this.attachmentType);
    }
}
