package fuzs.puzzleslib.neoforge.impl.attachment;

import fuzs.puzzleslib.impl.attachment.AttachmentTypeAdapter;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public record NeoForgeAttachmentTypeAdapter<T extends IAttachmentHolder, A>(DeferredHolder<AttachmentType<?>, AttachmentType<A>> attachmentType) implements AttachmentTypeAdapter<T, A> {

    @Override
    public ResourceLocation resourceLocation() {
        return this.attachmentType.getKey().location();
    }

    @Override
    public boolean hasData(T holder) {
        Objects.requireNonNull(holder, "holder is null");
        return holder.hasData(this.attachmentType);
    }

    @Override
    public @Nullable A getData(T holder) {
        Objects.requireNonNull(holder, "holder is null");
        return holder.getExistingDataOrNull(this.attachmentType);
    }

    @Override
    public void setData(T holder, A value) {
        Objects.requireNonNull(holder, "holder is null");
        Objects.requireNonNull(value, "value is null");
        holder.setData(this.attachmentType, value);
    }

    @Override
    public void removeData(T holder) {
        Objects.requireNonNull(holder, "holder is null");
        holder.removeData(this.attachmentType);
    }
}
